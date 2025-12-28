-- ========================================
-- WATCH STORE DATABASE - ALTER TABLE MIGRATION
-- PostgreSQL Update Script (For Existing Database)
-- Generated: December 17, 2025
-- ========================================

-- ========================================
-- STEP 1: ADD MISSING PRODUCT_STATUS COLUMN
-- ========================================
-- Check if column exists, if not add it
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'products' AND column_name = 'product_status'
    ) THEN
        ALTER TABLE products 
        ADD COLUMN product_status VARCHAR(50) NOT NULL DEFAULT 'NOT_SOLD' 
        CHECK (product_status IN ('NOT_SOLD', 'SELLING', 'OUT_OF_STOCK', 'DISCONTINUED'));
        
        RAISE NOTICE 'Added product_status column to products table';
    ELSE
        RAISE NOTICE 'product_status column already exists';
    END IF;
END $$;

-- ========================================
-- STEP 2: ADD EXPLICIT COLUMN NAMES (IF NOT EXIST)
-- ========================================

-- BRANDS TABLE
ALTER TABLE brands 
    ALTER COLUMN brand_code SET NOT NULL,
    ADD CONSTRAINT uq_brand_code UNIQUE (brand_code);

ALTER TABLE brands
    ALTER COLUMN brand_name SET NOT NULL;

-- PRODUCTS TABLE
ALTER TABLE products
    ALTER COLUMN product_code SET NOT NULL,
    ADD CONSTRAINT uq_product_code UNIQUE (product_code);

ALTER TABLE products
    ALTER COLUMN product_name SET NOT NULL,
    ALTER COLUMN product_price SET NOT NULL,
    ALTER COLUMN quantity_stock SET NOT NULL;

-- Add check constraints
ALTER TABLE products
    ADD CONSTRAINT chk_product_price_positive CHECK (product_price >= 0);

ALTER TABLE products
    ADD CONSTRAINT chk_quantity_stock_nonnegative CHECK (quantity_stock >= 0);

-- USERS TABLE
ALTER TABLE users
    ALTER COLUMN user_code SET NOT NULL,
    ADD CONSTRAINT uq_user_code UNIQUE (user_code);

ALTER TABLE users
    ALTER COLUMN user_name SET NOT NULL,
    ALTER COLUMN user_account SET NOT NULL,
    ALTER COLUMN user_password SET NOT NULL,
    ADD CONSTRAINT uq_user_account UNIQUE (user_account);

-- Add phone validation constraint
ALTER TABLE users
    ADD CONSTRAINT chk_user_phone_format CHECK (user_phone ~ '^[0-9]{10,11}$');

-- ORDERS TABLE
ALTER TABLE orders
    ALTER COLUMN order_code SET NOT NULL,
    ADD CONSTRAINT uq_order_code UNIQUE (order_code);

ALTER TABLE orders
    ALTER COLUMN order_date SET NOT NULL,
    ALTER COLUMN order_status SET NOT NULL,
    ALTER COLUMN order_amount SET NOT NULL,
    ALTER COLUMN shipping_address SET NOT NULL,
    ALTER COLUMN shipping_phone SET NOT NULL;

-- Add check constraints
ALTER TABLE orders
    ADD CONSTRAINT chk_order_amount_nonnegative CHECK (order_amount >= 0);

ALTER TABLE orders
    ADD CONSTRAINT chk_shipping_phone_format CHECK (shipping_phone ~ '^[0-9]{10,11}$');

-- ROLES TABLE
ALTER TABLE roles
    ALTER COLUMN role_code SET NOT NULL,
    ALTER COLUMN role_name SET NOT NULL,
    ADD CONSTRAINT uq_role_code UNIQUE (role_code);

-- IMAGES TABLE
ALTER TABLE images
    ALTER COLUMN image_name SET NOT NULL,
    ALTER COLUMN image_url SET NOT NULL;

-- Change image_url to TEXT type if it's not already
DO $$
BEGIN
    IF EXISTS (
        SELECT 1 FROM information_schema.columns 
        WHERE table_name = 'images' 
        AND column_name = 'image_url' 
        AND data_type != 'text'
    ) THEN
        ALTER TABLE images ALTER COLUMN image_url TYPE TEXT;
        RAISE NOTICE 'Changed image_url column to TEXT type';
    END IF;
END $$;

-- ORDER_ITEMS TABLE
ALTER TABLE order_items
    ALTER COLUMN item_quantity SET NOT NULL,
    ALTER COLUMN item_price SET NOT NULL,
    ALTER COLUMN unit_price SET NOT NULL;

-- Add check constraints
ALTER TABLE order_items
    ADD CONSTRAINT chk_item_quantity_positive CHECK (item_quantity > 0);

ALTER TABLE order_items
    ADD CONSTRAINT chk_item_price_nonnegative CHECK (item_price >= 0);

ALTER TABLE order_items
    ADD CONSTRAINT chk_unit_price_nonnegative CHECK (unit_price >= 0);

-- ========================================
-- STEP 3: ENSURE ENUM CONSTRAINTS
-- ========================================

-- Gender enum constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.constraint_column_usage
        WHERE table_name = 'users' AND constraint_name = 'chk_user_gender'
    ) THEN
        ALTER TABLE users
        ADD CONSTRAINT chk_user_gender CHECK (user_gender IN ('MALE', 'FEMALE', 'OTHER'));
    END IF;
END $$;

-- Order status enum constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.constraint_column_usage
        WHERE table_name = 'orders' AND constraint_name = 'chk_order_status'
    ) THEN
        ALTER TABLE orders
        ADD CONSTRAINT chk_order_status CHECK (order_status IN ('NEW', 'PROCESSING', 'DELIVERED', 'CANCELLED'));
    END IF;
END $$;

-- Payment method enum constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.constraint_column_usage
        WHERE table_name = 'orders' AND constraint_name = 'chk_payment_method'
    ) THEN
        ALTER TABLE orders
        ADD CONSTRAINT chk_payment_method CHECK (payment_method IN ('CASH', 'BANK_TRANSFER', 'CARD'));
    END IF;
END $$;

-- Product status enum constraint
DO $$
BEGIN
    IF NOT EXISTS (
        SELECT 1 FROM information_schema.constraint_column_usage
        WHERE table_name = 'products' AND constraint_name = 'chk_product_status'
    ) THEN
        ALTER TABLE products
        ADD CONSTRAINT chk_product_status CHECK (product_status IN ('NOT_SOLD', 'SELLING', 'OUT_OF_STOCK', 'DISCONTINUED'));
    END IF;
END $$;

-- ========================================
-- STEP 4: CREATE INDEXES (IF NOT EXIST)
-- ========================================

-- Products indexes
CREATE INDEX IF NOT EXISTS idx_products_brand_id ON products(brand_id);
CREATE INDEX IF NOT EXISTS idx_products_product_code ON products(product_code);
CREATE INDEX IF NOT EXISTS idx_products_product_status ON products(product_status);

-- Images indexes
CREATE INDEX IF NOT EXISTS idx_images_product_id ON images(product_id);

-- Users indexes
CREATE INDEX IF NOT EXISTS idx_users_role_id ON users(role_id);
CREATE INDEX IF NOT EXISTS idx_users_user_code ON users(user_code);
CREATE INDEX IF NOT EXISTS idx_users_user_account ON users(user_account);

-- Orders indexes
CREATE INDEX IF NOT EXISTS idx_orders_user_id ON orders(user_id);
CREATE INDEX IF NOT EXISTS idx_orders_order_code ON orders(order_code);
CREATE INDEX IF NOT EXISTS idx_orders_order_status ON orders(order_status);
CREATE INDEX IF NOT EXISTS idx_orders_order_date ON orders(order_date);

-- Order items indexes
CREATE INDEX IF NOT EXISTS idx_order_items_order_id ON order_items(order_id);
CREATE INDEX IF NOT EXISTS idx_order_items_product_id ON order_items(product_id);

-- ========================================
-- STEP 5: INSERT DEFAULT ROLES (IF NOT EXIST)
-- ========================================
INSERT INTO roles (role_code, role_name)
SELECT 'ADM', 'Administrator'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_code = 'ADM');

INSERT INTO roles (role_code, role_name)
SELECT 'STF', 'Staff'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_code = 'STF');

INSERT INTO roles (role_code, role_name)
SELECT 'CUS', 'Customer'
WHERE NOT EXISTS (SELECT 1 FROM roles WHERE role_code = 'CUS');

-- ========================================
-- VERIFICATION QUERIES
-- ========================================
-- Run these to verify the changes

-- Check all tables exist
SELECT table_name 
FROM information_schema.tables 
WHERE table_schema = 'public' 
AND table_type = 'BASE TABLE'
ORDER BY table_name;

-- Check all constraints on products
SELECT constraint_name, constraint_type
FROM information_schema.table_constraints
WHERE table_name = 'products';

-- Check all columns in products table
SELECT column_name, data_type, is_nullable, column_default
FROM information_schema.columns
WHERE table_name = 'products'
ORDER BY ordinal_position;

-- Check roles data
SELECT * FROM roles;

-- ========================================
-- END OF MIGRATION
-- ========================================

-- Note: If you encounter errors about constraints already existing,
-- you can safely ignore them as the script checks for existence first.
