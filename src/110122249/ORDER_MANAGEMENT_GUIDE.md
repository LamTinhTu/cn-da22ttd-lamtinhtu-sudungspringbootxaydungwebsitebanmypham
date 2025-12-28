# Hướng dẫn Quản lý Đơn hàng

## Tổng quan
Hệ thống quản lý đơn hàng đã được tích hợp đầy đủ giữa frontend (React) và backend (Spring Boot) với các chức năng:
- Đặt hàng từ giỏ hàng
- Xem danh sách đơn hàng
- Cập nhật trạng thái đơn hàng
- Hủy đơn hàng
- Xóa đơn hàng đã hủy

## Luồng đặt hàng (Customer)

### 1. Thêm sản phẩm vào giỏ hàng
- Truy cập trang chi tiết sản phẩm
- Chọn số lượng và nhấn "Thêm vào giỏ hàng"
- Sản phẩm được lưu vào Redux store và localStorage

### 2. Xem giỏ hàng
- Click icon giỏ hàng trên navigation để xem chi tiết
- Có thể cập nhật số lượng hoặc xóa sản phẩm
- Path: `/cart`

### 3. Thanh toán
- Từ trang giỏ hàng, nhấn "Thanh toán"
- Điền thông tin giao hàng (tên, SĐT, địa chỉ, tỉnh/thành phố)
- Chọn phương thức thanh toán
- Nhấn "Đặt hàng"
- Path: `/checkout`

**Validation:**
- Phải đăng nhập mới được đặt hàng
- Giỏ hàng không được rỗng
- Số điện thoại phải 10-11 số
- Tất cả các trường bắt buộc phải điền

**API Call:**
```javascript
POST /api/v1/orders
Body: {
  orderItems: [
    { productId: 1, itemQuantity: 2 },
    { productId: 3, itemQuantity: 1 }
  ],
  shippingAddress: "123 Đường ABC, Quận 1",
  shippingPhone: "0123456789",
  paymentMethod: "CASH",
  orderStatus: "NEW"
}
```

### 4. Xem đơn hàng của tôi
- Truy cập menu "Đơn hàng của tôi"
- Xem danh sách tất cả đơn hàng đã đặt
- Xem chi tiết từng đơn hàng
- Path: `/my-orders`

**API Call:**
```javascript
GET /api/v1/orders/user/{userId}
```

## Quản lý đơn hàng (Employee/Admin)

### Trang quản lý Employee
- Path: `/employee/orders`
- Quyền: STAFF role
- Chức năng:
  - Xem danh sách đơn hàng (phân trang)
  - Xem chi tiết đơn hàng
  - Cập nhật trạng thái đơn hàng
  - Hủy đơn hàng

### Trang quản lý Admin
- Path: `/admin/orders`
- Quyền: ADMIN role
- Chức năng:
  - Tất cả chức năng của Employee
  - Xóa đơn hàng đã hủy

### Cập nhật trạng thái
**Các trạng thái hợp lệ:**
- `NEW` (Đã đặt) - Trạng thái ban đầu khi khách đặt hàng
- `PROCESSING` (Đang xử lý) - Đơn hàng đang được xử lý
- `DELIVERED` (Đã giao) - Đơn hàng đã giao thành công
- `CANCELLED` (Đã hủy) - Đơn hàng bị hủy

**Quy tắc chuyển trạng thái:**
- Không thể cập nhật trạng thái của đơn hàng đã DELIVERED hoặc CANCELLED
- Khi đánh dấu DELIVERED, hệ thống tự động cập nhật ngày thanh toán

**API Call:**
```javascript
PUT /api/v1/orders/{orderId}/status?status=PROCESSING
```

### Hủy đơn hàng
**Điều kiện:**
- Chỉ hủy được đơn ở trạng thái NEW hoặc PROCESSING
- Customer chỉ hủy được đơn của mình
- Admin/Staff có thể hủy bất kỳ đơn nào

**Hành động khi hủy:**
- Đổi trạng thái thành CANCELLED
- Khôi phục lại số lượng tồn kho sản phẩm

**API Call:**
```javascript
PUT /api/v1/orders/{orderId}/cancel
```

### Xóa đơn hàng
**Điều kiện:**
- Chỉ Admin mới có quyền xóa
- Chỉ xóa được đơn hàng ở trạng thái CANCELLED

**API Call:**
```javascript
DELETE /api/v1/orders/{orderId}
```

## Cấu trúc dữ liệu

### OrderRequest (Tạo đơn hàng mới)
```java
{
  "orderItems": [
    {
      "productId": Integer,
      "itemQuantity": Integer
    }
  ],
  "shippingAddress": String (max 500 chars),
  "shippingPhone": String (pattern: ^[0-9]{10,11}$),
  "paymentMethod": String (CASH|BANK_TRANSFER|CARD),
  "orderStatus": String (NEW|PROCESSING|DELIVERED|CANCELLED)
}
```

### OrderResponse
```java
{
  "orderId": Integer,
  "orderCode": String,
  "orderDate": LocalDate,
  "orderAmount": BigDecimal,
  "orderStatus": String (displayName tiếng Việt),
  "paymentMethod": String (displayName tiếng Việt),
  "paymentDate": LocalDate,
  "shippingAddress": String,
  "shippingPhone": String,
  "userName": String,
  "userPhone": String,
  "orderItems": [
    {
      "productId": Integer,
      "productName": String,
      "itemPrice": BigDecimal,
      "itemQuantity": Integer
    }
  ]
}
```

## Bảo mật

### IDOR Protection
- Customer chỉ xem/hủy được đơn hàng của mình
- Admin/Staff có thể xem/hủy tất cả đơn hàng
- Backend kiểm tra quyền sở hữu đơn hàng

### Authentication
- Tất cả các API yêu cầu JWT token
- Token được gửi qua header: `Authorization: Bearer {token}`

### Rate Limiting
- Giới hạn số lượng request để tránh spam
- Cấu hình: 20 requests/phút

## Mapping Enum

Frontend gửi enum name (tiếng Anh), backend trả về displayName (tiếng Việt):

**Order Status:**
| Enum Name | Display Name |
|-----------|-------------|
| NEW | Đã đặt |
| PROCESSING | Đang xử lý |
| DELIVERED | Đã giao |
| CANCELLED | Đã hủy |

**Payment Method:**
| Enum Name | Display Name |
|-----------|-------------|
| CASH | Tiền mặt |
| BANK_TRANSFER | Chuyển khoản |
| CARD | Thẻ tín dụng |

## Testing

### Test đặt hàng thành công
1. Đăng nhập với tài khoản customer
2. Thêm sản phẩm vào giỏ hàng
3. Vào trang checkout và điền đầy đủ thông tin
4. Kiểm tra đơn hàng được tạo với trạng thái "Đã đặt"
5. Tồn kho sản phẩm giảm đi

### Test cập nhật trạng thái
1. Đăng nhập với tài khoản staff/admin
2. Vào trang quản lý đơn hàng
3. Chọn đơn hàng có trạng thái "Đã đặt"
4. Cập nhật sang "Đang xử lý"
5. Kiểm tra trạng thái đã được cập nhật

### Test hủy đơn hàng
1. Chọn đơn hàng có trạng thái "Đã đặt" hoặc "Đang xử lý"
2. Nhấn nút "Hủy"
3. Kiểm tra trạng thái đổi thành "Đã hủy"
4. Kiểm tra tồn kho sản phẩm được khôi phục

### Test xóa đơn hàng
1. Đăng nhập với tài khoản admin
2. Chọn đơn hàng có trạng thái "Đã hủy"
3. Nhấn nút "Xóa"
4. Kiểm tra đơn hàng đã bị xóa khỏi database

## Troubleshooting

### Lỗi "Invalid order status"
- **Nguyên nhân:** Frontend gửi displayName thay vì enum name
- **Giải pháp:** Đảm bảo gửi NEW, PROCESSING, DELIVERED, CANCELLED (viết hoa)

### Lỗi "Cannot update status of delivered order"
- **Nguyên nhân:** Cố gắng cập nhật đơn đã giao hoặc đã hủy
- **Giải pháp:** Chỉ cập nhật đơn hàng ở trạng thái NEW hoặc PROCESSING

### Lỗi "You do not have permission"
- **Nguyên nhân:** Customer cố hủy/xem đơn hàng của người khác
- **Giải pháp:** Đảm bảo đăng nhập đúng tài khoản hoặc dùng tài khoản admin/staff

### Đơn hàng không tìm thấy
- **Nguyên nhân:** ID hoặc mã đơn hàng không tồn tại
- **Giải pháp:** Kiểm tra lại orderId hoặc orderCode

## File liên quan

**Frontend:**
- `frontend/src/pages/Checkout/CheckoutPage.jsx` - Trang thanh toán
- `frontend/src/pages/Orders/MyOrders.jsx` - Đơn hàng của khách
- `frontend/src/pages/Employee/Orders.jsx` - Quản lý đơn hàng (Employee)
- `frontend/src/pages/Admin/Orders.jsx` - Quản lý đơn hàng (Admin)
- `frontend/src/api/order.js` - API calls cho đơn hàng
- `frontend/src/store/features/cartSlice.js` - Redux state cho giỏ hàng

**Backend:**
- `backend/src/main/java/.../controllers/OrderController.java` - REST endpoints
- `backend/src/main/java/.../services/impl/OrderServiceImpl.java` - Business logic
- `backend/src/main/java/.../entities/Order.java` - Entity model
- `backend/src/main/java/.../dtos/request/OrderRequest.java` - Request DTO
- `backend/src/main/java/.../dtos/response/OrderResponse.java` - Response DTO
- `backend/src/main/java/.../enums/OrderStatus.java` - Enum trạng thái
- `backend/src/main/java/.../enums/PaymentMethod.java` - Enum thanh toán
