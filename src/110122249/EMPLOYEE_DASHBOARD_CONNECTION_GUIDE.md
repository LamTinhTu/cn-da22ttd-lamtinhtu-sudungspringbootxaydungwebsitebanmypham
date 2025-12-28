# 🚀 Hướng dẫn kết nối và sử dụng Employee Dashboard

## ✅ Đã hoàn thành

### 1. **Backend API** đã được triển khai
- ✅ `/api/v1/dashboard/stats` - Thống kê tổng quan
- ✅ `/api/v1/dashboard/recent-orders` - 5 đơn hàng gần nhất
- ✅ `/api/v1/dashboard/top-products` - 5 sản phẩm bán chạy

### 2. **Frontend Dashboard** đã kết nối
- ✅ [DashboardHome.jsx](frontend/src/pages/Employee/DashboardHome.jsx) - Component chính
- ✅ [dashboard.js](frontend/src/api/dashboard.js) - API client
- ✅ Hiển thị 6 thẻ thống kê
- ✅ Bảng đơn hàng gần đây
- ✅ Danh sách sản phẩm bán chạy
- ✅ Loading states & error handling

---

## 🔧 Cách khởi động hệ thống

### **Bước 1: Khởi động Backend**

**Cách 1: Dùng file batch (Khuyến nghị)**
```bash
cd D:\110122249
.\restart-backend.bat
```

**Cách 2: Dùng Maven trực tiếp**
```bash
cd D:\110122249\backend
.\mvnw.cmd spring-boot:run
```

⏰ **Đợi 30-60 giây** để backend khởi động hoàn toàn.

Khi thấy dòng này là đã sẵn sàng:
```
Started BackendApplication in X.XXX seconds
```

### **Bước 2: Khởi động Frontend**

```bash
cd D:\110122249\frontend
npm start
```

Frontend sẽ tự động mở tại: http://localhost:3000

---

## 🔐 Đăng nhập

### **Tài khoản Staff (Nhân viên)**
- **Username:** `staff`
- **Password:** `password`

### **Tài khoản Admin (Quản trị viên)**
- **Username:** `admin`
- **Password:** `password`

### **Các bước đăng nhập:**

1. Mở http://localhost:3000
2. Click **"Đăng nhập"** ở góc phải
3. Nhập username: `staff` và password: `password`
4. Click **"Đăng Nhập"**
5. Sau khi đăng nhập thành công, trang sẽ tự động chuyển đến:
   - **http://localhost:3000/employee** (nếu đăng nhập với tài khoản Staff)

---

## 📊 Giao diện Employee Dashboard

### **URL:** http://localhost:3000/employee

### **Các thành phần:**

#### 1. **Thẻ thống kê (6 cards)**
   - 💰 **Tổng doanh thu** - Với xu hướng % so với tháng trước
   - 🛍️ **Tổng đơn hàng** - Với xu hướng %
   - 📦 **Đơn hàng mới** - Số đơn chờ xử lý
   - 👥 **Tổng khách hàng** - Với xu hướng %
   - 📱 **Tổng sản phẩm** - Với xu hướng %
   - ⚠️ **Sản phẩm sắp hết** - Cảnh báo tồn kho thấp

#### 2. **Đơn hàng gần đây** (Recent Orders)
   - Hiển thị 5 đơn hàng mới nhất
   - Thông tin: Mã đơn, Khách hàng, Ngày, Tổng tiền, Trạng thái
   - Màu sắc trạng thái:
     - 🟡 Chờ xử lý (PENDING)
     - 🔵 Đang xử lý (PROCESSING)
     - 🟣 Đã xác nhận (CONFIRMED)
     - 🔷 Đang giao (SHIPPING)
     - 🟢 Đã giao (DELIVERED)
     - 🔴 Đã hủy (CANCELLED)
     - ⚪ Hoàn trả (RETURNED)

#### 3. **Sản phẩm bán chạy** (Top Products)
   - Top 5 sản phẩm bán nhiều nhất
   - Hiển thị: Hình ảnh, Tên, Mã, Giá, Số lượng đã bán

---

## 🔍 Kiểm tra kết nối

### **Test 1: Dùng Test Page**
```
http://localhost:3000/test-dashboard.html
```
- Tự động load token từ localStorage
- Click "Test All" để kiểm tra cả 3 API

### **Test 2: Dùng PowerShell Script**
```powershell
cd D:\110122249
.\test-dashboard-connection.ps1
```

### **Test 3: Dùng Swagger UI**
```
http://localhost:5000/swagger-ui/index.html
```
- Tìm section **"3. Dashboard"**
- Test từng endpoint trực tiếp

---

## ❌ Xử lý lỗi thường gặp

### **Lỗi 1: "Failed to load resource: net::ERR_CONNECTION_REFUSED"**
**Nguyên nhân:** Backend chưa chạy

**Giải pháp:**
```bash
cd D:\110122249\backend
.\mvnw.cmd spring-boot:run
```

---

### **Lỗi 2: "Invalid credentials" hoặc 400 Bad Request**
**Nguyên nhân:** Sai username/password hoặc DataSeeder chưa chạy

**Giải pháp:**
1. Đảm bảo dùng đúng credentials: `staff` / `password`
2. Restart backend để chạy DataSeeder
3. Kiểm tra backend log có dòng "DATA SEEDER" không

---

### **Lỗi 3: Dashboard trống hoặc loading mãi**
**Nguyên nhân:** Token hết hạn hoặc không có quyền

**Giải pháp:**
1. Đăng xuất và đăng nhập lại
2. Xóa cache browser (Ctrl + Shift + Delete)
3. Hard reload (Ctrl + F5)
4. Kiểm tra Console (F12) xem lỗi cụ thể

---

### **Lỗi 4: "Phiên đăng nhập đã hết hạn"**
**Nguyên nhân:** JWT token đã expire

**Giải pháp:**
1. Đăng nhập lại
2. Token có thời hạn 24h (có thể config trong application.properties)

---

## 🎨 Responsive Design

Dashboard tự động điều chỉnh theo kích thước màn hình:

- **Desktop (≥1024px):** 3 cột thẻ thống kê
- **Tablet (≥768px):** 2 cột thẻ thống kê
- **Mobile (<768px):** 1 cột thẻ thống kê

---

## 🛠️ Cấu trúc code

### **Frontend:**
```
frontend/src/
├── pages/Employee/
│   ├── DashboardHome.jsx      # Component chính
│   ├── EmployeeLayout.jsx     # Layout wrapper
│   └── CustomerLookup.jsx     # Trang khác
├── api/
│   ├── dashboard.js           # API client cho dashboard
│   ├── constant.js            # Base URL config
│   └── authentication.js      # Login API
└── components/Employee/
    ├── EmployeeHeader.jsx     # Header component
    └── Sidebar.jsx            # Sidebar navigation
```

### **Backend:**
```
backend/src/main/java/com/oceanbutterflyshop/backend/
├── controllers/
│   └── DashboardController.java    # REST endpoints
├── services/
│   ├── DashboardService.java       # Interface
│   └── impl/
│       └── DashboardServiceImpl.java  # Logic xử lý
├── dtos/response/
│   ├── DashboardStatsResponse.java    # DTO stats
│   ├── RecentOrderResponse.java       # DTO orders
│   └── TopProductResponse.java        # DTO products
└── repositories/
    ├── OrderRepository.java
    ├── ProductRepository.java
    └── UserRepository.java
```

---

## 📡 API Endpoints

### **1. Dashboard Statistics**
```http
GET /api/v1/dashboard/stats
Authorization: Bearer {token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Dashboard statistics retrieved successfully",
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

### **2. Recent Orders**
```http
GET /api/v1/dashboard/recent-orders?limit=5
Authorization: Bearer {token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Recent orders retrieved successfully",
  "data": [
    {
      "orderId": 1,
      "orderCode": "ORD001",
      "customerName": "Nguyễn Văn A",
      "orderDate": "2025-12-25",
      "totalAmount": 500000,
      "orderStatus": "PENDING"
    }
  ]
}
```

### **3. Top Products**
```http
GET /api/v1/dashboard/top-products?limit=5
Authorization: Bearer {token}
```

**Response:**
```json
{
  "status": 200,
  "message": "Top selling products retrieved successfully",
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

---

## 💡 Tips & Best Practices

1. **Luôn đăng nhập với tài khoản Staff** để truy cập Employee Dashboard
2. **Kiểm tra Console (F12)** nếu có lỗi để debug nhanh hơn
3. **Sử dụng test-dashboard.html** để verify API trước khi debug frontend
4. **Restart backend** nếu gặp lỗi kết nối database
5. **Clear cache** nếu UI không update sau khi code thay đổi

---

## 📞 Hỗ trợ

Nếu gặp vấn đề:

1. **Kiểm tra backend logs** trong terminal
2. **Kiểm tra frontend console** (F12)
3. **Dùng test tools:**
   - test-dashboard.html
   - test-login.html
   - Swagger UI
4. **Restart cả backend và frontend**

---

**Version:** 1.0.0  
**Last Updated:** December 25, 2025  
**Team:** Ocean Butterfly Shop

---

## ✨ Kết luận

✅ **Backend đã kết nối thành công với Employee Dashboard!**

Tất cả API endpoints đang hoạt động ổn định với:
- Thống kê real-time từ database
- Authentication bằng JWT
- Error handling đầy đủ
- Responsive UI/UX
- Loading states & skeleton screens

**Để bắt đầu:**
1. Chạy `.\restart-backend.bat`
2. Đợi backend khởi động (30-60s)
3. Mở http://localhost:3000
4. Đăng nhập với `staff` / `password`
5. Tự động redirect đến http://localhost:3000/employee

**Enjoy! 🎉**
