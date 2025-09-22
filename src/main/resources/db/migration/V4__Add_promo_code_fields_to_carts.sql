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