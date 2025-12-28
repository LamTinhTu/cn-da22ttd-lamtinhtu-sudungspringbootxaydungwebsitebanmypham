# COSMETICS SELLING WEBSITE API Documentation

> **Version:** 1.0.0  
> **Base URL:** `http://localhost:8080/api/v1`  
> **Last Updated:** December 19, 2025

## Table of Contents
- [Authentication](#authentication)
- [Product Management](#product-management)
- [Order Management](#order-management)
- [User Management](#user-management)
- [Brand Management](#brand-management)
- [Image Management](#image-management)
- [Role Management](#role-management)
- [Dashboard](#dashboard)
- [Common Response Format](#common-response-format)
- [Error Codes Reference](#error-codes-reference)

---

## Authentication

All authentication endpoints are **public** and do not require JWT tokens.

### 1. Register New Customer

**Endpoint:** `POST /api/v1/auth/register`

**Description:** Create a new customer account with default role (CUSTOMER). Auto-generates user code with 'KH' prefix.

**Auth Required:** No

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| userName | String | Letters and spaces only, max 255 characters | Yes |
| userPhone | String | Exactly 10-11 digits | Yes |
| userAccount | String | 3-50 characters | Yes |
| userPassword | String | Minimum 6 characters | Yes |

**Request Example:**
```json
{
  "userName": "Nguyen Van An",
  "userPhone": "0912345678",
  "userAccount": "customer123",
  "userPassword": "password123"
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "Registration successful",
  "data": {
    "userCode": "KH12345678",
    "userName": "Nguyen Van An",
    "roleName": "Customer",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Error Codes:**
- **400 Bad Request:** 
  - Validation error (invalid phone format, username too short)
  - Duplicate username or phone number
  - Message example: `"RBHT: Full name must contain only letters and spaces"` or `"RBSDT: Phone must be exactly 10 or 11 digits"`

---

### 2. User Login

**Endpoint:** `POST /api/v1/auth/login`

**Description:** Authenticate user with username and password. Returns JWT token and user information on success.

**Auth Required:** No

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| userAccount | String | Not blank | Yes |
| userPassword | String | Not blank | Yes |

**Request Example:**
```json
{
  "userAccount": "admin",
  "userPassword": "password"
}
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Login successful",
  "data": {
    "userCode": "AD12345678",
    "userName": "Admin User",
    "roleName": "Administrator",
    "accessToken": "eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
  }
}
```

**Error Codes:**
- **400 Bad Request:** Missing username or password
- **401 Unauthorized:** Invalid credentials (wrong username or password)

---

## Product Management

GET endpoints are **public**. Create/Update/Delete require **ADMIN or STAFF** role.

### 3. Get Products (Paginated & Filtered)

**Endpoint:** `GET /api/v1/products`

**Description:** Retrieve products with pagination, sorting, and advanced filtering. Supports keyword search, price range, brand filtering, and status filtering.

**Auth Required:** No

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-indexed) |
| size | Integer | 10 | Items per page |
| sort | String | productId,desc | Sort field and direction (format: field,direction) |
| keyword | String | - | Search in product name and description |
| minPrice | BigDecimal | - | Minimum product price |
| maxPrice | BigDecimal | - | Maximum product price |
| brandId | Integer | - | Filter by brand ID |
| status | String | - | Filter by status: `SELLING`, `OUT_OF_STOCK`, `DISCONTINUED` |

**Sortable Fields:** `productId`, `productName`, `productPrice`, `productStatus`

**Request Examples:**
```
GET /api/v1/products
GET /api/v1/products?page=1&size=5
GET /api/v1/products?sort=productPrice,desc&keyword=rolex&minPrice=1000&maxPrice=5000
GET /api/v1/products?brandId=1&status=SELLING
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Products retrieved successfully",
  "data": {
    "items": [
      {
        "productId": 1,
        "productCode": "SP09123842",
        "productName": "Kem nền",
        "productDescription": "Kem nền xịn",
        "productPrice": 8500.50,
        "quantityStock": 15,
        "productStatus": "SELLING",
        "createdAt": "2025-12-01T10:00:00",
        "updatedAt": "2025-12-01T10:00:00",
        "brandId": 1,
        "brandCode": "TH12345678",
        "brandName": "MAC",
        "brandDescription": "Thương hiệu trang điểm chuyên nghiệp đẳng cấp quốc tế",
        "images": [
          {
            "imageId": 1,
            "imageName": "rolex-front.jpg",
            "imageURL": "https://example.com/rolex-front.jpg"
          }
        ]
      }
    ],
    "pageNo": 0,
    "pageSize": 10,
    "totalPages": 5,
    "totalElements": 50,
    "first": true,
    "last": false,
    "hasNext": true,
    "hasPrevious": false
  }
}
```

**Error Codes:**
- **400 Bad Request:** Invalid query parameters

---

### 4. Get Product by ID

**Endpoint:** `GET /api/v1/products/{productId}`

**Description:** Retrieve a single product by its ID.

**Auth Required:** No

**Path Parameters:**
- `productId` (Integer) - Product identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Product retrieved successfully",
  "data": {
    "productId": 1,
    "productCode": "SP09123842",
    "productName": "Kem nền",
    "productDescription": "Kem nền xịn",
    "productPrice": 8500.50,
    "quantityStock": 15,
    "productStatus": "SELLING",
    "brandId": 1,
    "brandName": "Omega",
    "images": []
  }
}
```

**Error Codes:**
- **404 Not Found:** Product not found

---

### 5. Create Product

**Endpoint:** `POST /api/v1/products`

**Description:** Create a new watch product with auto-generated code (SP prefix).

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| productName | String | Letters, numbers, spaces, hyphens, underscores only. Max 100 chars | Yes |
| productDescription | String | Max 1000 characters | No |
| productPrice | BigDecimal | Must be positive | Yes |
| quantityStock | Integer | Cannot be negative | Yes |
| brandId | Integer | Must exist | Yes |
| productStatus | Enum | `NOT_SOLD`, `SELLING`, `OUT_OF_STOCK`, `DISCONTINUED` | Yes |

**Request Example:**
```json
{
  "productName": "Kem nền",
  "productDescription": "Kem nền xịn",
  "productPrice": 8500.50,
  "quantityStock": 15,
  "brandId": 1,
  "productStatus": "SELLING"
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "Product created successfully",
  "data": {
    "productId": 1,
    "productCode": "SP09123842",
    "productName": "Kem nền",
    "productPrice": 8500.50,
    "quantityStock": 15,
    "productStatus": "SELLING",
    "brandId": 1
  }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error (invalid price, missing fields)
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** User does not have ADMIN or STAFF role

---

### 6. Update Product

**Endpoint:** `PUT /api/v1/products/{productId}`

**Description:** Update an existing product.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `productId` (Integer) - Product identifier

**Request Body:** Same as Create Product

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Product updated successfully",
  "data": { /* Product object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Product not found

---

### 7. Delete Product

**Endpoint:** `DELETE /api/v1/products/{productId}`

**Description:** Delete a product (soft delete).

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `productId` (Integer) - Product identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Product deleted successfully",
  "data": null
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Product not found

---

## Order Management

All order endpoints require authentication. Customers can only access their own orders.

### 8. Get Orders (Paginated)

**Endpoint:** `GET /api/v1/orders`

**Description:** Retrieve orders with pagination and sorting support.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-indexed) |
| size | Integer | 10 | Items per page |
| sort | String | orderId,desc | Sort field and direction |

**Sortable Fields:** `orderId`, `orderDate`, `totalPrice`, `orderStatus`

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Orders retrieved successfully",
  "data": {
    "items": [
      {
        "orderId": 1,
        "orderCode": "DH12345678",
        "userId": 1,
        "orderDate": "2025-12-03",
        "orderStatus": "New",
        "orderAmount": 17001.00,
        "shippingAddress": "123 Le Loi Street, District 1, Ho Chi Minh City",
        "shippingPhone": "0912345678",
        "paymentDate": "2025-12-03",
        "paymentMethod": "BANK_TRANSFER",
        "userName": "Nguyen Van An",
        "userPhone": "0912345678",
        "orderItems": [
          {
            "orderItemId": 1,
            "productId": 1,
            "productName": "Kem nền",
            "productCode": "SP12345678",
            "itemQuantity": 2,
            "itemPrice": 8500.50,
            "unitPrice": 8500.50
          }
        ]
      }
    ],
    "pageNo": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 5,
    "first": true,
    "last": true,
    "hasNext": false,
    "hasPrevious": false
  }
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role

---

### 9. Get Order by ID

**Endpoint:** `GET /api/v1/orders/{orderId}`

**Description:** Retrieve a single order by ID. Customers can only view their own orders. Admin/Staff can view all orders.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Path Parameters:**
- `orderId` (Integer) - Order identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Order retrieved successfully",
  "data": { /* Order object */ }
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Customer trying to access another customer's order
- **404 Not Found:** Order not found

---

### 10. Get Order by Code

**Endpoint:** `GET /api/v1/orders/code/{orderCode}`

**Description:** Retrieve a single order by order code.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Path Parameters:**
- `orderCode` (String) - Order code (e.g., "DH12345678")

**Success Response (200 OK):** Same as Get Order by ID

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Customer trying to access another customer's order
- **404 Not Found:** Order not found

---

### 11. Get Orders by User ID

**Endpoint:** `GET /api/v1/orders/user/{userId}`

**Description:** Retrieve all orders for a specific user.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `userId` (Integer) - User identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "User orders retrieved successfully",
  "data": [ /* Array of Order objects */ ]
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role

---

### 12. Get Orders by Status

**Endpoint:** `GET /api/v1/orders/status/{status}`

**Description:** Retrieve all orders with a specific status.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Path Parameters:**
- `status` (String) - Order status: `New`, `Processing`, `Delivered`, `Cancelled`

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Orders by status retrieved successfully",
  "data": [ /* Array of Order objects */ ]
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid

---

### 13. Create Order

**Endpoint:** `POST /api/v1/orders`

**Description:** Create a new order. User ID is automatically retrieved from SecurityContext (JWT token).

**Auth Required:** Yes (Role: **Any authenticated user**)

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| orderStatus | String | `New`, `Processing`, `Delivered`, `Cancelled` | Yes |
| orderAmount | BigDecimal | Must be positive | Yes |
| shippingAddress | String | Max 200 characters | Yes |
| shippingPhone | String | Exactly 10-11 digits | Yes |
| paymentDate | LocalDate | ISO date format (YYYY-MM-DD) | No |
| paymentMethod | String | `CASH`, `BANK_TRANSFER`, `CARD` | No |
| orderItems | Array | At least 1 item | Yes |

**OrderItem Object:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| productId | Integer | Must exist | Yes |
| itemQuantity | Integer | Minimum 1 | Yes |
| itemPrice | BigDecimal | Must be positive | Yes |
| unitPrice | Double | Must be positive | Yes |

**Request Example:**
```json
{
  "orderStatus": "New",
  "orderAmount": 17001.00,
  "shippingAddress": "123 Le Loi Street, District 1, Ho Chi Minh City",
  "shippingPhone": "0912345678",
  "paymentDate": "2025-12-03",
  "paymentMethod": "BANK_TRANSFER",
  "orderItems": [
    {
      "productId": 1,
      "itemQuantity": 2,
      "itemPrice": 8500.50,
      "unitPrice": 8500.50
    }
  ]
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "Order created successfully",
  "data": {
    "orderId": 1,
    "orderCode": "DH12345678",
    "orderStatus": "New",
    "orderAmount": 17001.00,
    "orderItems": [ /* Order items */ ]
  }
}
```

**Error Codes:**
- **400 Bad Request:** 
  - Validation error (missing fields, invalid format)
  - Order must have at least one item
  - Product not found or insufficient stock
- **401 Unauthorized:** JWT token missing or invalid

---

### 14. Update Order Status

**Endpoint:** `PUT /api/v1/orders/{orderId}/status`

**Description:** Update the status of an order.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `orderId` (Integer) - Order identifier

**Query Parameters:**
- `status` (String) - New status: `New`, `Processing`, `Delivered`, `Cancelled`

**Request Example:**
```
PUT /api/v1/orders/1/status?status=Processing
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Order status updated successfully",
  "data": { /* Updated Order object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Invalid status value
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Order not found

---

### 15. Update Order Payment

**Endpoint:** `PUT /api/v1/orders/{orderId}/payment`

**Description:** Update the payment method of an order.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `orderId` (Integer) - Order identifier

**Query Parameters:**
- `paymentMethod` (String) - Payment method: `CASH`, `BANK_TRANSFER`, `CARD`

**Request Example:**
```
PUT /api/v1/orders/1/payment?paymentMethod=CARD
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Order payment updated successfully",
  "data": { /* Updated Order object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Invalid payment method
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Order not found

---

### 16. Cancel Order

**Endpoint:** `PUT /api/v1/orders/{orderId}/cancel`

**Description:** Cancel an order. Customers can only cancel their own orders. Admin/Staff can cancel any order.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Path Parameters:**
- `orderId` (Integer) - Order identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Order cancelled successfully",
  "data": null
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Customer trying to cancel another customer's order
- **404 Not Found:** Order not found

---

### 17. Calculate Order Amount

**Endpoint:** `GET /api/v1/orders/calculate-amount`

**Description:** Calculate the total amount for an order based on product IDs and quantities.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Query Parameters:**
- `productIds` (Array of Integers) - List of product IDs
- `quantities` (Array of Integers) - Corresponding quantities for each product

**Request Example:**
```
GET /api/v1/orders/calculate-amount?productIds=1,2&quantities=2,1
```

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Order amount calculated",
  "data": 17001.00
}
```

**Error Codes:**
- **400 Bad Request:** Mismatched array lengths or invalid product IDs
- **401 Unauthorized:** JWT token missing or invalid

---

## User Management

All user endpoints require authentication. Most require **ADMIN** role.

### 18. Get Users (Paginated)

**Endpoint:** `GET /api/v1/users`

**Description:** Retrieve users with pagination and sorting support.

**Auth Required:** Yes (Role: **ADMIN**)

**Query Parameters:**

| Parameter | Type | Default | Description |
|-----------|------|---------|-------------|
| page | Integer | 0 | Page number (0-indexed) |
| size | Integer | 10 | Items per page |
| sort | String | userId,desc | Sort field and direction |

**Sortable Fields:** `userId`, `userAccount`, `userFullName`, `userEmail`

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Users retrieved successfully",
  "data": {
    "items": [
      {
        "userId": 1,
        "userCode": "KH12345678",
        "userName": "Nguyen Van An",
        "userGender": "Male",
        "userBirthDate": "1990-05-15",
        "userAddress": "123 Le Loi Street",
        "userPhone": "0912345678",
        "userAccount": "nguyenvanan",
        "roleId": 3,
        "roleCode": "KH",
        "roleName": "Customer"
      }
    ],
    "pageNo": 0,
    "pageSize": 10,
    "totalPages": 1,
    "totalElements": 5,
    "first": true,
    "last": true
  }
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role

---

### 19. Get User by ID

**Endpoint:** `GET /api/v1/users/{userId}`

**Description:** Retrieve a single user by ID.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Path Parameters:**
- `userId` (Integer) - User identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "User retrieved successfully",
  "data": { /* User object */ }
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **404 Not Found:** User not found

---

### 20. Get User by Account

**Endpoint:** `GET /api/v1/users/account/{userAccount}`

**Description:** Retrieve a single user by account username.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Path Parameters:**
- `userAccount` (String) - Username

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "User retrieved successfully",
  "data": { /* User object */ }
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **404 Not Found:** User not found

---

### 21. Create User

**Endpoint:** `POST /api/v1/users`

**Description:** Create a new user with any role.

**Auth Required:** Yes (Role: **ADMIN**)

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| userName | String | Letters and spaces only. Max 100 chars | Yes |
| userGender | String | `Male`, `Female`, `Other` | Yes |
| userBirthDate | LocalDate | Must be in the past (ISO format YYYY-MM-DD) | No |
| userAddress | String | Max 200 characters | No |
| userPhone | String | Exactly 10-11 digits | Yes |
| userAccount | String | 3-50 characters | Yes |
| userPassword | String | Minimum 6 characters | Yes |
| roleId | Integer | 1=Admin, 2=Staff, 3=Customer | Yes |

**Request Example:**
```json
{
  "userName": "Nguyen Van An",
  "userGender": "Male",
  "userBirthDate": "1990-05-15",
  "userAddress": "123 Le Loi Street, District 1, Ho Chi Minh City",
  "userPhone": "0912345678",
  "userAccount": "nguyenvanan",
  "userPassword": "password123",
  "roleId": 3
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "User created successfully",
  "data": { /* User object */ }
}
```

**Error Codes:**
- **400 Bad Request:** 
  - Validation error
  - Duplicate username or phone
  - Invalid role ID
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role

---

### 22. Update User

**Endpoint:** `PUT /api/v1/users/{userId}`

**Description:** Update an existing user.

**Auth Required:** Yes (Role: **ADMIN**)

**Path Parameters:**
- `userId` (Integer) - User identifier

**Request Body:** Same as Create User

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "User updated successfully",
  "data": { /* Updated User object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role
- **404 Not Found:** User not found

---

### 23. Delete User

**Endpoint:** `DELETE /api/v1/users/{userId}`

**Description:** Delete a user.

**Auth Required:** Yes (Role: **ADMIN**)

**Path Parameters:**
- `userId` (Integer) - User identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "User deleted successfully",
  "data": null
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role
- **404 Not Found:** User not found

---

## Brand Management

GET endpoints are **public**. Create/Update/Delete require **ADMIN or STAFF** role.

### 24. Get All Brands

**Endpoint:** `GET /api/v1/brands`

**Description:** Retrieve all brands.

**Auth Required:** No

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Brands retrieved successfully",
  "data": [
    {
      "brandId": 1,
      "brandCode": "TH12345678",
      "brandName": "Omega",
      "brandDescription": "Swiss luxury watchmaker known for precision",
      "products": []
    }
  ]
}
```

---

### 25. Get Brand by ID

**Endpoint:** `GET /api/v1/brands/{brandId}`

**Description:** Retrieve a single brand by ID.

**Auth Required:** No

**Path Parameters:**
- `brandId` (Integer) - Brand identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Brand retrieved successfully",
  "data": { /* Brand object */ }
}
```

**Error Codes:**
- **404 Not Found:** Brand not found

---

### 26. Create Brand

**Endpoint:** `POST /api/v1/brands`

**Description:** Create a new brand with auto-generated code (TH prefix).

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| brandName | String | Max 100 characters | Yes |
| brandDescription | String | Max 1000 characters | No |

**Request Example:**
```json
{
  "brandName": "Omega",
  "brandDescription": "Swiss luxury watchmaker known for precision and innovation"
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "Brand created successfully",
  "data": {
    "brandId": 1,
    "brandCode": "TH12345678",
    "brandName": "Omega",
    "brandDescription": "Swiss luxury watchmaker"
  }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error or duplicate brand name
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role

---

### 27. Update Brand

**Endpoint:** `PUT /api/v1/brands/{brandId}`

**Description:** Update an existing brand.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `brandId` (Integer) - Brand identifier

**Request Body:** Same as Create Brand

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Brand updated successfully",
  "data": { /* Updated Brand object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Brand not found

---

### 28. Delete Brand

**Endpoint:** `DELETE /api/v1/brands/{brandId}`

**Description:** Delete a brand.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `brandId` (Integer) - Brand identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Brand deleted successfully",
  "data": null
}
```

**Error Codes:**
- **400 Bad Request:** Brand has associated products (cannot delete)
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Brand not found

---

## Image Management

GET endpoints are **public**. Create/Update/Delete require **ADMIN or STAFF** role.

### 29. Get All Images

**Endpoint:** `GET /api/v1/images`

**Description:** Retrieve all product images.

**Auth Required:** No

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Images retrieved successfully",
  "data": [
    {
      "imageId": 1,
      "productId": 1,
      "imageName": "rolex-front.jpg",
      "imageURL": "https://example.com/rolex-front.jpg",
      "productName": "Rolex Submariner"
    }
  ]
}
```

---

### 30. Get Image by ID

**Endpoint:** `GET /api/v1/images/{imageId}`

**Description:** Retrieve a single image by ID.

**Auth Required:** No

**Path Parameters:**
- `imageId` (Integer) - Image identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Image retrieved successfully",
  "data": { /* Image object */ }
}
```

**Error Codes:**
- **404 Not Found:** Image not found

---

### 31. Get Images by Product ID

**Endpoint:** `GET /api/v1/images/product/{productId}`

**Description:** Retrieve all images for a specific product.

**Auth Required:** No

**Path Parameters:**
- `productId` (Integer) - Product identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Product images retrieved successfully",
  "data": [ /* Array of Image objects */ ]
}
```

---

### 32. Create Image

**Endpoint:** `POST /api/v1/images`

**Description:** Create a new product image.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| productId | Integer | Must exist | Yes |
| imageName | String | Max 100 characters | Yes |
| imageURL | String | Valid HTTP/HTTPS URL ending with jpg, jpeg, png, gif, or webp. Max 255 chars | Yes |

**Request Example:**
```json
{
  "productId": 1,
  "imageName": "rolex-front.jpg",
  "imageURL": "https://example.com/images/rolex-front.jpg"
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "Image created successfully",
  "data": {
    "imageId": 1,
    "productId": 1,
    "imageName": "rolex-front.jpg",
    "imageURL": "https://example.com/images/rolex-front.jpg"
  }
}
```

**Error Codes:**
- **400 Bad Request:** 
  - Validation error (invalid URL format)
  - Product not found
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role

---

### 33. Update Image

**Endpoint:** `PUT /api/v1/images/{imageId}`

**Description:** Update an existing image.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `imageId` (Integer) - Image identifier

**Request Body:** Same as Create Image

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Image updated successfully",
  "data": { /* Updated Image object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Image not found

---

### 34. Delete Image

**Endpoint:** `DELETE /api/v1/images/{imageId}`

**Description:** Delete an image.

**Auth Required:** Yes (Role: **ADMIN or STAFF**)

**Path Parameters:**
- `imageId` (Integer) - Image identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Image deleted successfully",
  "data": null
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN or STAFF role
- **404 Not Found:** Image not found

---

## Role Management

GET endpoints are **public**. Create/Update/Delete require **ADMIN** role.

### 35. Get All Roles

**Endpoint:** `GET /api/v1/roles`

**Description:** Retrieve all roles.

**Auth Required:** No

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Roles retrieved successfully",
  "data": [
    {
      "roleId": 1,
      "roleCode": "AD",
      "roleName": "Administrator"
    },
    {
      "roleId": 2,
      "roleCode": "NV",
      "roleName": "Staff"
    },
    {
      "roleId": 3,
      "roleCode": "KH",
      "roleName": "Customer"
    }
  ]
}
```

---

### 36. Get Role by ID

**Endpoint:** `GET /api/v1/roles/{roleId}`

**Description:** Retrieve a single role by ID.

**Auth Required:** No

**Path Parameters:**
- `roleId` (Integer) - Role identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Role retrieved successfully",
  "data": {
    "roleId": 3,
    "roleCode": "KH",
    "roleName": "Customer"
  }
}
```

**Error Codes:**
- **404 Not Found:** Role not found

---

### 37. Get Role by Code

**Endpoint:** `GET /api/v1/roles/code/{roleCode}`

**Description:** Retrieve a single role by role code.

**Auth Required:** No

**Path Parameters:**
- `roleCode` (String) - Role code (e.g., "AD", "NV", "KH")

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Role retrieved successfully",
  "data": { /* Role object */ }
}
```

**Error Codes:**
- **404 Not Found:** Role not found

---

### 38. Create Role

**Endpoint:** `POST /api/v1/roles`

**Description:** Create a new role.

**Auth Required:** Yes (Role: **ADMIN**)

**Request Body:**

| Field | Type | Validation Rules | Required |
|-------|------|-----------------|----------|
| roleCode | String | Max 20 characters | Yes |
| roleName | String | Max 100 characters | Yes |

**Request Example:**
```json
{
  "roleCode": "MG",
  "roleName": "Manager"
}
```

**Success Response (201 Created):**
```json
{
  "status": 201,
  "message": "Role created successfully",
  "data": {
    "roleId": 4,
    "roleCode": "MG",
    "roleName": "Manager"
  }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error or duplicate role code
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role

---

### 39. Update Role

**Endpoint:** `PUT /api/v1/roles/{roleId}`

**Description:** Update an existing role.

**Auth Required:** Yes (Role: **ADMIN**)

**Path Parameters:**
- `roleId` (Integer) - Role identifier

**Request Body:** Same as Create Role

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Role updated successfully",
  "data": { /* Updated Role object */ }
}
```

**Error Codes:**
- **400 Bad Request:** Validation error
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role
- **404 Not Found:** Role not found

---

### 40. Delete Role

**Endpoint:** `DELETE /api/v1/roles/{roleId}`

**Description:** Delete a role.

**Auth Required:** Yes (Role: **ADMIN**)

**Path Parameters:**
- `roleId` (Integer) - Role identifier

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Role deleted successfully",
  "data": null
}
```

**Error Codes:**
- **400 Bad Request:** Role has associated users (cannot delete)
- **401 Unauthorized:** JWT token missing or invalid
- **403 Forbidden:** Requires ADMIN role
- **404 Not Found:** Role not found

---

## Dashboard

### 41. Get Dashboard Statistics

**Endpoint:** `GET /api/v1/dashboard/stats`

**Description:** Retrieve dashboard statistics including total counts and revenue.

**Auth Required:** Yes (Role: **Any authenticated user**)

**Success Response (200 OK):**
```json
{
  "status": 200,
  "message": "Dashboard statistics retrieved",
  "data": {
    "totalProducts": 50,
    "totalBrands": 10,
    "totalUsers": 25,
    "totalOrders": 100,
    "pendingOrders": 15,
    "totalRevenue": 250000.50,
    "lowStockProducts": 5
  }
}
```

**Error Codes:**
- **401 Unauthorized:** JWT token missing or invalid

---

## Common Response Format

All API responses follow this standard format:

```json
{
  "status": 200,
  "message": "Operation successful",
  "data": { /* Response data or null */ }
}
```

### Pagination Response Format

Paginated endpoints return data in this wrapper:

```json
{
  "items": [ /* Array of items */ ],
  "pageNo": 0,
  "pageSize": 10,
  "totalPages": 5,
  "totalElements": 50,
  "first": true,
  "last": false,
  "hasNext": true,
  "hasPrevious": false
}
```

---

## Error Codes Reference

### HTTP Status Codes

| Code | Meaning | Common Causes |
|------|---------|---------------|
| 200 | OK | Successful GET, PUT, DELETE request |
| 201 | Created | Successful POST request (resource created) |
| 400 | Bad Request | Validation error, missing required fields, invalid data format |
| 401 | Unauthorized | Missing JWT token, invalid token, expired token |
| 403 | Forbidden | User does not have required role for the operation |
| 404 | Not Found | Resource does not exist (product, user, order, etc.) |
| 500 | Internal Server Error | Unexpected server error |

### Validation Error Format

When validation fails (400 Bad Request), the response includes detailed error messages:

```json
{
  "status": 400,
  "message": "Validation failed",
  "data": {
    "userName": "RBHT: Full name must contain only letters and spaces",
    "userPhone": "RBSDT: Phone must be exactly 10 or 11 digits",
    "productPrice": "Product price must be a positive number"
  }
}
```

### Common Validation Messages

| Field | Validation Rule | Error Message |
|-------|----------------|---------------|
| userName | Letters and spaces only | `RBHT: Full name must contain only letters and spaces` |
| userPhone | 10-11 digits | `RBSDT: Phone must be exactly 10 or 11 digits` |
| userAccount | 3-50 characters | `Username must be between 3 and 50 characters` |
| userPassword | Min 6 characters | `Password must be at least 6 characters` |
| productPrice | Positive number | `Product price must be a positive number` |
| orderStatus | Enum values | `Order status must be: New, Processing, Delivered, or Cancelled` |
| paymentMethod | Enum values | `Payment method must be: CASH, BANK_TRANSFER, or CARD` |

---

## Authentication & Authorization

### JWT Token Usage

All protected endpoints require a JWT token in the Authorization header:

```
Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9...
```

### Token Expiration

- JWT tokens are valid for **24 hours** from the time of issuance
- After expiration, users must login again to obtain a new token

### Role-Based Access Control

| Role | Code | Permissions |
|------|------|-------------|
| Administrator | AD | Full access to all endpoints |
| Staff | NV | Manage products, brands, images, orders (cannot manage users or roles) |
| Customer | KH | Create orders, view own orders, view public endpoints |

### Role Requirements by Endpoint Group

| Endpoint Group | Public (No Auth) | CUSTOMER | STAFF | ADMIN |
|----------------|------------------|----------|-------|-------|
| Authentication | ✅ Register, Login | - | - | - |
| Products (GET) | ✅ All GET endpoints | ✅ | ✅ | ✅ |
| Products (CUD) | ❌ | ❌ | ✅ | ✅ |
| Orders (View) | ❌ | ✅ Own orders only | ✅ All | ✅ All |
| Orders (Create) | ❌ | ✅ | ✅ | ✅ |
| Orders (Update) | ❌ | ❌ Cancel own | ✅ | ✅ |
| Users (View) | ❌ | ✅ Own profile | ✅ All | ✅ All |
| Users (CUD) | ❌ | ❌ | ❌ | ✅ |
| Brands (GET) | ✅ All GET endpoints | ✅ | ✅ | ✅ |
| Brands (CUD) | ❌ | ❌ | ✅ | ✅ |
| Images (GET) | ✅ All GET endpoints | ✅ | ✅ | ✅ |
| Images (CUD) | ❌ | ❌ | ✅ | ✅ |
| Roles (GET) | ✅ All GET endpoints | ✅ | ✅ | ✅ |
| Roles (CUD) | ❌ | ❌ | ❌ | ✅ |
| Dashboard | ❌ | ✅ | ✅ | ✅ |

---

## Testing Guide

### Using Postman/Thunder Client

1. **Login to get JWT token:**
   ```
   POST http://localhost:8080/api/v1/auth/login
   Body: {
     "userAccount": "admin",
     "userPassword": "password"
   }
   ```

2. **Copy the `accessToken` from the response**

3. **Add token to subsequent requests:**
   - Go to Authorization tab
   - Select "Bearer Token"
   - Paste the token

### Sample Test Flow

1. Register a new customer
2. Login with the customer account
3. Browse products (with filters)
4. Create an order
5. View order details
6. Cancel order (if needed)

### Admin Test Flow

1. Login as admin
2. Create a brand
3. Create a product
4. View all orders
5. Update order status
6. View dashboard statistics

---

## Notes for Frontend Developers

### Date Formats
- All dates use ISO 8601 format: `YYYY-MM-DD` (e.g., `2025-12-03`)
- Timestamps use ISO 8601 with time: `YYYY-MM-DDTHH:mm:ss` (e.g., `2025-12-03T14:30:00`)

### Decimal Numbers
- Prices and amounts use `BigDecimal` type
- Send as numbers in JSON: `8500.50` (not as strings)

### Enum Values
- **Order Status:** `New`, `Processing`, `Delivered`, `Cancelled`
- **Payment Method:** `CASH`, `BANK_TRANSFER`, `CARD`
- **Product Status:** `NOT_SOLD`, `SELLING`, `OUT_OF_STOCK`, `DISCONTINUED`
- **Gender:** `Male`, `Female`, `Other`

### Pagination
- Page numbers are **0-indexed** (first page is 0, not 1)
- Default page size is 10
- Use `hasNext` and `hasPrevious` flags to enable/disable pagination buttons

### Security Context
- `userId` is **not required** in order creation - it's extracted from the JWT token
- The authenticated user's information is automatically retrieved from SecurityContext

### Code Generation
The following codes are **auto-generated** by the system (do not send in requests):
- User Code: `KH` (Customer), `AD` (Admin), `NV` (Staff) + 8 random digits
- Product Code: `SP` + 8 random digits
- Order Code: `DH` + 8 random digits
- Brand Code: `TH` + 8 random digits

### Error Handling
Always check the `status` field in responses:
- `status: 200-299` → Success
- `status: 400-499` → Client error (check your request)
- `status: 500-599` → Server error (contact backend team)

Display the `message` field to users for friendly error messages.

---

**End of API Documentation**

For questions or issues, please contact the backend development team.
