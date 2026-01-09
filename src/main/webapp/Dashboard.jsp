<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
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
            font-size: 2.2rem;
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
        <div class="header">
            <h1>Dashboard</h1>
            <div class="user-profile">
                <div class="user-info">
                    <div class="user-name">Nor Azlina</div>
                    <div class="user-role">Administrator</div>
                </div>
                <div class="user-avatar">
                    <img src="images/icons/user.jpg" alt="User Avatar" onerror="this.style.display='none'">
                </div>
            </div>
        </div>

        <!-- Dashboard Content -->
        <div class="dashboard-content">
            <div class="content-grid">
                <!-- Report and Analysis -->
                <div class="report-section">
                    <div class="report-header">
                        <h2>Report and Analysis</h2>
                        <div class="sort-dropdown">
                            <span>Sort by</span>
                            <select>
                                <option>This Month</option>
                                <option>Last Month</option>
                                <option>This Year</option>
                            </select>
                        </div>
                    </div>

                    <div class="chart-container">
                        <div class="chart-placeholder">
                            <canvas id="reportChart"></canvas>
                        </div>
                    </div>

                    <div class="legend">
                        <div class="legend-item project-a">Project A</div>
                        <div class="legend-item project-b">Project B</div>
                        <div class="legend-item project-c">Project C</div>
                    </div>
                </div>

                <!-- Stats Cards -->
                <div class="stats-section">
                    <div class="stat-card">
                        <h3>This Month Profits</h3>
                        <div class="amount">RM77,170</div>
                    </div>
                    <div class="stat-card">
                        <h3>Last Month Profits</h3>
                        <div class="amount">RM65,789</div>
                    </div>
                    <div class="stat-card">
                        <h3>Last Year Total Profits</h3>
                        <div class="amount">RM234,567</div>
                    </div>
                </div>
            </div>
        </div>
    </div>

    <script src="https://cdn.jsdelivr.net/npm/chart.js"></script>
    <script>
        // Toggle dropdown function
        function toggleDropdown(dropdownId, element) {
            const dropdown = document.getElementById(dropdownId);
            const isOpen = dropdown.classList.contains('show');
            
            // Toggle current dropdown only
            if (isOpen) {
                dropdown.classList.remove('show');
                element.classList.remove('open');
            } else {
                dropdown.classList.add('show');
                element.classList.add('open');
            }
        }

        // Chart Data
        const ctx = document.getElementById('reportChart').getContext('2d');
        const reportChart = new Chart(ctx, {
            type: 'line',
            data: {
                labels: ['Jan', 'Feb', 'March', 'April', 'May', 'June', 'July', 'Aug', 'Sept', 'Oct', 'Nov', 'Dec'],
                datasets: [
                    {
                        label: 'Project A',
                        data: [15, 10, 38, 33, 35, 28, 30, 45, 48, 30, 50, 55],
                        borderColor: '#7dd3c0',
                        backgroundColor: 'transparent',
                        tension: 0.4,
                        pointRadius: 4,
                        pointBackgroundColor: '#7dd3c0'
                    },
                    {
                        label: 'Project B',
                        data: [13, 8, 20, 15, 40, 35, 42, 48, 52, 60, 65, 68],
                        borderColor: '#0c373f',
                        backgroundColor: 'transparent',
                        tension: 0.4,
                        pointRadius: 4,
                        pointBackgroundColor: '#0c373f'
                    },
                    {
                        label: 'Project C',
                        data: [20, 15, 35, 30, 35, 40, 38, 42, 52, 58, 62, 70],
                        borderColor: '#ff9a5a',
                        backgroundColor: 'transparent',
                        tension: 0.4,
                        pointRadius: 4,
                        pointBackgroundColor: '#ff9a5a'
                    }
                ]
            },
            options: {
                responsive: true,
                maintainAspectRatio: false,
                plugins: {
                    legend: {
                        display: false
                    }
                },
                scales: {
                    y: {
                        beginAtZero: true,
                        max: 70,
                        ticks: {
                            stepSize: 10
                        },
                        grid: {
                            color: '#f0f0f0'
                        }
                    },
                    x: {
                        grid: {
                            display: false
                        }
                    }
                }
            }
        });
    </script>
</body>
</html>