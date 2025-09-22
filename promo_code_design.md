# Promo Code Functionality Design

## Overview
Add promo code functionality to the cart system allowing customers to apply registered promo codes for discounts.

## PromoCode Entity Design

### Fields
- `id: Long` - Primary key
- `code: String` - Unique promo code (e.g., "SAVE10", "WELCOME20")
- `description: String?` - Optional description for admin purposes
- `discountType: DiscountType` - PERCENTAGE or FIXED_AMOUNT
- `discountValue: BigDecimal` - Percentage (0-100) or fixed amount
- `minimumOrderAmount: BigDecimal?` - Optional minimum order requirement
- `maxUsageCount: Int?` - Optional maximum usage limit (null = unlimited)
- `currentUsageCount: Int` - Current usage count (default 0)
- `validFrom: LocalDateTime` - Start date/time
- `validUntil: LocalDateTime` - End date/time
- `isActive: Boolean` - Active status (default true)
- `createdAt: LocalDateTime` - Creation timestamp
- `updatedAt: LocalDateTime` - Last update timestamp

### Enum: DiscountType
- `PERCENTAGE` - Discount as percentage of subtotal
- `FIXED_AMOUNT` - Fixed discount amount

## Cart Entity Updates

### New Fields
- `promoCodeId: Long?` - Reference to applied promo code (nullable)
- `promoCodeCode: String?` - Snapshot of promo code for reference
- `discountAmount: BigDecimal` - Calculated discount amount (default 0.00)

### Updated Total Calculation
```
total = subtotal - discountAmount + shippingCost
```

## Database Schema Changes

### New Table: promo_codes
```sql
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
```

### Cart Table Updates
```sql
ALTER TABLE carts ADD COLUMN promo_code_id BIGINT REFERENCES promo_codes(id);
ALTER TABLE carts ADD COLUMN promo_code_code VARCHAR(50);
ALTER TABLE carts ADD COLUMN discount_amount DECIMAL(10,2) NOT NULL DEFAULT 0.00;
```

## Service Layer Design

### PromoCodeService
- `validatePromoCode(code: String, cartSubtotal: BigDecimal): PromoCodeValidationResult`
- `calculateDiscount(promoCode: PromoCode, cartSubtotal: BigDecimal): BigDecimal`
- `applyPromoCode(promoCode: PromoCode): PromoCode` - Increment usage count
- `findActivePromoCodeByCode(code: String): PromoCode?`

### PromoCodeValidationResult
- `isValid: Boolean`
- `promoCode: PromoCode?`
- `errorMessage: String?`
- `discountAmount: BigDecimal`

### CartService Updates
- `applyPromoCode(cartId: Long, promoCode: String): Cart`
- `removePromoCode(cartId: Long): Cart`
- Update `recalculateCartTotals()` to include discount calculation

## API Endpoints

### Cart Controller Updates
- `POST /api/cart/{cartId}/promo-code` - Apply promo code
- `DELETE /api/cart/{cartId}/promo-code` - Remove promo code

### Request/Response DTOs
- `ApplyPromoCodeRequest(code: String)`
- `PromoCodeDto(code: String, discountAmount: BigDecimal, description: String?)`

## UI Updates

### Cart Template
- Add promo code input field in cart summary section
- Display applied promo code and discount amount
- Show promo code validation errors
- Add remove promo code functionality

### Cart Summary Structure
```
Subtotal: $XX.XX
Promo Code (CODE): -$XX.XX
Shipping: $XX.XX
Total: $XX.XX
```

## Validation Rules

1. **Code Format**: Alphanumeric, 3-50 characters
2. **Active Status**: Must be active
3. **Date Range**: Current date/time must be within validFrom and validUntil
4. **Usage Limit**: If maxUsageCount is set, currentUsageCount must be less than max
5. **Minimum Order**: If minimumOrderAmount is set, cart subtotal must meet requirement
6. **One Code Per Cart**: Only one promo code can be applied at a time

## Error Handling

### Validation Errors
- "Promo code not found"
- "Promo code has expired"
- "Promo code is not yet valid"
- "Promo code usage limit exceeded"
- "Minimum order amount not met (requires $XX.XX)"
- "Promo code is not active"

## Testing Strategy

### Unit Tests
- PromoCodeService validation logic
- Discount calculation for percentage and fixed amount
- Cart total recalculation with promo codes
- Edge cases (expired codes, usage limits, minimum orders)

### Integration Tests
- API endpoints for applying/removing promo codes
- Database operations and constraints
- End-to-end cart flow with promo codes