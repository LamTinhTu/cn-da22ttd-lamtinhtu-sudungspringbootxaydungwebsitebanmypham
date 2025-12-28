@echo off
echo.
echo ========================================
echo   FIX: ERR_CONNECTION_REFUSED
echo ========================================
echo.
echo Loi nay xay ra vi BACKEND KHONG CHAY!
echo.
echo Dang khoi dong backend...
echo.

cd /d "%~dp0backend"

echo Khoi dong Spring Boot Backend...
echo Doi 30-60 giay de backend khoi dong hoan toan.
echo.
echo Khi thay dong "Started BackendApplication", backend da san sang!
echo.

start "Backend Server - Ocean Butterfly Shop" cmd /k "mvnw.cmd spring-boot:run"

echo.
echo ========================================
echo   HUONG DAN
echo ========================================
echo.
echo 1. Doi backend khoi dong (30-60 giay)
echo 2. Xem cua so "Backend Server"
echo 3. Tim dong: "Started BackendApplication"
echo 4. Quay lai trinh duyet
echo 5. Nhan Ctrl+F5 de reload trang
echo.
echo Backend URL: http://localhost:5000
echo Employee Dashboard: http://localhost:3000/employee
echo.

pause
