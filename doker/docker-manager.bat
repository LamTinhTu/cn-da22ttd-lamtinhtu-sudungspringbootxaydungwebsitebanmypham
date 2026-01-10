@echo off
REM Ocean Butterfly Shop - Docker Management Script
REM Tiện ích quản lý Docker containers

echo ====================================
echo Ocean Butterfly Shop - Docker Manager
echo ====================================
echo.

:menu
echo Chon tac vu:
echo 1. Khoi dong tat ca (Start All)
echo 2. Dung tat ca (Stop All)
echo 3. Khoi dong lai (Restart All)
echo 4. Xem trang thai (Status)
echo 5. Xem logs Backend
echo 6. Xem logs Frontend
echo 7. Xem logs Database
echo 8. Rebuild Backend
echo 9. Rebuild Frontend
echo 10. Rebuild tat ca (Rebuild All)
echo 11. Xoa tat ca va bat dau lai (Clean & Restart)
echo 0. Thoat (Exit)
echo.

set /p choice="Nhap lua chon (0-11): "

if "%choice%"=="1" goto start_all
if "%choice%"=="2" goto stop_all
if "%choice%"=="3" goto restart_all
if "%choice%"=="4" goto status
if "%choice%"=="5" goto logs_backend
if "%choice%"=="6" goto logs_frontend
if "%choice%"=="7" goto logs_postgres
if "%choice%"=="8" goto rebuild_backend
if "%choice%"=="9" goto rebuild_frontend
if "%choice%"=="10" goto rebuild_all
if "%choice%"=="11" goto clean_restart
if "%choice%"=="0" goto end

echo Lua chon khong hop le!
goto menu

:start_all
echo.
echo Dang khoi dong tat ca containers...
docker-compose up -d
goto wait_continue

:stop_all
echo.
echo Dang dung tat ca containers...
docker-compose down
goto wait_continue

:restart_all
echo.
echo Dang khoi dong lai tat ca containers...
docker-compose restart
goto wait_continue

:status
echo.
echo Trang thai containers:
docker-compose ps
echo.
echo Tai nguyen su dung:
docker stats --no-stream
goto wait_continue

:logs_backend
echo.
echo Logs Backend (Nhan Ctrl+C de thoat):
docker-compose logs -f backend
goto menu

:logs_frontend
echo.
echo Logs Frontend (Nhan Ctrl+C de thoat):
docker-compose logs -f frontend
goto menu

:logs_postgres
echo.
echo Logs PostgreSQL (Nhan Ctrl+C de thoat):
docker-compose logs -f postgres
goto menu

:rebuild_backend
echo.
echo Dang rebuild Backend...
docker-compose build --no-cache backend
docker-compose up -d backend
goto wait_continue

:rebuild_frontend
echo.
echo Dang rebuild Frontend...
docker-compose build --no-cache frontend
docker-compose up -d frontend
goto wait_continue

:rebuild_all
echo.
echo Dang rebuild tat ca...
docker-compose down
docker-compose build --no-cache
docker-compose up -d
goto wait_continue

:clean_restart
echo.
echo CANH BAO: Thao tac nay se xoa tat ca data trong database!
set /p confirm="Ban co chac chan? (y/n): "
if /i not "%confirm%"=="y" goto menu
echo Dang xoa va khoi dong lai...
docker-compose down -v
docker-compose build --no-cache
docker-compose up -d
goto wait_continue

:wait_continue
echo.
pause
cls
goto menu

:end
echo.
echo Tam biet!
exit /b
