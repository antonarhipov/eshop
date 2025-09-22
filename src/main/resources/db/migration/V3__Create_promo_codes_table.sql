-- Create promo codes table for discount functionality

CREATE TABLE promo_codes (
    id BIGSERIAL PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE,
    description VARCHAR(255),
    discount_type VARCHAR(20) NOT NULL,
    discount_value DECIMAL(10,2) NOT NULL,
    minimum_order_amount DECIMAL(10,2),
    max_usage_count INTEGER,
    current_usage_count INTEGER NOT NULL DEFAULT 0,
    valid_from TIMESTAMP NOT NULL,
    valid_until TIMESTAMP NOT NULL,
    is_active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

-- Create indexes for common queries
CREATE INDEX idx_promo_codes_code ON promo_codes(code);
CREATE INDEX idx_promo_codes_active ON promo_codes(is_active);
CREATE INDEX idx_promo_codes_valid_dates ON promo_codes(valid_from, valid_until);
CREATE INDEX idx_promo_codes_active_valid ON promo_codes(is_active, valid_from, valid_until);
CREATE INDEX idx_promo_codes_usage ON promo_codes(current_usage_count, max_usage_count);
CREATE INDEX idx_promo_codes_discount_type ON promo_codes(discount_type);

-- Add constraints for enum values and business rules
ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_discount_type 
    CHECK (discount_type IN ('PERCENTAGE', 'FIXED_AMOUNT'));

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_discount_value_positive 
    CHECK (discount_value > 0);

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_percentage_max 
    CHECK (discount_type != 'PERCENTAGE' OR discount_value <= 100);

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_minimum_order_positive 
    CHECK (minimum_order_amount IS NULL OR minimum_order_amount > 0);

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_max_usage_positive 
    CHECK (max_usage_count IS NULL OR max_usage_count > 0);

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_current_usage_non_negative 
    CHECK (current_usage_count >= 0);

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_usage_not_exceed_max 
    CHECK (max_usage_count IS NULL OR current_usage_count <= max_usage_count);

ALTER TABLE promo_codes ADD CONSTRAINT chk_promo_codes_valid_date_range 
    CHECK (valid_from < valid_until);

-- Add comment for documentation
COMMENT ON TABLE promo_codes IS 'Stores promotional discount codes with validation rules and usage tracking';
COMMENT ON COLUMN promo_codes.code IS 'Unique promotional code string (e.g., SAVE10, WELCOME20)';
COMMENT ON COLUMN promo_codes.discount_type IS 'Type of discount: PERCENTAGE or FIXED_AMOUNT';
COMMENT ON COLUMN promo_codes.discount_value IS 'Discount value: percentage (0-100) or fixed amount';
COMMENT ON COLUMN promo_codes.minimum_order_amount IS 'Optional minimum order amount required to use this promo code';
COMMENT ON COLUMN promo_codes.max_usage_count IS 'Optional maximum number of times this code can be used';
COMMENT ON COLUMN promo_codes.current_usage_count IS 'Current number of times this code has been used';