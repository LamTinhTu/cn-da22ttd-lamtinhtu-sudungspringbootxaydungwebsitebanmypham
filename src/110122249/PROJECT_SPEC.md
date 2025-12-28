# COSMETICS SELLING WEBSITE - PROJECT SPECIFICATION

## 1. Project Overview
Build a retail watch website system to help the store manage imports, sales, and product tracking.
- **Goal:** Manage products, brands, customers, orders, and revenue reports.
- **Target Users:** Administrator (Admin), Staff, Customer.

## 2. Tech Stack
- **Backend:** Spring Boot (Java 17+), Maven/Gradle.
- **Frontend:** ReactJS, Axios, (Recommended: TailwindCSS or Ant Design for UI).
- **Database:** PostgreSQL.
- **DevOps:** Docker, Docker Compose, Nginx (as Reverse Proxy).
- **Architecture:** RESTful API, Layered Architecture.

## 3. Database Schema Design
Based on the designed ERD, the table structures are as follows:

### 3.1. Main Tables
1. **Brand**
   - `BrandID` (PK, int): Auto-increment ID.
   - `BrandCode` (varchar(10), unique): Format "TH" + 8 random digits.
   - `BrandName` (varchar): Brand Name.
   - `BrandDescription` (text): Description.

2. **Product**
   - `ProductID` (PK, int): Auto-increment ID.
   - `BrandID` (FK, int): Links to Brand table.
   - `ProductCode` (varchar(10), unique): Format "SP" + 8 random digits.
   - `ProductName` (varchar): Product Name.
   - `ProductDescription` (text): Description.
   - `ProductPrice` (DECIMAL(15,2)): Selling Price (Replaces float).
   - `QuantityStock` (int): Stock Quantity (Min = 0).
   - `ProductStatus` (boolean): "Chưa bán", "Đang bán", "Hết hàng", "Ngừng kinh doanh": ENUM(Not Sold, Selling, Out of Stock, Discontinued).

3. **Image** (Product Images)
   - `ImageID` (PK, int).
   - `ProductID` (FK, int).
   - `ImageName` (varchar).
   - `ImageURL` (text): Image Link (Replaces char).

4. **Role**
   - `RoleID` (PK, int).
   - `RoleCode` (char).
   - `RoleName` (varchar).

5. **User** (User/Customer)
   - `UserID` (PK, int).
   - `RoleID` (FK, int).
   - `UserCode` (varchar(10), unique): Format based on Role ("AD", "NV", "KH") + 8 random digits.
   - `UserName` (varchar).
   - `UserGender` (enum/varchar): "Nam", "Nữ", "Khác": ENUM(Male, Female, Other).
   - `UserBirthDate` (date).
   - `UserAddress` (varchar).
   - `UserPhone` (varchar(11)): Validate 10-11 digits.
   - `UserAccount` (char): Username.
   - `UserPassword` (varchar(255)): Password (Hashed).

6. **Order**
   - `OrderID` (PK, int).
   - `UserID` (FK, int): Customer who purchased.
   - `OrderCode` (varchar(10), unique): Format "DH" + 8 random digits.
   - `OrderDate` (date).
   - `OrderStatus` (varchar): "Đã đặt", "Đang xử lý", "Đã giao", "Đã hủy": ENUM(New, Processing, Delivered, Cancelled).
   - `OrderAmount` (DECIMAL(15,2)): Total Amount.
   - `ShippingAddress` (varchar): Delivery address at the time of order (Snapshot).
   - `ShippingPhone` (varchar): Receiver's phone at the time of order (Snapshot).
   - `PaymentDate` (date).
   - `PaymentMethod` (varchar): "Tiền mặt", "Chuyển khoản", "Thẻ tín dụng" ENUM('CASH', 'BANK_TRANSFER', 'CARD').

7. **OrderItem** (Order Details)
   - `OrderItemID` (PK, int).
   - `OrderID` (FK, int).
   - `ProductID` (FK, int).
   - `ItemQuantity` (int): Purchased quantity.
   - `ItemPrice` (DECIMAL(15,2)): Selling price at the time of purchase.
   - `UnitPrice` (float): Original Unit Price.

### 3.2. Relationships
- **Brand - Product:** 1 - n (One brand has many products).
- **Product - Image:** 1 - n (One product has many images).
- **Role - User:** 1 - n (One role has many users).
- **User - Order:** 1 - n (One customer has many orders).
- **Order - OrderItem:** 1 - n (One order has many details).
- **Product - OrderItem:** 1 - n.

## 4. Functional Requirements

### 4.1. Product Management
- CRUD Product: Create, Read, Update, Delete (soft delete), View list.
- Brand Management: CRUD brand information.
- Image Management: Upload images for products.
- Logic: When deleting a product, check if the product exists in any unprocessed orders.

### 4.2. Customer Management
- CRUD Customer: Create, Read, Update, Delete (soft delete or flag), View details.
- Authentication: Customer info includes Name, Address, Phone, Email.

### 4.3. Order Management
- Create Order: Select product -> Calculate total -> Save Order & OrderItem.
- Update Status: Admin updates workflow (New -> Processing -> Delivered).
- Cancel Order: Only allow cancellation when status is "New" or "Processing".

### 4.4. Payment Management
- Record payment method and payment date.
- Calculate related costs (if any).

### 4.5. Auto-generated Code Logic
- **Structure:** `PREFIX` + `8 RANDOM DIGITS` (e.g., SP09123842).
- **Rules:**
    - If the generated 8 digits duplicate an existing record in DB -> Regenerate.
    - If generated number < 8 digits -> Pad with leading zeros.
- **Prefixes:**
    - Brand: "TH"
    - Product: "SP"
    - Order: "DH"
    - User: Depends on Role (Admin -> "AD", Staff -> "NV", Customer -> "KH").

### 4.6. Data Constraints - Validation
*Apply strictly to Input DTOs:*
1.  **Full Name:** Only contains letters and spaces (RBHT).
2.  **Phone:** Exactly 10 or 11 digits (RBSDT).
3.  **Price:** Must be a positive number (RBGTN).
4.  **Quantity:** Cannot be negative (RBSL).
5.  **Gender:** Only accept "Male", "Female", "Other" (RBGTH). In UI map to "Nam", "Nữ", "Khác".
6.  **Order Status:** Only accept "New", "Processing", "Delivered", "Cancelled" (RBTTDH). In UI map to "Mới", "Đang xử lý", "Đã giao", "Đã hủy".
7.  **Payment Method:** Only accept "CASH", "BANK_TRANSFER", "CARD" (RBPTTT). In UI map to "Tiền mặt", "Chuyển khoản", "Thẻ".
8. **Product Status:** Only accept "Not Sold", "Selling", "Out of Stock", "Discontinued" (RBTTSP). In UI map to "Chưa bán", "Đang bán", "Hết hàng", "Ngừng kinh doanh".

### 4.7. Authentication & Authorization (New)
*Secure access control for the system.*

#### A. Registration (Register)
- **Target User:** Guest (Unauthenticated users).
- **Process:**
    1.  User submits: `Full Name`, `Phone`, `Username`, `Password`.
    2.  System validates:
        - Check if `UserAccount` (Username) already exists.
        - Check if `UserPhone` already exists.
    3.  System logic:
        - Assign Default Role: **Customer (CUS)**.
        - Generate `UserCode` with prefix "KH" (using Auto-generated Code Logic).
        - **Hash Password** (e.g., using BCrypt) before saving to DB. Do NOT store plain text.
    4.  Create User record.

#### B. Login
- **Target User:** All users (Admin, Staff, Customer).
- **Process:**
    1.  User submits: `UserAccount`, `UserPassword`.
    2.  System verifies:
        - Find user by `UserAccount`.
        - Match input password with stored **Hashed Password**.
    3.  Output:
        - If success: Return User Information (Name, Code, Role) and Status 200.
        - If fail: Return Error Message ("Invalid credentials") and Status 401.

#### C. Authorization Rules
- **Guest:** Can view Products, Register, Login.
- **Customer:** Can View Products, Add to Cart, Place Order, View History.
- **Staff:** Manage Orders, Manage Products (if allowed).
- **Admin:** Full access (Manage Users, Revenue, Settings).

## 5. API & Architecture Standards

### 5.1. Backend (Spring Boot)
- Clear folder structure:
```text
src/main
├── configs/           # Configuration (SecurityConfig, Swagger, Cors)
├── controllers/       # API Layer (REST Controllers)
├── dtos/              # Data Transfer Objects (Request/Response)
│   ├── request/
│   └── response/
├── entities/          # JPA Entities (Mapping with Database)
├── enums/  
├── exceptions/        # Global Exception Handling
├── mappers/
├── filters/
├── repositories/      # Data Access Layer (JPA Repositories)
├── services/          # Business Logic Layer
│   └── impl/    # Service Implementation
└── utils/             # Utility functions (JwtUtil, CodeGenerator)
```
- API Response Standard: All APIs must return a unified JSON format:
```bash
{
    "status": 200,
    "message": "Success",
    "data": { ... }
}
```
- Naming Convention: CamelCase for Java, Snake_case for Database columns.

- **Controller Layer Update:**
    - `AuthController.java`: Handle `/api/v1/auth/login` and `/api/v1/auth/register`.
    - `ProductController.java`: ...
    - `OrderController.java`: ...

- **DTO Requirements Update:**
    - `RegisterRequestDTO`:
        - `UserName` (RBHT: letters only).
        - `UserPhone` (RBSDT: 10-11 digits).
        - `UserAccount` (NotNull).
        - `UserPassword` (NotNull, Min 6 chars).
    - `LoginRequestDTO`:
        - `UserAccount`.
        - `UserPassword`.
    - `AuthResponseDTO`:
        - `UserCode`, `UserName`, `RoleName`, `AccessToken` (if using JWT).

### 5.2. Frontend (ReactJS)

Folder structure:
```text
src/
├── api/             # Axios config and endpoints
├── components/      # Reusable components (Button, Input, Layout)
├── pages/           # Main pages (ProductPage, CartPage)
├── context/         # React Context or Redux store
└── utils/           # Helper functions
```
### 5.3. Deployment
- Use docker-compose.yml to run:
    - 1 Container for PostgreSQL.
    - 1 Container for Spring Boot Backend.
    - 1 Container for ReactJS (Build static or Dev mode).
    - 1 Container Nginx as Gateway.