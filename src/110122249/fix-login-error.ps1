# Fix Login 400 Error - Troubleshooting Guide

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Fix Login 400 Error" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 1 - Testing Login API..." -ForegroundColor Yellow
Write-Host ""

# Test with admin credentials
$adminBody = @{
    userAccount = "admin"
    userPassword = "password"
} | ConvertTo-Json

Write-Host "Testing with admin/password..." -ForegroundColor White

try {
    $response = Invoke-RestMethod -Uri "http://localhost:5000/api/v1/auth/login" `
        -Method POST `
        -ContentType "application/json" `
        -Body $adminBody
    
    Write-Host "✓ SUCCESS! Admin login works!" -ForegroundColor Green
    Write-Host "Access Token: $($response.data.accessToken.Substring(0,20))..." -ForegroundColor Green
    Write-Host "User: $($response.data.userName) - Role: $($response.data.roleName)" -ForegroundColor Green
    Write-Host ""
    
} catch {
    Write-Host "✗ FAILED with status $($_.Exception.Response.StatusCode.value__)" -ForegroundColor Red
    
    if ($_.Exception.Response) {
        $reader = New-Object System.IO.StreamReader($_.Exception.Response.GetResponseStream())
        $reader.BaseStream.Position = 0
        $reader.DiscardBufferedData()
        $errorBody = $reader.ReadToEnd()
        Write-Host "Error details:" -ForegroundColor Red
        Write-Host $errorBody -ForegroundColor Red
    }
    Write-Host ""
}

Write-Host "Step 2 - Checking if DataSeeder ran..." -ForegroundColor Yellow
Write-Host ""
Write-Host "DataSeeder creates default users:" -ForegroundColor White
Write-Host "  - admin/password (Administrator)" -ForegroundColor Cyan
Write-Host "  - staff/password (Staff)" -ForegroundColor Cyan
Write-Host "  - customer1-5/password (Customers)" -ForegroundColor Cyan
Write-Host ""

Write-Host "Step 3 - Solutions" -ForegroundColor Yellow
Write-Host ""
Write-Host "If login failed, try these:" -ForegroundColor White
Write-Host ""
Write-Host "1. Check Backend Logs:" -ForegroundColor Cyan
Write-Host "   Look for 'DATA SEEDER' messages in terminal" -ForegroundColor White
Write-Host ""
Write-Host "2. Restart Backend (to run DataSeeder):" -ForegroundColor Cyan
Write-Host "   cd backend" -ForegroundColor White
Write-Host "   .\mvnw.cmd clean" -ForegroundColor White
Write-Host "   .\mvnw.cmd spring-boot:run" -ForegroundColor White
Write-Host ""
Write-Host "3. Check Database Connection:" -ForegroundColor Cyan
Write-Host "   Verify PostgreSQL is running on localhost:5432" -ForegroundColor White
Write-Host "   Database: shopltt_db" -ForegroundColor White
Write-Host "   Username: postgres" -ForegroundColor White
Write-Host ""
Write-Host "4. Use Test Page:" -ForegroundColor Cyan
Write-Host "   http://localhost:3000/test-login.html" -ForegroundColor White
Write-Host "   This shows detailed error messages" -ForegroundColor White
Write-Host ""

Write-Host "=====================================" -ForegroundColor Cyan
Write-Host "  Common Login Credentials" -ForegroundColor Cyan
Write-Host "=====================================" -ForegroundColor Cyan
Write-Host ""
Write-Host "Administrator:" -ForegroundColor Yellow
Write-Host "  Username: admin" -ForegroundColor White
Write-Host "  Password: password" -ForegroundColor White
Write-Host ""
Write-Host "Staff:" -ForegroundColor Yellow
Write-Host "  Username: staff" -ForegroundColor White
Write-Host "  Password: password" -ForegroundColor White
Write-Host ""
Write-Host "Customer:" -ForegroundColor Yellow
Write-Host "  Username: customer1" -ForegroundColor White
Write-Host "  Password: password" -ForegroundColor White
Write-Host ""
