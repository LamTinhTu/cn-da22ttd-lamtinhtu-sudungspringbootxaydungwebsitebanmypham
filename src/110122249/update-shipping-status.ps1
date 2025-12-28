# Update database constraint to add SHIPPING status
$env:PGPASSWORD = ""
& "C:\Program Files\pgAdmin 4\runtime\psql.exe" -U postgres -d shopltt_db -c "ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_order_status_check; ALTER TABLE orders ADD CONSTRAINT orders_order_status_check CHECK (order_status IN ('NEW', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED'));"

Write-Host "Database constraint updated successfully!" -ForegroundColor Green
