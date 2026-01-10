# Ocean Butterfly Shop - Docker Deployment

## Hướng dẫn triển khai dự án lên Docker

### Yêu cầu
- Docker Desktop đã được cài đặt và đang chạy
- Ít nhất 4GB RAM dành cho Docker
- 10GB dung lượng ổ đĩa trống

### Cấu trúc Docker

Dự án bao gồm 3 services:
1. **postgres** - PostgreSQL Database (Port 5432)
2. **backend** - Spring Boot API (Port 5000)
3. **frontend** - React App với Nginx (Port 80)

### Cách chạy dự án

#### 1. Build và khởi động tất cả services
```bash
docker-compose up -d --build
```

#### 2. Kiểm tra trạng thái containers
```bash
docker-compose ps
```

#### 3. Xem logs
```bash
# Xem logs tất cả services
docker-compose logs -f

# Xem logs của một service cụ thể
docker-compose logs -f backend
docker-compose logs -f frontend
docker-compose logs -f postgres
```

#### 4. Dừng dự án
```bash
docker-compose down
```

#### 5. Dừng và xóa volumes (database data)
```bash
docker-compose down -v
```

### Truy cập ứng dụng

- **Frontend**: http://localhost
- **Backend API**: http://localhost:5000
- **Swagger UI**: http://localhost:5000/swagger-ui.html
- **PostgreSQL**: localhost:5432

### Cấu hình Database

- Database: `shopltt_db`
- Username: `postgres`
- Password: `postgres123`
- Host: `postgres` (trong Docker network) hoặc `localhost` (từ máy host)

### Troubleshooting

#### Backend không kết nối được database
```bash
# Khởi động lại backend sau khi postgres đã sẵn sàng
docker-compose restart backend
```

#### Cần rebuild một service cụ thể
```bash
# Rebuild backend
docker-compose up -d --build backend

# Rebuild frontend
docker-compose up -d --build frontend
```

#### Xóa tất cả và bắt đầu lại từ đầu
```bash
docker-compose down -v
docker-compose up -d --build
```

#### Truy cập vào container để debug
```bash
# Backend
docker exec -it shopltt-backend sh

# Frontend
docker exec -it shopltt-frontend sh

# PostgreSQL
docker exec -it shopltt-postgres psql -U postgres -d shopltt_db
```

### Cập nhật code

Sau khi thay đổi code:
```bash
# Backend
docker-compose up -d --build backend

# Frontend
docker-compose up -d --build frontend
```

### Sao lưu và khôi phục database

#### Sao lưu
```bash
docker exec shopltt-postgres pg_dump -U postgres shopltt_db > backup.sql
```

#### Khôi phục
```bash
docker exec -i shopltt-postgres psql -U postgres shopltt_db < backup.sql
```

### Monitoring

#### Kiểm tra tài nguyên sử dụng
```bash
docker stats
```

#### Kiểm tra dung lượng
```bash
docker system df
```

### Production Deployment

Để deploy lên production, cần:
1. Thay đổi `POSTGRES_PASSWORD` trong docker-compose.yml
2. Thay đổi `JWT_SECRET` sang giá trị mới
3. Cập nhật Twilio credentials
4. Cấu hình SSL/HTTPS cho frontend
5. Sử dụng external database thay vì container
6. Thiết lập backup tự động cho database
