<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Bean.Staff" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="Utill.DBConn" %>

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

    // Chart data: bank -> 12 months (NULL for months without data)
    Map<String, Double[]> bankMonthTotals = new LinkedHashMap<>();

    String sqlBankList = "SELECT bankname FROM bank ORDER BY bankname";

    String sqlChartAll =
        "SELECT b.bankname, " +
        "       EXTRACT(MONTH FROM f.startdate) AS mth, " +
        "       SUM(f.depositamount * f.tenure * f.interestrt) AS total_profit " +  // Added profit calculation
        "FROM bank b " +
        "JOIN fixeddepositrecord f ON f.bankid = b.bankid " +
        "WHERE EXTRACT(YEAR FROM f.startdate) = ? " +
        "GROUP BY b.bankname, EXTRACT(MONTH FROM f.startdate) " +
        "ORDER BY b.bankname, mth";

    String sqlChartOne =
        "SELECT b.bankname, " +
        "       EXTRACT(MONTH FROM f.startdate) AS mth, " +
        "       SUM(f.depositamount * f.tenure * f.interestrt) AS total_profit " +  // Added profit calculation
        "FROM bank b " +
        "JOIN fixeddepositrecord f ON f.bankid = b.bankid " +
        "WHERE EXTRACT(YEAR FROM f.startdate) = ? " +
        "  AND b.bankname = ? " +
        "GROUP BY b.bankname, EXTRACT(MONTH FROM f.startdate) " +
        "ORDER BY b.bankname, mth";

    // Stats cards (PostgreSQL: COALESCE instead of NVL)
    double thisMonthTotal = 0.0, lastMonthTotal = 0.0, lastYearTotal = 0.0;
    int totalStaff = 0;

    int thisMonth = cal.get(Calendar.MONTH) + 1;
    int lastMonth = thisMonth - 1;
    int lastMonthYear = currentYear;
    if (lastMonth == 0) { lastMonth = 12; lastMonthYear = currentYear - 1; }

    String sqlThisMonth =
        "SELECT COALESCE(SUM(depositamount * tenure * interestrt),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=? AND EXTRACT(MONTH FROM startdate)=?";

    String sqlLastMonth =
        "SELECT COALESCE(SUM(depositamount * tenure * interestrt),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=? AND EXTRACT(MONTH FROM startdate)=?";

    String sqlLastYear =
        "SELECT COALESCE(SUM(depositamount * tenure * interestrt),0) AS total " +
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

        // --- Chart data ---
        if ("ALL".equalsIgnoreCase(selectedBank)) {
            try (PreparedStatement ps = con.prepareStatement(sqlChartAll)) {
                ps.setInt(1, selectedYear);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String bankName = rs.getString("bankname");
                        int mth = rs.getInt("mth"); // 1..12
                        double profit = rs.getDouble("total_profit");  // Use profit instead of deposit

                        Double[] arr = bankMonthTotals.get(bankName);
                        if (arr == null) {
                            arr = new Double[12];
                            Arrays.fill(arr, null);
                            bankMonthTotals.put(bankName, arr);
                        }
                        if (mth >= 1 && mth <= 12) arr[mth - 1] = profit;  // Store profit
                    }
                }
            }
        } else {
            try (PreparedStatement ps = con.prepareStatement(sqlChartOne)) {
                ps.setInt(1, selectedYear);
                ps.setString(2, selectedBank);
                try (ResultSet rs = ps.executeQuery()) {
                    while (rs.next()) {
                        String bankName = rs.getString("bankname");
                        int mth = rs.getInt("mth");
                        double profit = rs.getDouble("total_profit");  // Use profit instead of deposit

                        Double[] arr = bankMonthTotals.get(bankName);
                        if (arr == null) {
                            arr = new Double[12];
                            Arrays.fill(arr, null);
                            bankMonthTotals.put(bankName, arr);
                        }
                        if (mth >= 1 && mth <= 12) arr[mth - 1] = profit;  // Store profit
                    }
                }
            }
        }

        // --- This month total deposit ---
        try (PreparedStatement ps = con.prepareStatement(sqlThisMonth)) {
            ps.setInt(1, currentYear);
            ps.setInt(2, thisMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) thisMonthTotal = rs.getDouble("total");
            }
        }

        // --- Last month total deposit ---
        try (PreparedStatement ps = con.prepareStatement(sqlLastMonth)) {
            ps.setInt(1, lastMonthYear);
            ps.setInt(2, lastMonth);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lastMonthTotal = rs.getDouble("total");
            }
        }

        // --- Last year total deposit ---
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
    // Check if we have any chart data
    // =========================
    boolean hasChartData = false;
    for (Double[] arr : bankMonthTotals.values()) {
        if (arr == null) continue;
        for (int i = 0; i < 12; i++) {
            if (arr[i] != null && arr[i] > 0) { hasChartData = true; break; }
        }
        if (hasChartData) break;
    }

    // Start labels at first month with any value (so graph start when data starts)
    int firstIndex = 0;
    if (hasChartData) {
        boolean found = false;
        for (int i = 0; i < 12; i++) {
            for (Double[] arr : bankMonthTotals.values()) {
                if (arr != null && arr[i] != null && arr[i] > 0) {
                    firstIndex = i;
                    found = true;
                    break;
                }
            }
            if (found) break;
        }
    }

    String[] monthLabels = {"Jan","Feb","Mar","Apr","May","Jun","Jul","Aug","Sep","Oct","Nov","Dec"};

    StringBuilder jsLabels = new StringBuilder("[");
    for (int i = firstIndex; i < 12; i++) {
        jsLabels.append("\"").append(monthLabels[i]).append("\"");
        if (i < 11) jsLabels.append(",");
    }
    jsLabels.append("]");

    String[] colors = new String[] {
        "#7dd3c0", "#0c373f", "#ff9a5a", "#6c5ce7", "#0984e3", 
        "#00b894", "#d63031", "#e67e22", "#f39c12", "#2ecc71", 
        "#8e44ad", "#34495e"
    };

    StringBuilder jsDatasets = new StringBuilder("[");

    int colorIdx = 0;
    int appended = 0;

    for (Map.Entry<String, Double[]> entry : bankMonthTotals.entrySet()) {
        String bankName = entry.getKey();
        Double[] arr = entry.getValue();

        boolean allNull = true;
        if (arr != null) {
            for (int i = 0; i < 12; i++) {
                if (arr[i] != null && arr[i] > 0) { allNull = false; break; }
            }
        }
        if (allNull) continue;

        String safeName = (bankName == null ? "Unknown" : bankName)
                .replace("\\", "\\\\")
                .replace("\"", "\\\"");

        String color = colors[colorIdx % colors.length];

        if (appended > 0) jsDatasets.append(",");

        jsDatasets.append("{")
                  .append("label:\"").append(safeName).append("\",")
                  .append("data:[");

        for (int i = firstIndex; i < 12; i++) {
            if (arr == null || arr[i] == null) jsDatasets.append("null");
            else jsDatasets.append(arr[i]);
            if (i < 11) jsDatasets.append(",");
        }

        // TAJAM + bersambung bila bulan tertentu null (spanGaps true)
        jsDatasets.append("],")
                  .append("showLine:true,")
                  .append("spanGaps:true,")
                  .append("borderColor:\"").append(color).append("\",")
                  .append("backgroundColor:\"transparent\",")
                  .append("borderWidth:3,")
                  .append("fill:false,")
                  .append("tension:0,")
                  .append("pointRadius:4,")
                  .append("pointHoverRadius:6,")
                  .append("pointBorderWidth:2,")
                  .append("pointBackgroundColor:\"").append(color).append("\",")
                  .append("pointBorderColor:\"#ffffff\"")
                  .append("}");

        appended++;
        colorIdx++;
    }
    jsDatasets.append("]");

    hasChartData = hasChartData && (appended > 0);

    DecimalFormat df = new DecimalFormat("#,##0.00");

    boolean showThisMonth = thisMonthTotal > 0;
    boolean showLastMonth = lastMonthTotal > 0;
    boolean showLastYear  = lastYearTotal > 0;
    boolean showTotalStaff = totalStaff > 0;
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

    <link rel="stylesheet" href="css/Dashbord.css">
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
                        <h2>Fixed Deposit Profit by Month (Year <%= selectedYear %>)</h2>

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

                    <!-- ✅ Chart container ALWAYS shows, so layout never disappears -->
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
                    <!-- Display stats for this month, even if the value is null or zero -->
                    <div class="stat-card">
                        <h3>This Month Total Profit</h3>
                        <div class="amount">
                            <%= thisMonthTotal > 0 ? "RM" + df.format(thisMonthTotal) : "RM 0.00" %>
                        </div>
                    </div>

                    <!-- Display stats for last month, even if the value is null or zero -->
                    <div class="stat-card">
                        <h3>Last Month Total Profit</h3>
                        <div class="amount">
                            <%= lastMonthTotal > 0 ? "RM" + df.format(lastMonthTotal) : "RM 0.00" %>
                        </div>
                    </div>

                    <!-- Display stats for last year, even if the value is null or zero -->
                    <div class="stat-card">
                        <h3>Last Year Total Profit</h3>
                        <div class="amount">
                            <%= lastYearTotal > 0 ? "RM" + df.format(lastYearTotal) : "RM 0.00" %>
                        </div>
                    </div>

                    <!-- Display stats for total staff -->
                    <div class="stat-card">
                        <h3>Total Staff</h3>
                        <div class="amount">
                            <%= totalStaff > 0 ? totalStaff : "0" %>
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
            type: 'line',
            data: { labels, datasets },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                interaction: { mode: 'index', intersect: false },
                plugins: { legend: { display: false }, tooltip: { enabled: true } },
                scales: {
                    y: {
                        beginAtZero: true,
                        grid
