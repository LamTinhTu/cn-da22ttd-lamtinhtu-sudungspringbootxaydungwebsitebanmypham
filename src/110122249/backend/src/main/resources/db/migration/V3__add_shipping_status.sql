-- Migration to add SHIPPING status to order_status enum
-- This updates the check constraint to include the new SHIPPING status

-- Drop the existing constraint
ALTER TABLE orders DROP CONSTRAINT IF EXISTS orders_order_status_check;

-- Add the updated constraint with SHIPPING status
ALTER TABLE orders
    ADD CONSTRAINT orders_order_status_check 
    CHECK (order_status IN ('NEW', 'PROCESSING', 'SHIPPING', 'DELIVERED', 'CANCELLED'));
