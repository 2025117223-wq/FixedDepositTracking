<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.*,java.sql.*,com.fd.dao.FixedDepositDAO,com.fd.model.Bank" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="com.fd.util.DBConnection" %>

<%
    // Initialize necessary variables
    FixedDepositDAO fdDAO = new FixedDepositDAO();
    DecimalFormat df = new DecimalFormat("#,##0.00");
    
    Calendar cal = Calendar.getInstance();
    int currentYear = cal.get(Calendar.YEAR);
    int selectedYear = currentYear;
    
    // Get the selected year from request, default to current year if none provided
    try {
        String yearParam = request.getParameter("year");
        if (yearParam != null && !yearParam.trim().isEmpty()) {
            selectedYear = Integer.parseInt(yearParam.trim());
        }
    } catch (Exception ignore) {}

    // Initialize necessary data for bank filters and chart data
    String selectedBank = request.getParameter("bank");
    if (selectedBank == null || selectedBank.trim().isEmpty()) {
        selectedBank = "ALL";
    }

    List<String> bankList = new ArrayList<>();
    Map<String, Double[]> bankMonthTotals = new LinkedHashMap<>();
    double thisMonthTotal = 0.0, lastMonthTotal = 0.0, lastYearTotal = 0.0;
    int totalStaff = 0;

    // SQL Queries for chart data and stats
    String sqlBankList = "SELECT bankname FROM bank ORDER BY bankname";
    String sqlChartAll = "SELECT b.bankname, EXTRACT(MONTH FROM f.startdate) AS mth, " +
                         "SUM(f.depositamount * f.interestrt) AS total_profit " +
                         "FROM bank b " +
                         "JOIN fixeddepositrecord f ON f.bankid = b.bankid " +
                         "WHERE EXTRACT(YEAR FROM f.startdate) = ? " +
                         "GROUP BY b.bankname, EXTRACT(MONTH FROM f.startdate) " +
                         "ORDER BY b.bankname, mth";
    String sqlChartOne = "SELECT b.bankname, EXTRACT(MONTH FROM f.startdate) AS mth, " +
                         "SUM(f.depositamount * f.interestrt) AS total_profit " +
                         "FROM bank b " +
                         "JOIN fixeddepositrecord f ON f.bankid = b.bankid " +
                         "WHERE EXTRACT(YEAR FROM f.startdate) = ? AND b.bankname = ? " +
                         "GROUP BY b.bankname, EXTRACT(MONTH FROM f.startdate) " +
                         "ORDER BY b.bankname, mth";
    String sqlThisMonth = "SELECT COALESCE(SUM(depositamount * interestrt),0) AS total " +
                          "FROM fixeddepositrecord WHERE EXTRACT(YEAR FROM startdate) = ? " +
                          "AND EXTRACT(MONTH FROM startdate) = ?";
    String sqlLastMonth = "SELECT COALESCE(SUM(depositamount * interestrt),0) AS total " +
                          "FROM fixeddepositrecord WHERE EXTRACT(YEAR FROM startdate) = ? " +
                          "AND EXTRACT(MONTH FROM startdate) = ?";
    String sqlLastYear = "SELECT COALESCE(SUM(depositamount * interestrt),0) AS total " +
                         "FROM fixeddepositrecord WHERE EXTRACT(YEAR FROM startdate) = ?";
    String sqlTotalStaff = "SELECT COUNT(*) AS total_staff FROM staff";

    try (Connection con = DBConnection.getConnection()) {
        // Retrieve the list of banks for the dropdown
        try (PreparedStatement ps = con.prepareStatement(sqlBankList);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                bankList.add(rs.getString("bankname"));
            }
        }

        // Retrieve the chart data for all banks or a specific one
        if ("ALL".equalsIgnoreCase(selectedBank)) {
            try (PreparedStatement ps = con.prepareStatement(sqlChartAll)) {
                ps.setInt(1, selectedYear);
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

        // Retrieve totals for this month, last month, and last year
        try (PreparedStatement ps = con.prepareStatement(sqlThisMonth)) {
            ps.setInt(1, currentYear);
            ps.setInt(2, cal.get(Calendar.MONTH) + 1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) thisMonthTotal = rs.getDouble("total");
            }
        }

        try (PreparedStatement ps = con.prepareStatement(sqlLastMonth)) {
            ps.setInt(1, currentYear);
            ps.setInt(2, (cal.get(Calendar.MONTH) == 0) ? 12 : cal.get(Calendar.MONTH));
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lastMonthTotal = rs.getDouble("total");
            }
        }

        try (PreparedStatement ps = con.prepareStatement(sqlLastYear)) {
            ps.setInt(1, currentYear - 1);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) lastYearTotal = rs.getDouble("total");
            }
        }

        try (PreparedStatement ps = con.prepareStatement(sqlTotalStaff);
             ResultSet rs = ps.executeQuery()) {
            if (rs.next()) totalStaff = rs.getInt("total_staff");
        }
    } catch (Exception e) {
        e.printStackTrace();
    }

    // Check if we have chart data
    boolean hasChartData = false;
    for (Double[] arr : bankMonthTotals.values()) {
        if (arr != null) {
            for (Double val : arr) {
                if (val != null && val > 0) {
                    hasChartData = true;
                    break;
                }
            }
        }
    }

    // Prepare labels and datasets for chart.js
    StringBuilder jsLabels = new StringBuilder("[");
    String[] monthLabels = {"Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", "Nov", "Dec"};
    for (int i = 0; i < monthLabels.length; i++) {
        jsLabels.append("\"").append(monthLabels[i]).append("\"");
        if (i < monthLabels.length - 1) jsLabels.append(",");
    }
    jsLabels.append("]");

    StringBuilder jsDatasets = new StringBuilder("[");
    int colorIdx = 0;
    String[] colors = {"#7dd3c0", "#0c373f", "#ff9a5a", "#6c5ce7", "#0984e3"};
    for (Map.Entry<String, Double[]> entry : bankMonthTotals.entrySet()) {
        String bankName = entry.getKey();
        Double[] arr = entry.getValue();

        String safeName = bankName != null ? bankName.replace("\"", "\\\"") : "Unknown";
        String color = colors[colorIdx % colors.length];

        jsDatasets.append("{")
                  .append("label:\"").append(safeName).append("\",")
                  .append("data:[");

        for (int i = 0; i < arr.length; i++) {
            jsDatasets.append(arr[i] != null ? arr[i] : "null");
            if (i < arr.length - 1) jsDatasets.append(",");
        }

        jsDatasets.append("],")
                  .append("borderColor:\"").append(color).append("\",")
                  .append("backgroundColor:\"transparent\",")
                  .append("borderWidth:3,")
                  .append("fill:false,")
                  .append("tension:0}")
                  .append("}");
        colorIdx++;
    }
    jsDatasets.append("]");

%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Dashboard - Fixed Deposit Tracking System</title>
    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
</head>
<body>
    <!-- Your Dashboard Header, Sidebar and Content sections go here -->
    
    <div>
        <h2>Fixed Deposit Profit by Month (Year <%= selectedYear %>)</h2>
        <form method="get" style="display: flex;">
            <label for="year">Year: </label>
            <select name="year" id="year">
                <option value="<%= currentYear %>" <%= (selectedYear == currentYear ? "selected" : "") %>><%= currentYear %></option>
                <option value="<%= currentYear-1 %>" <%= (selectedYear == currentYear-1 ? "selected" : "") %>><%= currentYear-1 %></option>
                <option value="<%= currentYear-2 %>" <%= (selectedYear == currentYear-2 ? "selected" : "") %>><%= currentYear-2 %></option>
            </select>
            <label for="bank">Bank: </label>
            <select name="bank" id="bank">
                <option value="ALL" <%= "ALL".equalsIgnoreCase(selectedBank) ? "selected" : "" %>>All Banks</option>
                <% for (String b : bankList) { %>
                    <option value="<%= b %>" <%= (b.equalsIgnoreCase(selectedBank) ? "selected" : "") %>><%= b %></option>
                <% } %>
            </select>
            <button type="submit">Apply</button>
        </form>
        
        <div>
            <% if (hasChartData) { %>
                <canvas id="fdProfitChart"></canvas>
            <% } else { %>
                <p>No data available for selected year and bank.</p>
            <% } %>
        </div>

        <script>
            const ctx = document.getElementById('fdProfitChart').getContext('2d');
            const chartData = {
                labels: <%= jsLabels.toString() %>,
                datasets: <%= jsDatasets.toString() %>
            };

            const chartConfig = {
                type: 'line',
                data: chartData,
                options: {
                    responsive: true,
                    plugins: {
                        legend: {
                            display: true
                        },
                        tooltip: {
                            enabled: true
                        }
                    },
                    scales: {
                        y: {
                            beginAtZero: true,
                            ticks: {
                                callback: (value) => 'RM ' + value.toLocaleString()
                            }
                        }
                    }
                }
            };

            new Chart(ctx, chartConfig);
        </script>
    </div>
</body>
</html>
