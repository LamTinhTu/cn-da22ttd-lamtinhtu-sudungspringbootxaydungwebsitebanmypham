# Test Dashboard Connection Script
# Author: Ocean Butterfly Shop Team
# Date: December 25, 2025

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Dashboard Connection Test Tool" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

# Check if backend is running
Write-Host "[1] Checking Backend Server..." -ForegroundColor Yellow
$backendStatus = Test-NetConnection -ComputerName localhost -Port 5000 -InformationLevel Quiet -WarningAction SilentlyContinue

if ($backendStatus) {
    Write-Host "    ✓ Backend is running on port 5000" -ForegroundColor Green
} else {
    Write-Host "    ✗ Backend is NOT running on port 5000" -ForegroundColor Red
    Write-Host "    → Start backend with: cd backend; .\mvnw.cmd spring-boot:run" -ForegroundColor Yellow
    exit 1
}

# Check if frontend is running
Write-Host "[2] Checking Frontend Server..." -ForegroundColor Yellow
$frontendStatus = Test-NetConnection -ComputerName localhost -Port 3000 -InformationLevel Quiet -WarningAction SilentlyContinue

if ($frontendStatus) {
    Write-Host "    ✓ Frontend is running on port 3000" -ForegroundColor Green
} else {
    Write-Host "    ✗ Frontend is NOT running on port 3000" -ForegroundColor Red
    Write-Host "    → Start frontend with: cd frontend; npm start" -ForegroundColor Yellow
    exit 1
}

Write-Host ""
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  URLs to Access" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Frontend Shop:        http://localhost:3000" -ForegroundColor White
Write-Host "Employee Dashboard:   http://localhost:3000/employee" -ForegroundColor White
Write-Host "Admin Dashboard:      http://localhost:3000/admin" -ForegroundColor White
Write-Host "API Test Page:        http://localhost:3000/test-dashboard.html" -ForegroundColor White
Write-Host "Backend API:          http://localhost:5000/api/v1" -ForegroundColor White
Write-Host "Swagger UI:           http://localhost:5000/swagger-ui/index.html" -ForegroundColor White
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Test Instructions" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "1. Login as Employee:" -ForegroundColor Yellow
Write-Host "   - Go to http://localhost:3000" -ForegroundColor White
Write-Host "   - Login with Staff role credentials" -ForegroundColor White
Write-Host "   - Navigate to http://localhost:3000/employee" -ForegroundColor White
Write-Host ""
Write-Host "2. Use API Test Page:" -ForegroundColor Yellow
Write-Host "   - Go to http://localhost:3000/test-dashboard.html" -ForegroundColor White
Write-Host "   - It will auto-load token from localStorage" -ForegroundColor White
Write-Host "   - Click 'Test All' to verify all APIs" -ForegroundColor White
Write-Host ""
Write-Host "3. View Swagger Documentation:" -ForegroundColor Yellow
Write-Host "   - Go to http://localhost:5000/swagger-ui/index.html" -ForegroundColor White
Write-Host "   - Find 'Dashboard' section" -ForegroundColor White
Write-Host "   - Test APIs directly from Swagger" -ForegroundColor White
Write-Host ""

$openBrowser = Read-Host "Do you want to open the test page in browser? (Y/N)"
if ($openBrowser -eq "Y" -or $openBrowser -eq "y") {
    Start-Process "http://localhost:3000/test-dashboard.html"
    Write-Host "✓ Browser opened!" -ForegroundColor Green
}

Write-Host ""
Write-Host "All systems are ready! Happy testing! 🚀" -ForegroundColor Green
Write-Host ""
