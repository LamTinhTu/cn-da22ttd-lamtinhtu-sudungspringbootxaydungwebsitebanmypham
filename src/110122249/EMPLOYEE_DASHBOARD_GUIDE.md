# 📊 Hướng dẫn sử dụng Dashboard Nhân viên

## 🎯 Tổng quan

Dashboard Nhân viên được thiết kế để giúp nhân viên cửa hàng theo dõi và quản lý công việc hàng ngày một cách hiệu quả.

## 🔐 Đăng nhập

1. Truy cập: `http://localhost:3000`
2. Đăng nhập với tài khoản có role **Staff**
3. Sau khi đăng nhập thành công, truy cập: `http://localhost:3000/employee`

## 📈 Các tính năng Dashboard

### 1. Thống kê Tổng quan (Dashboard Stats)

Dashboard hiển thị 6 chỉ số quan trọng:

#### 💰 Tổng Doanh Thu
- Hiển thị tổng doanh thu của cửa hàng
- Xu hướng so với tháng trước (↑ tăng / ↓ giảm)

#### 🛍️ Tổng Đơn Hàng
- Tổng số đơn hàng đã xử lý
- Xu hướng so với tháng trước

#### 📦 Đơn Hàng Mới
- Số đơn hàng mới cần xử lý
- Không có xu hướng (real-time data)

#### 👥 Tổng Khách Hàng
- Tổng số khách hàng đã đăng ký
- Xu hướng tăng trưởng khách hàng

#### 📱 Tổng Sản Phẩm
- Số lượng sản phẩm trong hệ thống
- Xu hướng thêm/bớt sản phẩm

#### ⚠️ Sản Phẩm Sắp Hết
- Cảnh báo sản phẩm có tồn kho thấp
- Cần nhập hàng hoặc kiểm tra

### 2. Đơn Hàng Gần Đây

Hiển thị 5 đơn hàng gần nhất với thông tin:
- **Mã đơn**: Mã định danh đơn hàng
- **Khách hàng**: Tên người đặt hàng
- **Ngày đặt**: Thời gian đặt hàng
- **Tổng tiền**: Giá trị đơn hàng
- **Trạng thái**: Tình trạng xử lý

#### Các trạng thái đơn hàng:
- 🟡 **Chờ xử lý** (PENDING) - Đơn hàng mới, chưa xác nhận
- 🔵 **Đang xử lý** (PROCESSING) - Đang chuẩn bị hàng
- 🟣 **Đã xác nhận** (CONFIRMED) - Đã xác nhận với khách
- 🔷 **Đang giao** (SHIPPING) - Đang vận chuyển
- 🟢 **Đã giao** (DELIVERED) - Giao hàng thành công
- 🔴 **Đã hủy** (CANCELLED) - Đơn hàng bị hủy
- ⚪ **Hoàn trả** (RETURNED) - Khách hàng hoàn trả

### 3. Sản Phẩm Bán Chạy

Hiển thị 5 sản phẩm bán chạy nhất với:
- **Hình ảnh**: Ảnh sản phẩm
- **Tên sản phẩm**: Tên đầy đủ
- **Mã sản phẩm**: Mã SKU
- **Giá**: Giá bán hiện tại
- **Đã bán**: Tổng số lượng đã bán

## 🔄 Làm mới dữ liệu

- Dashboard tự động tải dữ liệu khi vào trang
- Nếu có lỗi, click nút **"Thử lại"** để làm mới
- Reload trang (F5) để cập nhật dữ liệu mới nhất

## ❌ Xử lý lỗi

### Lỗi "Không thể tải dữ liệu"
**Nguyên nhân**: Kết nối backend bị lỗi
**Giải pháp**: 
1. Kiểm tra backend có đang chạy không
2. Click "Thử lại" 
3. Nếu vẫn lỗi, liên hệ IT

### Lỗi "Phiên đăng nhập đã hết hạn"
**Nguyên nhân**: Token đã hết hạn
**Giải pháp**: 
1. Đăng xuất
2. Đăng nhập lại

## 🔧 Kiểm tra kết nối

Sử dụng công cụ test:

### Cách 1: Dùng PowerShell Script
```powershell
cd D:\110122249
.\test-dashboard-connection.ps1
```

### Cách 2: Dùng Test Page
1. Truy cập: `http://localhost:3000/test-dashboard.html`
2. Token sẽ tự động load từ localStorage
3. Click **"Test All"** để kiểm tra tất cả API

### Cách 3: Dùng Swagger UI
1. Truy cập: `http://localhost:5000/swagger-ui/index.html`
2. Tìm section **"3. Dashboard"**
3. Test từng endpoint:
   - `GET /api/v1/dashboard/stats`
   - `GET /api/v1/dashboard/recent-orders`
   - `GET /api/v1/dashboard/top-products`

## 📡 API Endpoints

Dashboard sử dụng 3 API chính:

### 1. Dashboard Stats
```
GET /api/v1/dashboard/stats
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": {
    "totalRevenue": 50000000,
    "totalOrders": 150,
    "newOrders": 12,
    "totalCustomers": 320,
    "totalProducts": 85,
    "lowStockProducts": 5,
    "revenueTrend": 8.5,
    "ordersTrend": 5.2,
    "customersTrend": 12.3,
    "productsTrend": 2.1
  }
}
```

### 2. Recent Orders
```
GET /api/v1/dashboard/recent-orders?limit=5
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "orderId": 1,
      "orderCode": "ORD001",
      "customerName": "Nguyen Van A",
      "orderDate": "2025-12-25",
      "totalAmount": 500000,
      "orderStatus": "PENDING"
    }
  ]
}
```

### 3. Top Products
```
GET /api/v1/dashboard/top-products?limit=5
Authorization: Bearer {token}
```

**Response:**
```json
{
  "success": true,
  "data": [
    {
      "productId": 1,
      "productCode": "PRD001",
      "productName": "Son Dưỡng",
      "productPrice": 150000,
      "totalSold": 120,
      "imageUrl": "/uploads/product1.jpg"
    }
  ]
}
```

## 🎨 Giao diện

### Màu sắc
- **Xanh lá** (💰): Doanh thu
- **Xanh dương** (🛍️): Đơn hàng
- **Cam** (📦): Đơn mới
- **Tím** (👥): Khách hàng
- **Xanh đậm** (📱): Sản phẩm
- **Đỏ** (⚠️): Cảnh báo

### Responsive
- **Mobile**: 1 cột
- **Tablet**: 2 cột
- **Desktop**: 3 cột

## 💡 Tips & Tricks

1. **Theo dõi đơn mới**: Số "Đơn hàng mới" cho biết có bao nhiêu đơn cần xử lý
2. **Cảnh báo tồn kho**: Chú ý số "Sản phẩm sắp hết" để kịp thời nhập hàng
3. **Xu hướng**: Mũi tên ↑↓ và % cho biết tình hình kinh doanh
4. **Sản phẩm hot**: Danh sách "Sản phẩm bán chạy" giúp biết sản phẩm nào cần ưu tiên

## 🐛 Troubleshooting

### Dashboard trống rỗng
- Kiểm tra kết nối internet
- Kiểm tra backend đang chạy: `http://localhost:5000`
- Xem Console log (F12) để biết lỗi cụ thể

### Hình ảnh sản phẩm không hiển thị
- Sử dụng ảnh placeholder mặc định
- Kiểm tra đường dẫn ảnh trong database

### Loading quá lâu
- Kiểm tra tốc độ mạng
- Backend có thể đang xử lý dữ liệu lớn
- Đợi hoặc reload trang

## 📞 Hỗ trợ

Nếu gặp vấn đề không giải quyết được:
1. Chụp màn hình lỗi
2. Mở Console (F12) và chụp log
3. Liên hệ team IT với thông tin trên

---

**Version**: 1.0.0  
**Last Updated**: December 25, 2025  
**Team**: Ocean Butterfly Shop
