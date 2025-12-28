@echo off
echo ========================================
echo   Restart Backend to Run DataSeeder
echo ========================================
echo.

echo [1] Stopping any running Java processes...
taskkill /F /IM java.exe 2>nul
timeout /t 2 /nobreak >nul
echo.

echo [2] Cleaning build artifacts...
cd /d "%~dp0backend"
call mvnw.cmd clean
echo.

echo [3] Starting backend with dev profile...
echo This will run DataSeeder to create default users:
echo   - admin/password (Administrator)
echo   - staff/password (Staff)
echo   - customer1-5/password (Customers)
echo.

start "Backend Server" cmd /k "mvnw.cmd spring-boot:run -Dspring-boot.run.profiles=dev"

echo.
echo ========================================
echo Backend is starting in a new window...
echo Wait 30-60 seconds for it to fully start
echo Look for "DATA SEEDER" messages in the log
echo ========================================
echo.

pause
