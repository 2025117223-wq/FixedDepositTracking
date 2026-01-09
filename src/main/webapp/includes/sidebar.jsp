<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!-- Sidebar Component -->
<style>
    /* Sidebar */
    .sidebar {
        width: 250px;
        background: #003f5c;
        color: white;
        display: flex;
        flex-direction: column;
        padding: 10px 0;
        position: fixed;
        height: 100vh;
        overflow-y: auto;
    }

    .logo-section {
        padding: 10px 20px;
        display: flex;
        align-items: center;
        justify-content: left;
        margin-top:-30px;
        margin-bottom: -20px;
    }

    .logo-icon {
        width: 120px;
        height: 120px;
    }

    .logo-icon img {
        width: 100%;
        height: 100%;
        object-fit: contain;
    }

    .system-text {
        padding: 0 20px 8px;
        border-bottom: 1px solid rgba(255, 255, 255, 0.1);
        margin-bottom: 8px;
    }

    .system-text p {
        font-size: 12px;
        font-weight: 500;
        line-height: 1.3;
    }

    .nav-menu {
        flex: 1;
        padding: 0;
    }

    .nav-item {
        padding: 12px 20px;
        display: flex;
        align-items: center;
        gap: 12px;
        color: white;
        text-decoration: none;
        transition: all 0.3s ease;
        cursor: pointer;
        font-size: 15px;
        position: relative;
    }

    .nav-item:hover {
        background: rgba(255, 255, 255, 0.1);
    }

    .nav-item.active {
        background: rgba(255, 255, 255, 0.15);
        border-left: 4px solid #7dd3c0;
    }

    .nav-icon {
        font-size: 20px;
    }

    .dropdown-icon {
        margin-left: auto;
        font-size: 12px;
        transition: transform 0.3s ease;
    }

    .nav-item.open .dropdown-icon {
        transform: rotate(180deg);
    }

    .sub-menu {
        max-height: 0;
        overflow: hidden;
        transition: max-height 0.3s ease;
        background: rgba(0, 0, 0, 0.1);
    }

    .sub-menu.show {
        max-height: 500px;
    }

    .sub-item {
        padding: 10px 20px 10px 52px;
        display: flex;
        align-items: center;
        gap: 8px;
        color: rgba(255, 255, 255, 0.8);
        text-decoration: none;
        font-size: 14px;
        transition: all 0.3s ease;
        cursor: pointer;
    }

    .sub-item:hover {
        color: white;
        background: rgba(255, 255, 255, 0.05);
    }

    .sub-item.active {
        color: #7dd3c0;
        background: rgba(255, 255, 255, 0.05);
    }

    .sub-item::before {
        content: '●';
        font-size: 8px;
    }

    .divider {
        height: 1px;
        background: rgba(255, 255, 255, 0.1);
        margin: 10px 20px;
    }
</style>

<div class="sidebar">
    <div class="logo-section">
        <div class="logo-icon">
            <img src="${pageContext.request.contextPath}/images/LogoWhite.png" alt="Logo">
        </div>
    </div>
    
    <div class="system-text">
        <p>Fixed Deposits<br>Tracking System</p>
    </div>

    <nav class="nav-menu">
        <a href="${pageContext.request.contextPath}/Dashboard.jsp" class="nav-item <%= request.getRequestURI().contains("Dashboard") ? "active" : "" %>">
            <span class="nav-icon">📊</span>
            <span>Dashboard</span>
        </a>

        <div class="nav-item" id="fdNavItem" onclick="toggleDropdown('fdDropdown', this)">
            <span class="nav-icon">📁</span>
            <span>Fixed Deposits</span>
            <span class="dropdown-icon">▼</span>
        </div>
        <div class="sub-menu" id="fdDropdown">
            <a href="${pageContext.request.contextPath}/FDList.jsp" class="sub-item <%= request.getRequestURI().contains("FDList") ? "active" : "" %>">Fixed Deposit Lists</a>
            <a href="${pageContext.request.contextPath}/CreateFD.jsp" class="sub-item <%= request.getRequestURI().contains("CreateFD") ? "active" : "" %>">Create New FD</a>
            <a href="${pageContext.request.contextPath}/GenerateReport.jsp" class="sub-item <%= request.getRequestURI().contains("GenerateReport") ? "active" : "" %>">Generate Report</a>
        </div>

        <div class="nav-item" id="bankNavItem" onclick="toggleDropdown('bankDropdown', this)">
            <span class="nav-icon">🏦</span>
            <span>Bank</span>
            <span class="dropdown-icon">▼</span>
        </div>
        <div class="sub-menu" id="bankDropdown">
            <a href="${pageContext.request.contextPath}/BankList.jsp" class="sub-item <%= request.getRequestURI().contains("BankList") ? "active" : "" %>">Bank Lists</a>
            <a href="${pageContext.request.contextPath}/CreateBank.jsp" class="sub-item <%= request.getRequestURI().contains("CreateBank") ? "active" : "" %>">Create New Bank</a>
        </div>

        <a href="${pageContext.request.contextPath}/UserList.jsp" class="nav-item <%= request.getRequestURI().contains("UserList") ? "active" : "" %>">
            <span class="nav-icon">👥</span>
            <span>Users</span>
        </a>

        <div class="divider"></div>

        <a href="${pageContext.request.contextPath}/Profile.jsp" class="nav-item <%= request.getRequestURI().contains("Profile") ? "active" : "" %>">
            <span class="nav-icon">👤</span>
            <span>Profile</span>
        </a>

        <a href="${pageContext.request.contextPath}/Login.jsp" class="nav-item">
            <span class="nav-icon">🚪</span>
            <span>Log Out</span>
        </a>
    </nav>
</div>

<script>
    // Toggle dropdown function
    function toggleDropdown(dropdownId, element) {
        const dropdown = document.getElementById(dropdownId);
        const isOpen = dropdown.classList.contains('show');
        
        // Toggle current dropdown
        if (isOpen) {
            dropdown.classList.remove('show');
            element.classList.remove('open');
        } else {
            dropdown.classList.add('show');
            element.classList.add('open');
        }
    }

    // Auto-open dropdowns based on current page
    document.addEventListener('DOMContentLoaded', function() {
        const currentPage = '<%= request.getRequestURI() %>';
        
        // Auto-open Fixed Deposits dropdown ONLY on FD-related pages (NOT on Bank pages)
        if ((currentPage.includes('FDList') || currentPage.includes('FixedDeposit') || currentPage.includes('CreateNewFD') || currentPage.includes('CreateFD') || currentPage.includes('GenerateReport')) 
            && !currentPage.includes('Dashboard') 
            && !currentPage.includes('BankList') 
            && !currentPage.includes('CreateBank')
            & !currentPage.includes('UserList')) {
            const fdDropdown = document.getElementById('fdDropdown');
            const fdNavItem = document.getElementById('fdNavItem');
            
            if (fdDropdown && fdNavItem) {
                fdDropdown.classList.add('show');
                fdNavItem.classList.add('open');
            }
        }
        // Auto-open Bank dropdown ONLY on Bank-related pages (NOT on FD pages)
        else if ((currentPage.includes('BankList') || currentPage.includes('CreateBank'))
            && !currentPage.includes('FDList')
            && !currentPage.includes('CreateFD')
            && !currentPage.includes('GenerateReport')) {
            const bankDropdown = document.getElementById('bankDropdown');
            const bankNavItem = document.getElementById('bankNavItem');
            
            if (bankDropdown && bankNavItem) {
                bankDropdown.classList.add('show');
                bankNavItem.classList.add('open');
            }
        }
    });
</script>
