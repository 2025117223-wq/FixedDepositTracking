<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="Bean.Staff" %>
<%@ page import="java.sql.*" %>
<%@ page import="java.util.*" %>
<%@ page import="java.text.DecimalFormat" %>
<%@ page import="Util.DBConn" %>

<% 
    // Define hasChartData from request
    boolean hasChartData = (Boolean) request.getAttribute("hasChartData");

    // Define DecimalFormat (df)
    DecimalFormat df = new DecimalFormat("#,##0.00");

    // Get jsLabels and jsDatasets from request
    String jsLabels = (String) request.getAttribute("jsLabels");
    String jsDatasets = (String) request.getAttribute("jsDatasets");
%>

<% if (hasChartData) { %>
<script>
    // Chart.js configuration for dynamic chart rendering
    const labels = <%= jsLabels %>;
    const datasets = <%= jsDatasets %>;

    const ctx = document.getElementById('reportChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: datasets
        },
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
                <!-- Total Staff -->
                <div class="stat-card">
                    <h3>Total Staff</h3>
                    <div class="amount">
                        <%= totalStaff > 0 ? totalStaff : "0" %>
                    </div>
                </div>

                <!-- This Month Total Profit -->
                <div class="stat-card">
                    <h3>This Month Total Profit</h3>
                    <div class="amount">
                        <%= thisMonthTotal > 0 ? "RM" + df.format(thisMonthTotal) : "RM 0.00" %>
                    </div>
                </div>

                <!-- Last Month Total Profit -->
                <div class="stat-card">
                    <h3>Last Month Total Profit</h3>
                    <div class="amount">
                        <%= lastMonthTotal > 0 ? "RM" + df.format(lastMonthTotal) : "RM 0.00" %>
                    </div>
                </div>

                <!-- Last Year Total Profit -->
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
    const labels = <%= jsLabels %>;
    const datasets = <%= jsDatasets %>;

    const ctx = document.getElementById('reportChart').getContext('2d');
    new Chart(ctx, {
        type: 'bar',
        data: {
            labels: labels,
            datasets: datasets
        },
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
