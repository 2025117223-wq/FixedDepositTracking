<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Bean.Staff" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="Util.DBConn" %>

<%!
    // Escape untuk JS string
    private String jsEscape(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"").replace("\n", "\\n").replace("\r", "");
    }

    // HSL -> HEX (warna dynamic ikut index)
    private String hslToHex(float h, float s, float l) {
        // h: 0..360, s & l: 0..1
        float c = (1 - Math.abs(2*l - 1)) * s;
        float hp = h / 60f;
        float x = c * (1 - Math.abs((hp % 2) - 1));

        float r1=0, g1=0, b1=0;
        if (0 <= hp && hp < 1) { r1=c; g1=x; b1=0; }
        else if (1 <= hp && hp < 2) { r1=x; g1=c; b1=0; }
        else if (2 <= hp && hp < 3) { r1=0; g1=c; b1=x; }
        else if (3 <= hp && hp < 4) { r1=0; g1=x; b1=c; }
        else if (4 <= hp && hp < 5) { r1=x; g1=0; b1=c; }
        else if (5 <= hp && hp < 6) { r1=c; g1=0; b1=x; }

        float m = l - c/2f;
        int r = Math.round((r1 + m) * 255);
        int g = Math.round((g1 + m) * 255);
        int b = Math.round((b1 + m) * 255);

        r = Math.max(0, Math.min(255, r));
        g = Math.max(0, Math.min(255, g));
        b = Math.max(0, Math.min(255, b));

        return String.format("#%02x%02x%02x", r, g, b);
    }

    // Bagi warna ikut index bank (dynamic, tak static)
    private String bankColor(int idx, int total) {
        // Hue spread ikut bilangan bank supaya tak clash sangat
        float hue = (total <= 0) ? (idx * 45f) : (idx * (360f / total));
        float sat = 0.65f;
        float light = 0.45f;
        return hslToHex(hue % 360f, sat, light);
    }

    private double nz(Double d) { return (d == null ? 0.0 : d.doubleValue()); }
%>

<%
    // =========================
    // SESSION PROTECTION
    // =========================
    Staff loggedStaff = (Staff) session.getAttribute("loggedStaff");
    if (loggedStaff == null) {
        response.sendRedirect("Login.jsp");
        return;
    }

    String staffName = loggedStaff.getStaffName();
    String staffRole = loggedStaff.getStaffRole();

    // =========================
    // SELECT YEAR (default current year)
    // =========================
    Calendar cal = Calendar.getInstance();
    int currentYear = cal.get(Calendar.YEAR);

    int selectedYear = currentYear;
    try {
        String y = request.getParameter("year");
        if (y != null && !y.trim().isEmpty()) selectedYear = Integer.parseInt(y.trim());
    } catch (Exception ignore) {}

    // =========================
    // BANK FILTER (optional)
    // =========================
    String selectedBank = request.getParameter("bank");
    if (selectedBank == null || selectedBank.trim().isEmpty()) selectedBank = "ALL";
    selectedBank = selectedBank.trim();

    // Dropdown banks
    List<String> bankList = new ArrayList<>();

    // Chart data: bank -> Top3 (index 0..2)
    Map<String, Double[]> bankTop3 = new LinkedHashMap<>();

    String sqlBankList = "SELECT bankname FROM bank ORDER BY bankname";

    // ✅ Profit formula (NEW): profit = depositamount * interestrt
    String sqlChartAll =
        "SELECT b.bankname, " +
        "       EXTRACT(MONTH FROM f.startdate) AS mth, " +
        "       SUM(f.depositamount * f.interestrt) AS total_profit " +
        "FROM bank b " +
        "JOIN fixeddepositrecord f ON f.bankid = b.bankid " +
        "WHERE EXTRACT(YEAR FROM f.startdate) = ? " +
        "GROUP BY b.bankname, EXTRACT(MONTH FROM f.startdate) " +
        "ORDER BY b.bankname, mth";

    String sqlChartOne =
        "SELECT b.bankname, " +
        "       EXTRACT(MONTH FROM f.startdate) AS mth, " +
        "       SUM(f.depositamount * f.interestrt) AS total_profit " +
        "FROM bank b " +
        "JOIN fixeddepositrecord f ON f.bankid = b.bankid " +
        "WHERE EXTRACT(YEAR FROM f.startdate) = ? " +
        "  AND b.bankname = ? " +
        "GROUP BY b.bankname, EXTRACT(MONTH FROM f.startdate) " +
        "ORDER BY b.bankname, mth";

    // =========================
    // Stats cards (PostgreSQL)
    // =========================
    double thisMonthTotal = 0.0, lastMonthTotal = 0.0, lastYearTotal = 0.0;
    int totalStaff = 0;

    int thisMonth = cal.get(Calendar.MONTH) + 1;
    int lastMonth = thisMonth - 1;
    int lastMonthYear = currentYear;
    if (lastMonth == 0) { lastMonth = 12; lastMonthYear = currentYear - 1; }

    // ✅ Profit formula (NEW): profit = depositamount * interestrt
    String sqlThisMonth =
        "SELECT COALESCE(SUM(depositamount),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=? AND EXTRACT(MONTH FROM startdate)=?";

    String sqlLastMonth =
        "SELECT COALESCE(SUM(depositamount),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=? AND EXTRACT(MONTH FROM startdate)=?";

    String sqlLastYear =
        "SELECT COALESCE(SUM(depositamount),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=?";

    String sqlTotalStaff =
        "SELECT COUNT(*) AS total_staff FROM staff";

    try (Connection con = DBConn.getConnection()) {

        // --- Bank dropdown list ---  
        try (PreparedStatement ps = con.prepareStatement(sqlBankList);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) bankList.add(rs.getString("bankname"));
        }

        // --- Top 3 profit data ---
        String sqlToUse = "ALL".equalsIgnoreCase(selectedBank) ? sqlChartAll : sqlChartOne;

        try (PreparedStatement ps = con.prepareStatement(sqlToUse)) {
            ps.setInt(1, selectedYear);
            if (!"ALL".equalsIgnoreCase(selectedBank)) ps.setString(2, selectedBank);

            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    String bankName = rs.getString("bankname");
                    int mth = rs.getInt("mth");
                    double profit = rs.getDouble("total_profit");

                    Double[] arr = bankTop3.get(bankName);
                    if (arr == null) {
                        arr = new Double[12];
                        Arrays.fill(arr, null);
                        bankTop3.put(bankName, arr);
                    }
                    if (mth >= 1 && mth <= 12) arr[mth - 1] = profit;
                }
            }
        }

        // --- This month total profit ---
        try (PreparedStatement ps = con.prepareStatement(sqlThisMonth)) {
            ps.setInt(1, currentYear);
            ps.setInt(2, thisMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) thisMonthTotal = rs.getDouble("total");
            }
        }

        // --- Last month total profit ---
        try (PreparedStatement ps = con.prepareStatement(sqlLastMonth)) {
            ps.setInt(1, lastMonthYear);
            ps.setInt(2, lastMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lastMonthTotal = rs.getDouble("total");
            }
        }

        // --- Last year total profit (previous year) ---
        try (PreparedStatement ps = con.prepareStatement(sqlLastYear)) {
            ps.setInt(1, currentYear - 1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lastYearTotal = rs.getDouble("total");
            }
        }

        // --- Total Staff ---
        try (PreparedStatement ps = con.prepareStatement(sqlTotalStaff);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalStaff = rs.getInt("total_staff");
        }

    } catch (Exception e) {
        e.printStackTrace();
    }

    // =========================
    // SORT BANKS by Top1 profit (desc)
    // =========================
    List<Map.Entry<String, Double[]>> sortedEntries = new ArrayList<>(bankTop3.entrySet());
    Collections.sort(sortedEntries, new Comparator<Map.Entry<String, Double[]>>() {
        public int compare(Map.Entry<String, Double[]> a, Map.Entry<String, Double[]> b) {
            double aTop1 = (a.getValue() == null ? 0 : nz(a.getValue()[0]));
            double bTop1 = (b.getValue() == null ? 0 : nz(b.getValue()[0]));
            return Double.compare(bTop1, aTop1); // DESC
        }
    });

    // rebuild map following sorted order
    Map<String, Double[]> bankTop3Sorted = new LinkedHashMap<>();
    for (Map.Entry<String, Double[]> e : sortedEntries) bankTop3Sorted.put(e.getKey(), e.getValue());
    bankTop3 = bankTop3Sorted;
%>

<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Fixed Deposit Tracking System</title>

    <link rel="preconnect" href="https://fonts.googleapis.com">
    <link rel="preconnect" href="https://fonts.gstatic.com" crossorigin>
    <link href="https://fonts.googleapis.com/css2?family=Inter:wght@400;500;600;700&display=swap" rel="stylesheet">

    <link rel="stylesheet" href="css/Dashboard.css">
</head>

<body>

    <%@ include file="includes/sidebar.jsp" %>

    <div class="main-content">
        <div class="header">
            <h1>Dashboard</h1>

            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name"><%= staffName %></div>
                    <div class="user-role"><%= staffRole %></div>
                </div>

                <div class="user-avatar">
                    <img src="ProfileImagesServlet" alt="User Avatar"
                         onerror="this.src='images/icons/user.jpg'">
                </div>
            </div>
        </div>

        <div class="dashboard-content">
            <div class="content-grid">

                <div class="report-section">
                    <div class="report-header">
                        <h2>Top 3 Fixed Deposit Profit by Bank (Year <%= selectedYear %>)</h2>

                        <div class="sort-dropdown">
                            <form method="get" style="display:flex; gap:10px; align-items:center;">
                                <span>Year</span>
                                <select name="year">
                                    <option value="<%= currentYear %>" <%= (selectedYear==currentYear ? "selected" : "") %>><%= currentYear %></option>
                                    <option value="<%= (currentYear-1) %>" <%= (selectedYear==(currentYear-1) ? "selected" : "") %>><%= (currentYear-1) %></option>
                                    <option value="<%= (currentYear-2) %>" <%= (selectedYear==(currentYear-2) ? "selected" : "") %>><%= (currentYear-2) %></option>
                                </select>

                                <span>Bank</span>
                                <select name="bank">
                                    <option value="ALL" <%= ("ALL".equalsIgnoreCase(selectedBank) ? "selected" : "") %>>All Banks</option>
                                    <% for (String b : bankList) { %>
                                        <option value="<%= b %>" <%= (b.equalsIgnoreCase(selectedBank) ? "selected" : "") %>><%= b %></option>
                                    <% } %>
                                </select>

                                <button type="submit"
                                        style="padding:8px 14px; border:1px solid #d0d0d0; border-radius:8px; background:#fff; cursor:pointer;">
                                    Apply
                                </button>
                            </form>
                        </div>
                    </div>

                    <!-- Chart container ALWAYS shows -->
                    <div class="chart-container" style="height:340px;">
                        <% if (hasChartData) { %>
                            <canvas id="reportChart"></canvas>
                        <% } else { %>
                            <div style="height:100%; display:flex; align-items:center; justify-content:center; color:#7f8c8d; font-size:14px;">
                                No data available for selected year/bank.
                            </div>
                        <% } %>
                    </div>
                </div>

                <div class="stats-section">
                    <!-- ✅ Total Staff moved to TOP -->
                    <div class="stat-card">
                        <h3>Total Staff</h3>
                        <div class="amount">
                            <%= totalStaff > 0 ? totalStaff : "0" %>
                        </div>
                    </div>

                    <div class="stat-card">
                        <h3>This Month Total Profit</h3>
                        <div class="amount">
                            <%= thisMonthTotal > 0 ? "RM" + df.format(thisMonthTotal) : "RM 0.00" %>
                        </div>
                    </div>

                    <div class="stat-card">
                        <h3>Last Month Total Profit</h3>
                        <div class="amount">
                            <%= lastMonthTotal > 0 ? "RM" + df.format(lastMonthTotal) : "RM 0.00" %>
                        </div>
                    </div>

                    <div class="stat-card">
                        <h3>Last Year Total Profit</h3>
                        <div class="amount">
                            <%= lastYearTotal > 0 ? "RM" + df.format(lastYearTotal) : "RM 0.00" %>
                        </div>
                    </div>
                </div>

            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>

    <% if (hasChartData) { %>
    <script>
        const labels = <%= jsLabels.toString() %>;
        const datasets = <%= jsDatasets.toString() %>;

        const ctx = document.getElementById('reportChart').getContext('2d');
        new Chart(ctx, {
            type: 'bar',
            data: { labels, datasets },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: {
                    legend: { display: true },
                    tooltip: {
                        callbacks: {
                            label: (context) => {
                                const v = context.raw;
                                const label = context.dataset.label || '';
                                return `${label}: RM ${Number(v || 0).toLocaleString()}`;
                            }
                        }
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid: { color: '#eeeeee' },
                        ticks: { callback: (val) => 'RM ' + Number(val).toLocaleString() }
                    },
                    x: { grid: { display: false } }
                }
            }
        });
    </script>
    <% } %>

</body>
</html>
