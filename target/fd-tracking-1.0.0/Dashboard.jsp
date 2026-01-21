<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.fd.model.Staff" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.util.Arrays" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.fd.util.DBConnection" %>
<%
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

    // Stats cards (PostgreSQL: COALESCE instead of NVL)
    double thisMonthTotal = 0.0, lastMonthTotal = 0.0, lastYearTotal = 0.0;
    int totalStaff = 0;

    int thisMonth = cal.get(Calendar.MONTH) + 1;
    int lastMonth = thisMonth - 1;
    int lastMonthYear = currentYear;
    if (lastMonth == 0) { lastMonth = 12; lastMonthYear = currentYear - 1; }

    // ✅ Profit formula (NEW): profit = depositamount * interestrt
    String sqlThisMonth =
        "SELECT COALESCE(SUM(depositamount * interestrt),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=? AND EXTRACT(MONTH FROM startdate)=?";

    String sqlLastMonth =
        "SELECT COALESCE(SUM(depositamount * interestrt),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=? AND EXTRACT(MONTH FROM startdate)=?";

    String sqlLastYear =
        "SELECT COALESCE(SUM(depositamount * interestrt),0) AS total " +
        "FROM fixeddepositrecord " +
        "WHERE EXTRACT(YEAR FROM startdate)=?";

    String sqlTotalStaff =
        "SELECT COUNT(*) AS total_staff FROM staff";

    try (Connection con = DBConnection.getConnection()) {

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
                        double profit = rs.getDouble("total_profit");

                        Double[] arr = bankMonthTotals.get(bankName);
                        if (arr == null) {
                            arr = new Double[12];
                            Arrays.fill(arr, null);
                            bankMonthTotals.put(bankName, arr);
                        }
                        if (mth >= 1 && mth <= 12) arr[mth - 1] = profit;
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
                        double profit = rs.getDouble("total_profit");

                        Double[] arr = bankMonthTotals.get(bankName);
                        if (arr == null) {
                            arr = new Double[12];
                            Arrays.fill(arr, null);
                            bankMonthTotals.put(bankName, arr);
                        }
                        if (mth >= 1 && mth <= 12) arr[mth - 1] = profit;
                    }
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
    <style>
        * {
            margin: 0;
            padding: 0;
            box-sizing: border-box;
        }

        body {
            font-family: 'Inter', sans-serif;
            background: #f5f5f5;
            display: flex;
            min-height: 100vh;
        }

        /* Main Content */
        .main-content {
            margin-left: 250px;
            flex: 1;
            display: flex;
            flex-direction: column;
        }

        /* Header */
        .header {
            background: white;
            padding: 20px 40px;
            display: flex;
            justify-content: space-between;
            align-items: center;
            box-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
        }

        .header h1 {
            font-size: 2rem;
            color: #2c3e50;
            font-weight: 600;
        }

        .user-profile {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .user-info {
            text-align: right;
        }

        .user-name {
            font-weight: 600;
            color: #2c3e50;
            font-size: 16px;
        }

        .user-role {
            font-size: 13px;
            color: #7f8c8d;
        }

        .user-avatar {
            width: 50px;
            height: 50px;
            border-radius: 50%;
            background: #d0d0d0;
            display: flex;
            align-items: center;
            justify-content: center;
            cursor: pointer;
            position: relative;
        }

        .user-avatar img {
            width: 100%;
            height: 100%;
            border-radius: 50%;
            object-fit: cover;
        }

        .dropdown-arrow {
            font-size: 12px;
            margin-left: 5px;
        }

        /* Dashboard Content */
        .dashboard-content {
            padding: 40px;
            flex: 1;
        }

        .content-grid {
            display: grid;
            grid-template-columns: 2fr 1fr;
            gap: 30px;
        }

        /* Report Section */
        .report-section {
            background: white;
            border-radius: 20px;
            padding: 30px;
            border: 2px solid #e0e0e0;
        }

        .report-header {
            display: flex;
            justify-content: space-between;
            align-items: center;
            margin-bottom: 30px;
        }

        .report-header h2 {
            font-size: 1.5rem;
            color: #2c3e50;
            font-weight: 600;
        }

        .sort-dropdown {
            display: flex;
            align-items: center;
            gap: 10px;
            color: #7f8c8d;
            font-size: 14px;
        }

        .sort-dropdown select {
            padding: 8px 15px;
            border: 1px solid #d0d0d0;
            border-radius: 8px;
            background: white;
            cursor: pointer;
            font-family: 'Inter', sans-serif;
        }

        .chart-container {
            height: 300px;
            margin-bottom: 30px;
            position: relative;
        }

        .chart-placeholder {
            width: 100%;
            height: 100%;
            display: flex;
            align-items: center;
            justify-content: center;
        }

        .legend {
            display: flex;
            flex-direction: column;
            gap: 15px;
        }

        .legend-item {
            padding: 15px 25px;
            border-radius: 15px;
            font-weight: 500;
            font-size: 15px;
            display: flex;
            align-items: center;
            gap: 10px;
        }

        .legend-item::before {
            content: '';
            width: 12px;
            height: 12px;
            border-radius: 50%;
            border: 2px solid currentColor;
        }

        .legend-item.project-a {
            background: #7dd3c0;
            color: #0c373f;
        }

        .legend-item.project-b {
            background: #0c373f;
            color: white;
        }

        .legend-item.project-c {
            background: #ff9a5a;
            color: white;
        }

        /* Stats Cards */
        .stats-section {
            display: flex;
            flex-direction: column;
            gap: 20px;
        }

        .stat-card {
            background: white;
            border-radius: 20px;
            padding: 30px;
            border: 2px solid #e0e0e0;
            text-align: center;
        }

        .stat-card h3 {
            font-size: 1.1rem;
            color: #2c3e50;
            font-weight: 500;
            margin-bottom: 15px;
        }

        .stat-card .amount {
            font-size: 1.6rem;
            font-weight: 700;
            color: #2c3e50;
        }

        @media (max-width: 1200px) {
            .content-grid {
                grid-template-columns: 1fr;
            }
        }

        @media (max-width: 768px) {
            .sidebar {
                width: 200px;
            }

            .main-content {
                margin-left: 200px;
            }

            .header {
                padding: 15px 20px;
            }

            .header h1 {
                font-size: 1.5rem;
            }

            .dashboard-content {
                padding: 20px;
            }
        }
    </style>
</head>
<body>
    <!-- Sidebar -->
	<%@ include file="includes/sidebar.jsp" %>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <% request.setAttribute("pageTitle", "Dashboard"); %>
		<%@ include file="includes/HeaderInclude.jsp" %>

        <!-- Dashboard Content -->
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