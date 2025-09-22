-- Add promo code support to carts table

-- Add promo code fields to carts table
ALTER TABLE carts ADD COLUMN promo_code_id BIGINT;
ALTER TABLE carts ADD COLUMN promo_code_code VARCHAR(50);
ALTER TABLE carts ADD COLUMN discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;

-- Add foreign key constraint to promo_codes table
ALTER TABLE carts ADD CONSTRAINT fk_carts_promo_code 
    FOREIGN KEY (promo_code_id) REFERENCES promo_codes(id) ON DELETE SET NULL;

-- Add constraints for data integrity
ALTER TABLE carts ADD CONSTRAINT chk_carts_discount_amount_non_negative 
    CHECK (discount_amount >= 0);

-- Add constraint to ensure promo_code_id and promo_code_code are consistent
-- If promo_code_id is set, promo_code_code should also be set and vice versa
ALTER TABLE carts ADD CONSTRAINT chk_carts_promo_code_consistency 
    CHECK (
        (promo_code_id IS NULL AND promo_code_code IS NULL) OR 
        (promo_code_id IS NOT NULL AND promo_code_code IS NOT NULL)
    );

-- Create indexes for performance
CREATE INDEX idx_carts_promo_code_id ON carts(promo_code_id);
CREATE INDEX idx_carts_promo_code_code ON carts(promo_code_code);
CREATE INDEX idx_carts_with_promo_codes ON carts(promo_code_id) WHERE promo_code_id IS NOT NULL;

-- Add comments for documentation
COMMENT ON COLUMN carts.promo_code_id IS 'Reference to the applied promo code (nullable)';
COMMENT ON COLUMN carts.promo_code_code IS 'Snapshot of the promo code string for reference';
COMMENT ON COLUMN carts.discount_amount IS 'Calculated discount amount applied to this cart';

-- Insert sample promo codes for testing
INSERT INTO promo_codes (code, description, discount_type, discount_value, minimum_order_amount, max_usage_count, current_usage_count, valid_from, valid_until, is_active) VALUES
('WELCOME10', '10% off for new customers', 'PERCENTAGE', 10.00, NULL, NULL, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true),
('SAVE5', '£5 off any order', 'FIXED_AMOUNT', 5.00, NULL, NULL, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true),
('BIGORDER', '£10 off orders over £50', 'FIXED_AMOUNT', 10.00, 50.00, NULL, 0, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true),
('LIMITED20', '20% off - limited use', 'PERCENTAGE', 20.00, NULL, 100, 5, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true),
('EXPIRED', 'Expired promo code', 'PERCENTAGE', 15.00, NULL, NULL, 0, '2023-01-01 00:00:00', '2023-12-31 23:59:59', true),
('TEATIME', '15% off tea orders', 'PERCENTAGE', 15.00, 25.00, 500, 12, '2024-01-01 00:00:00', '2024-12-31 23:59:59', true);