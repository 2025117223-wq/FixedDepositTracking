<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html lang="en">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Create Fixed Deposits - Fixed Deposit Tracking System</title>
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

        /* Page Content */
        .page-content {
            padding: 40px;
            flex: 1;
        }

        /* Form Container */
        .form-container {
            background: white;
            border-radius: 20px;
            padding: 40px;
            box-shadow: 0 2px 8px rgba(0, 0, 0, 0.1);
            border-top: 5px solid #ff8c42;
        }

        .form-grid {
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            gap: 30px;
            margin-bottom: 30px;
        }

        .form-group {
            display: flex;
            flex-direction: column;
        }

        .form-group label {
            font-size: 14px;
            font-weight: 500;
            color: #2c3e50;
            margin-bottom: 8px;
        }

        .form-group input,
        .form-group select,
        .form-group textarea {
            padding: 12px 15px;
            border: 2px solid #d0d0d0;
            border-radius: 8px;
            font-size: 14px;
            font-family: 'Inter', sans-serif;
            transition: all 0.3s ease;
        }

        .form-group input:focus,
        .form-group select:focus,
        .form-group textarea:focus {
            outline: none;
            border-color: #1a4d5e;
        }

        .form-group textarea {
            resize: vertical;
            min-height: 100px;
        }

        .form-group.full-width {
            grid-column: span 3;
        }

        .form-group.half-width {
            grid-column: span 1;
        }

        /* File Upload */
        .file-upload-wrapper {
            display: flex;
            align-items: center;
            gap: 15px;
        }

        .file-upload-btn {
            display: flex;
            align-items: center;
            gap: 10px;
            padding: 12px 20px;
            background: #f8f9fa;
            border: 2px solid #d0d0d0;
            border-radius: 8px;
            cursor: pointer;
            transition: all 0.3s ease;
        }

        .file-upload-btn:hover {
            background: #e9ecef;
            border-color: #1a4d5e;
        }

        .file-upload-btn input[type="file"] {
            display: none;
        }

        .file-icon {
            font-size: 24px;
        }

        .file-text {
            font-size: 14px;
            color: #2c3e50;
        }

        .file-info {
            font-size: 12px;
            color: #7f8c8d;
        }

        /* Calculate Link */
        .calculate-link {
            color: #0066cc;
            text-decoration: underline;
            cursor: pointer;
            font-size: 14px;
            font-weight: 500;
            margin-top: 5px;
            display: inline-block;
        }

        .calculate-link:hover {
            color: #004999;
        }

        /* Toggle Switches */
        .reminder-section {
            display: flex;
            flex-direction: column;
            gap: 20px;
            margin-top: 30px;
            padding-top: 30px;
            border-top: 1px solid #e0e0e0;
        }

        .reminder-item {
            display: flex;
            justify-content: space-between;
            align-items: center;
        }

        .reminder-label {
            font-size: 14px;
            color: #2c3e50;
        }

        /* Toggle Switch */
        .toggle-switch {
            position: relative;
            width: 60px;
            height: 30px;
        }

        .toggle-switch input {
            opacity: 0;
            width: 0;
            height: 0;
        }

        .slider {
            position: absolute;
            cursor: pointer;
            top: 0;
            left: 0;
            right: 0;
            bottom: 0;
            background-color: #ccc;
            transition: .4s;
            border-radius: 30px;
        }

        .slider:before {
            position: absolute;
            content: "";
            height: 22px;
            width: 22px;
            left: 4px;
            bottom: 4px;
            background-color: white;
            transition: .4s;
            border-radius: 50%;
        }

        input:checked + .slider {
            background-color: #4CAF50;
        }

        input:checked + .slider:before {
            transform: translateX(30px);
        }

        /* Action Buttons */
        .form-actions {
            display: flex;
            justify-content: flex-end;
            gap: 15px;
            margin-top: 30px;
        }

        .btn {
            padding: 12px 40px;
            border: none;
            border-radius: 8px;
            font-size: 15px;
            font-weight: 600;
            cursor: pointer;
            transition: all 0.3s ease;
            font-family: 'Inter', sans-serif;
        }

        .btn-primary {
            background: #003f5c;
            color: white;
        }

        .btn-primary:hover {
            background: #002d42;
            transform: translateY(-2px);
            box-shadow: 0 4px 8px rgba(0, 63, 92, 0.3);
        }

        .btn-secondary {
            background: #e0e0e0;
            color: #2c3e50;
        }

        .btn-secondary:hover {
            background: #d0d0d0;
        }

        @media (max-width: 1200px) {
            .form-grid {
                grid-template-columns: repeat(2, 1fr);
            }

            .form-group.full-width {
                grid-column: span 2;
            }
        }

        @media (max-width: 768px) {
            .form-grid {
                grid-template-columns: 1fr;
            }

            .form-group.full-width,
            .form-group.half-width {
                grid-column: span 1;
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

            .page-content {
                padding: 20px;
            }

            .form-container {
                padding: 20px;
            }
        }
        
		/* Notification Message */
		.notification {
		    position: fixed;
		    top: 80px;
		    left: 50%;
		    transform: translateX(-50%);
		    padding: 15px 30px;
		    border-radius: 10px;
		    box-shadow: 0 4px 12px rgba(0, 0, 0, 0.15);
		    font-size: 14px;
		    font-weight: 500;
		    z-index: 1000;
		    color: white;
		    min-width: 300px;
		    text-align: center;
		    margin-left: 100px;
		    margin-top: -50px;
		    opacity: 0;
		    transition: opacity 0.3s ease;
		}
		
		.notification.show {
		    opacity: 1;
		}
		
		.notification.turned-on {
		    background: #80cbc4;
		}
		
		.notification.turned-off {
		    background: #e74c3c;
		    color: #2c3e50;
		}
		
		/* Modal Popup */
		.modal-overlay {
		    display: none;
		    position: fixed;
		    top: 0;
		    left: 0;
		    right: 0;
		    bottom: 0;
		    background: rgba(0, 0, 0, 0.5);
		    z-index: 2000;
		    justify-content: center;
		    align-items: center;
		}
		
		.modal-overlay.show {
		    display: flex;
		}
		
		.modal-content {
		    background: white;
		    border-radius: 20px;
		    padding: 40px;
		    width: 90%;
		    max-width: 500px;
		    position: relative;
		    box-shadow: 0 10px 40px rgba(0, 0, 0, 0.3);
		    animation: modalSlideIn 0.3s ease-out;
		}
		
		@keyframes modalSlideIn {
		    from {
		        transform: translateY(-50px);
		        opacity: 0;
		    }
		    to {
		        transform: translateY(0);
		        opacity: 1;
		    }
		}
		
		.modal-close {
		    position: absolute;
		    top: 20px;
		    right: 20px;
		    font-size: 28px;
		    color: #7f8c8d;
		    cursor: pointer;
		    background: none;
		    border: none;
		    width: 35px;
		    height: 35px;
		    display: flex;
		    align-items: center;
		    justify-content: center;
		    border-radius: 50%;
		    transition: all 0.3s ease;
		}
		
		.modal-close:hover {
		    background: #f0f0f0;
		    color: #2c3e50;
		}
		
		.modal-title {
		    font-size: 2rem;
		    color: #2c3e50;
		    font-weight: 600;
		    text-align: center;
		    margin-bottom: 30px;
		}
		
		.calculation-result {
		    margin-bottom: 20px;
		}
		
		.calculation-result label {
		    display: block;
		    font-size: 14px;
		    color: #2c3e50;
		    margin-bottom: 8px;
		    font-weight: 500;
		}
		
		.calculation-result input {
		    width: 100%;
		    padding: 15px;
		    border: 2px solid #d0d0d0;
		    border-radius: 8px;
		    font-size: 16px;
		    font-family: 'Inter', sans-serif;
		    background: #f8f9fa;
		    color: #2c3e50;
		    font-weight: 600;
		    text-align: center;
		}
		
		.calculation-result input:focus {
		    outline: none;
		    border-color: #1a4d5e;
		}
    </style>
</head>
<body>
    <!-- Include Sidebar -->
    <%@ include file="includes/sidebar.jsp" %>

    <!-- Main Content -->
    <div class="main-content">
        <!-- Header -->
        <div class="header">
            <h1>Create Fixed Deposits</h1>
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

        <!-- Page Content -->
        <div class="page-content">
            <div class="form-container">
                <form id="createFDForm" onsubmit="return false;">
                    <div class="form-grid">
                        <!-- Account Number -->
                        <div class="form-group">
                            <label for="accountNumber">Account Number</label>
                            <input type="text" id="accountNumber" name="accountNumber" required>
                        </div>

                        <!-- Bank Name -->
                        <div class="form-group">
                            <label for="bankName">Bank Name</label>
                            <select id="bankName" name="bankName" required>
                                <option value="">Select Bank</option>
                                <option value="Maybank">Maybank</option>
                                <option value="CIMB">CIMB</option>
                                <option value="MBSB">MBSB</option>
                                <option value="Public Bank">Public Bank</option>
                                <option value="RHB Bank">RHB Bank</option>
                                <option value="Hong Leong Bank">Hong Leong Bank</option>
                                <option value="AmBank">AmBank</option>
                            </select>
                        </div>

                        <!-- Start Date -->
                        <div class="form-group">
                            <label for="startDate">Start Date</label>
                            <input type="date" id="startDate" name="startDate" required>
                        </div>

                        <!-- Referral Number -->
                        <div class="form-group">
                            <label for="referralNumber">Referral Number</label>
                            <input type="text" id="referralNumber" name="referralNumber">
                        </div>

                        <!-- Deposit Amount -->
                        <div class="form-group">
                            <label for="depositAmount">Deposit Amount (RM)</label>
                            <input type="number" id="depositAmount" name="depositAmount" step="0.01" required>
                        </div>

                        <!-- Maturity Date -->
                        <div class="form-group">
                            <label for="maturityDate">Maturity Date</label>
                            <input type="date" id="maturityDate" name="maturityDate" required>
                        </div>

                        <!-- Tenure -->
                        <div class="form-group">
                            <label for="tenure">Tenure(Month)</label>
                            <input type="number" id="tenure" name="tenure" min="1" required>
                        </div>

                        <!-- FD Certificate Upload -->
                        <div class="form-group">
                            <label for="fdCertificate">FD Certificate</label>
                            <div class="file-upload-wrapper">
                                <label for="fdCertificate" class="file-upload-btn">
                                    <span class="file-icon">📁</span>
                                    <span class="file-text">Choose File</span>
                                    <input type="file" id="fdCertificate" name="fdCertificate" accept=".jpg,.jpeg,.png">
                                </label>
                                <span class="file-info">JPEG. , PNG.</span>
                            </div>
                        </div>

                        <!-- Interest Rate -->
                        <div class="form-group">
                            <label for="interestRate">Interest Rate (%)</label>
                            <input type="number" id="interestRate" name="interestRate" step="0.01" required>
                            <a href="#" class="calculate-link" onclick="calculateInterest(); return false;">Calculate</a>
                        </div>
                    </div>

                    <!-- Reminder Section -->
                    <div class="reminder-section">
                        <div class="reminder-item">
                            <span class="reminder-label">Get a reminder 7 days before maturity dates</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="reminderMaturity" id="reminderMaturity">
                                <span class="slider"></span>
                            </label>
                        </div>

                        <div class="reminder-item">
                            <span class="reminder-label">Get a reminder for incomplete FD Details</span>
                            <label class="toggle-switch">
                                <input type="checkbox" name="reminderIncomplete" id="reminderIncomplete">
                                <span class="slider"></span>
                            </label>
                        </div>
                    </div>

                    <!-- Action Buttons -->
                    <div class="form-actions">
                        <button type="button" class="btn btn-secondary" onclick="window.location.href='FDList.jsp'">Cancel</button>
                        <button type="submit" class="btn btn-primary">Next</button>
                    </div>
                </form>
            </div>
        </div>
    </div>
	
	<!-- Calculation Modal -->
	<div class="modal-overlay" id="calculationModal">
	    <div class="modal-content">
	        <button class="modal-close" onclick="closeCalculationModal()">×</button>
	        <h2 class="modal-title">Calculations</h2>
	        
	        <div class="calculation-result">
	            <label>Expected Total Profit (RM)</label>
	            <input type="text" id="totalProfit" readonly>
	        </div>
	        
	        <div class="calculation-result">
	            <label>Expected Total Interest (RM)</label>
	            <input type="text" id="totalInterest" readonly>
	        </div>
	    </div>
	</div>
	
    <script>
        // Show notification function
        function showNotification(message, isTurnedOn) {
            // Remove any existing notification
            const existingNotification = document.querySelector('.notification');
            if (existingNotification) {
                existingNotification.remove();
            }

            // Create notification element
            const notification = document.createElement('div');
            notification.className = 'notification show';
            
            // Add class based on turned on/off state
            if (isTurnedOn) {
                notification.classList.add('turned-on');
            } else {
                notification.classList.add('turned-off');
            }
            
            notification.textContent = message;
            document.body.appendChild(notification);

            // Remove notification after 3 seconds
            setTimeout(() => {
                notification.classList.remove('show');
                setTimeout(() => notification.remove(), 300);
            }, 3000);
        }

        // Toggle reminder handlers
        document.getElementById('reminderMaturity').addEventListener('change', function() {
            if (this.checked) {
                showNotification('Fixed Deposit Maturity Reminder turned on !', true);
            } else {
                showNotification('Fixed Deposit Maturity Reminder turned off !', false);
            }
        });

        document.getElementById('reminderIncomplete').addEventListener('change', function() {
            if (this.checked) {
                showNotification('Incomplete Fixed Deposit Details Reminder turned on !', true);
            } else {
                showNotification('Incomplete Fixed Deposit Details Reminder turned off !', false);
            }
        });

        // File upload display
        document.getElementById('fdCertificate').addEventListener('change', function(e) {
            const fileName = e.target.files[0]?.name || 'Choose File';
            const fileText = this.parentElement.querySelector('.file-text');
            if (fileName !== 'Choose File') {
                fileText.textContent = fileName;
            }
        });

        // Calculate interest and show modal
        function calculateInterest() {
            const depositAmount = parseFloat(document.getElementById('depositAmount').value) || 0;
            const interestRate = parseFloat(document.getElementById('interestRate').value) || 0;
            const tenure = parseFloat(document.getElementById('tenure').value) || 0;

            if (depositAmount && interestRate && tenure) {
                const interest = (depositAmount * interestRate * tenure) / (100 * 12);
                const totalAmount = depositAmount + interest;
                
                // Show modal with calculations
                document.getElementById('totalProfit').value = totalAmount.toFixed(2);
                document.getElementById('totalInterest').value = interest.toFixed(2);
                document.getElementById('calculationModal').classList.add('show');
            } else {
                alert('Please fill in Deposit Amount, Interest Rate, and Tenure to calculate.');
            }
        }

        // Close calculation modal
        function closeCalculationModal() {
            document.getElementById('calculationModal').classList.remove('show');
        }

        // Close modal when clicking outside
        document.getElementById('calculationModal').addEventListener('click', function(e) {
            if (e.target === this) {
                closeCalculationModal();
            }
        });

        // Auto-calculate maturity date based on start date and tenure
        document.getElementById('startDate').addEventListener('change', calculateMaturityDate);
        document.getElementById('tenure').addEventListener('input', calculateMaturityDate);

        function calculateMaturityDate() {
            const startDate = document.getElementById('startDate').value;
            const tenure = parseInt(document.getElementById('tenure').value) || 0;

            if (startDate && tenure) {
                const start = new Date(startDate);
                start.setMonth(start.getMonth() + tenure);
                
                const year = start.getFullYear();
                const month = String(start.getMonth() + 1).padStart(2, '0');
                const day = String(start.getDate()).padStart(2, '0');
                
                document.getElementById('maturityDate').value = `${year}-${month}-${day}`;
            }
        }
        
        // Form validation and submission
        document.getElementById('createFDForm').addEventListener('submit', function(e) {
            e.preventDefault();
            
            // Get form data
            const formData = {
                accountNumber: document.getElementById('accountNumber').value,
                bankName: document.getElementById('bankName').value,
                startDate: document.getElementById('startDate').value,
                referralNumber: document.getElementById('referralNumber').value,
                depositAmount: document.getElementById('depositAmount').value,
                maturityDate: document.getElementById('maturityDate').value,
                tenure: document.getElementById('tenure').value,
                interestRate: document.getElementById('interestRate').value,
                reminderMaturity: document.getElementById('reminderMaturity').checked,
                reminderIncomplete: document.getElementById('reminderIncomplete').checked
            };
            
            // Save to sessionStorage
            sessionStorage.setItem('fdData', JSON.stringify(formData));
            
            // Redirect to Bank Application Form
            window.location.href = 'ApplicationForm.jsp';
        });
    </script>
</body>
</html>
