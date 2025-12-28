-- ========================================
-- WATCH STORE DATABASE SCHEMA
-- PostgreSQL Migration Script
-- Generated: December 17, 2025
-- ========================================

-- Drop existing tables if they exist (for clean slate)
DROP TABLE IF EXISTS order_items CASCADE;
DROP TABLE IF EXISTS orders CASCADE;
DROP TABLE IF EXISTS images CASCADE;
DROP TABLE IF EXISTS products CASCADE;
DROP TABLE IF EXISTS brands CASCADE;
DROP TABLE IF EXISTS users CASCADE;
DROP TABLE IF EXISTS roles CASCADE;

-- ========================================
-- 1. CREATE ROLES TABLE
-- ========================================
CREATE TABLE roles (
    role_id SERIAL PRIMARY KEY,
    role_code VARCHAR(10) UNIQUE NOT NULL,
    role_name VARCHAR(100) NOT NULL
);

-- Insert default roles
INSERT INTO roles (role_code, role_name) VALUES
('ADM', 'Administrator'),
('STF', 'Staff'),
('CUS', 'Customer');

-- ========================================
-- 2. CREATE BRANDS TABLE
-- ========================================
CREATE TABLE brands (
    brand_id SERIAL PRIMARY KEY,
    brand_code VARCHAR(10) UNIQUE NOT NULL,
    brand_name VARCHAR(255) NOT NULL,
    brand_description TEXT,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

-- ========================================
-- 3. CREATE PRODUCTS TABLE
-- ========================================
CREATE TABLE products (
    product_id SERIAL PRIMARY KEY,
    brand_id INTEGER NOT NULL,
    product_code VARCHAR(10) UNIQUE NOT NULL,
    product_name VARCHAR(255) NOT NULL,
    product_description TEXT,
    product_price DECIMAL(15,2) NOT NULL CHECK (product_price >= 0),
    quantity_stock INTEGER NOT NULL DEFAULT 0 CHECK (quantity_stock >= 0),
    product_status VARCHAR(50) NOT NULL CHECK (product_status IN ('NOT_SOLD', 'SELLING', 'OUT_OF_STOCK', 'DISCONTINUED')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (brand_id) REFERENCES brands(brand_id) ON DELETE RESTRICT
);

-- ========================================
-- 4. CREATE IMAGES TABLE
-- ========================================
CREATE TABLE images (
    image_id SERIAL PRIMARY KEY,
    product_id INTEGER NOT NULL,
    image_name VARCHAR(255) NOT NULL,
    image_url TEXT NOT NULL,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE CASCADE
);

-- ========================================
-- 5. CREATE USERS TABLE
-- ========================================
CREATE TABLE users (
    user_id SERIAL PRIMARY KEY,
    role_id INTEGER NOT NULL,
    user_code VARCHAR(10) UNIQUE NOT NULL,
    user_name VARCHAR(255) NOT NULL,
    user_gender VARCHAR(10) CHECK (user_gender IN ('MALE', 'FEMALE', 'OTHER')),
    user_birth_date DATE,
    user_address VARCHAR(500),
    user_phone VARCHAR(11) CHECK (user_phone ~ '^[0-9]{10,11}$'),
    user_account VARCHAR(100) UNIQUE NOT NULL,
    user_password VARCHAR(255) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (role_id) REFERENCES roles(role_id) ON DELETE RESTRICT
);

-- ========================================
-- 6. CREATE ORDERS TABLE
-- ========================================
CREATE TABLE orders (
    order_id SERIAL PRIMARY KEY,
    user_id INTEGER NOT NULL,
    order_code VARCHAR(10) UNIQUE NOT NULL,
    order_date DATE NOT NULL,
    order_status VARCHAR(20) NOT NULL CHECK (order_status IN ('NEW', 'PROCESSING', 'DELIVERED', 'CANCELLED')),
    order_amount DECIMAL(15,2) NOT NULL CHECK (order_amount >= 0),
    shipping_address VARCHAR(500) NOT NULL,
    shipping_phone VARCHAR(11) NOT NULL CHECK (shipping_phone ~ '^[0-9]{10,11}$'),
    payment_date DATE,
    payment_method VARCHAR(20) CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'CARD')),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (user_id) REFERENCES users(user_id) ON DELETE RESTRICT
);

-- ========================================
-- 7. CREATE ORDER_ITEMS TABLE
-- ========================================
CREATE TABLE order_items (
    order_item_id SERIAL PRIMARY KEY,
    order_id INTEGER NOT NULL,
    product_id INTEGER NOT NULL,
    item_quantity INTEGER NOT NULL CHECK (item_quantity > 0),
    item_price DECIMAL(15,2) NOT NULL CHECK (item_price >= 0),
    unit_price DOUBLE PRECISION NOT NULL CHECK (unit_price >= 0),
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    FOREIGN KEY (order_id) REFERENCES orders(order_id) ON DELETE CASCADE,
    FOREIGN KEY (product_id) REFERENCES products(product_id) ON DELETE RESTRICT
);

-- ========================================
-- CREATE INDEXES FOR PERFORMANCE
-- ========================================
CREATE INDEX idx_products_brand_id ON products(brand_id);
CREATE INDEX idx_products_product_code ON products(product_code);
CREATE INDEX idx_products_product_status ON products(product_status);

CREATE INDEX idx_images_product_id ON images(product_id);

CREATE INDEX idx_users_role_id ON users(role_id);
CREATE INDEX idx_users_user_code ON users(user_code);
CREATE INDEX idx_users_user_account ON users(user_account);

CREATE INDEX idx_orders_user_id ON orders(user_id);
CREATE INDEX idx_orders_order_code ON orders(order_code);
CREATE INDEX idx_orders_order_status ON orders(order_status);
CREATE INDEX idx_orders_order_date ON orders(order_date);

CREATE INDEX idx_order_items_order_id ON order_items(order_id);
CREATE INDEX idx_order_items_product_id ON order_items(product_id);

-- ========================================
-- CREATE TRIGGER FOR UPDATED_AT TIMESTAMPS
-- ========================================
CREATE OR REPLACE FUNCTION update_updated_at_column()
RETURNS TRIGGER AS $$
BEGIN
    NEW.updated_at = CURRENT_TIMESTAMP;
    RETURN NEW;
END;
$$ language 'plpgsql';

CREATE TRIGGER update_brands_updated_at BEFORE UPDATE ON brands
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_products_updated_at BEFORE UPDATE ON products
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_users_updated_at BEFORE UPDATE ON users
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_orders_updated_at BEFORE UPDATE ON orders
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

CREATE TRIGGER update_order_items_updated_at BEFORE UPDATE ON order_items
    FOR EACH ROW EXECUTE FUNCTION update_updated_at_column();

-- ========================================
-- END OF SCHEMA
-- ========================================
