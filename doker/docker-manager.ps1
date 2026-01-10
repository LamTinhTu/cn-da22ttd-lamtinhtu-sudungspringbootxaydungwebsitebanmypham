# Ocean Butterfly Shop - Docker Management PowerShell Script
# Tiện ích quản lý Docker containers

function Show-Menu {
    Clear-Host
    Write-Host "====================================" -ForegroundColor Cyan
    Write-Host "Ocean Butterfly Shop - Docker Manager" -ForegroundColor Cyan
    Write-Host "====================================" -ForegroundColor Cyan
    Write-Host ""
    Write-Host "1.  Khởi động tất cả (Start All)" -ForegroundColor Green
    Write-Host "2.  Dừng tất cả (Stop All)" -ForegroundColor Yellow
    Write-Host "3.  Khởi động lại (Restart All)" -ForegroundColor Yellow
    Write-Host "4.  Xem trạng thái (Status)" -ForegroundColor White
    Write-Host "5.  Xem logs Backend" -ForegroundColor White
    Write-Host "6.  Xem logs Frontend" -ForegroundColor White
    Write-Host "7.  Xem logs Database" -ForegroundColor White
    Write-Host "8.  Rebuild Backend" -ForegroundColor Magenta
    Write-Host "9.  Rebuild Frontend" -ForegroundColor Magenta
    Write-Host "10. Rebuild tất cả (Rebuild All)" -ForegroundColor Magenta
    Write-Host "11. Xóa tất cả và bắt đầu lại (Clean & Restart)" -ForegroundColor Red
    Write-Host "12. Mở Website" -ForegroundColor Cyan
    Write-Host "13. Mở Swagger UI" -ForegroundColor Cyan
    Write-Host "0.  Thoát (Exit)" -ForegroundColor Gray
    Write-Host ""
}

function Start-All {
    Write-Host "`nĐang khởi động tất cả containers..." -ForegroundColor Green
    docker-compose up -d
    Write-Host "Hoàn tất!" -ForegroundColor Green
}

function Stop-All {
    Write-Host "`nĐang dừng tất cả containers..." -ForegroundColor Yellow
    docker-compose down
    Write-Host "Hoàn tất!" -ForegroundColor Yellow
}

function Restart-All {
    Write-Host "`nĐang khởi động lại tất cả containers..." -ForegroundColor Yellow
    docker-compose restart
    Write-Host "Hoàn tất!" -ForegroundColor Yellow
}

function Show-Status {
    Write-Host "`nTrạng thái containers:" -ForegroundColor Cyan
    docker-compose ps
    Write-Host "`nTài nguyên sử dụng:" -ForegroundColor Cyan
    docker stats --no-stream
}

function Show-Logs {
    param([string]$Service)
    Write-Host "`nLogs $Service (Nhấn Ctrl+C để thoát):" -ForegroundColor Cyan
    docker-compose logs -f $Service
}

function Rebuild-Service {
    param([string]$Service)
    Write-Host "`nĐang rebuild $Service..." -ForegroundColor Magenta
    docker-compose build --no-cache $Service
    docker-compose up -d $Service
    Write-Host "Hoàn tất!" -ForegroundColor Magenta
}

function Rebuild-All {
    Write-Host "`nĐang rebuild tất cả..." -ForegroundColor Magenta
    docker-compose down
    docker-compose build --no-cache
    docker-compose up -d
    Write-Host "Hoàn tất!" -ForegroundColor Magenta
}

function Clean-Restart {
    Write-Host "`nCẢNH BÁO: Thao tác này sẽ xóa tất cả data trong database!" -ForegroundColor Red
    $confirm = Read-Host "Bạn có chắc chắn? (y/n)"
    if ($confirm -eq 'y' -or $confirm -eq 'Y') {
        Write-Host "Đang xóa và khởi động lại..." -ForegroundColor Red
        docker-compose down -v
        docker-compose build --no-cache
        docker-compose up -d
        Write-Host "Hoàn tất!" -ForegroundColor Red
    } else {
        Write-Host "Đã hủy." -ForegroundColor Yellow
    }
}

function Open-Website {
    Write-Host "`nĐang mở website..." -ForegroundColor Cyan
    Start-Process "http://localhost"
}

function Open-Swagger {
    Write-Host "`nĐang mở Swagger UI..." -ForegroundColor Cyan
    Start-Process "http://localhost:5000/swagger-ui.html"
}

# Main loop
do {
    Show-Menu
    $choice = Read-Host "Nhập lựa chọn (0-13)"
    
    switch ($choice) {
        '1' { Start-All }
        '2' { Stop-All }
        '3' { Restart-All }
        '4' { Show-Status }
        '5' { Show-Logs -Service "backend" }
        '6' { Show-Logs -Service "frontend" }
        '7' { Show-Logs -Service "postgres" }
        '8' { Rebuild-Service -Service "backend" }
        '9' { Rebuild-Service -Service "frontend" }
        '10' { Rebuild-All }
        '11' { Clean-Restart }
        '12' { Open-Website }
        '13' { Open-Swagger }
        '0' { 
            Write-Host "`nTạm biệt!" -ForegroundColor Cyan
            return 
        }
        default { 
            Write-Host "`nLựa chọn không hợp lệ!" -ForegroundColor Red
        }
    }
    
    if ($choice -ne '0') {
        Write-Host ""
        Read-Host "Nhấn Enter để tiếp tục"
    }
} while ($choice -ne '0')
