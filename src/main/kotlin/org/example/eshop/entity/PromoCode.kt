package org.example.eshop.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.math.RoundingMode
import java.time.LocalDateTime

enum class DiscountType {
    PERCENTAGE,
    FIXED_AMOUNT
}

@Entity
@Table(name = "promo_codes")
data class PromoCode(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, unique = true, length = 50)
    val code: String,

    @Column(length = 255)
    val description: String? = null,

    @Enumerated(EnumType.STRING)
    @Column(name = "discount_type", nullable = false, length = 20)
    val discountType: DiscountType,

    @Column(name = "discount_value", nullable = false, precision = 10, scale = 2)
    val discountValue: BigDecimal,

    @Column(name = "minimum_order_amount", precision = 10, scale = 2)
    val minimumOrderAmount: BigDecimal? = null,

    @Column(name = "max_usage_count")
    val maxUsageCount: Int? = null,

    @Column(name = "current_usage_count", nullable = false)
    var currentUsageCount: Int = 0,

    @Column(name = "valid_from", nullable = false)
    val validFrom: LocalDateTime,

    @Column(name = "valid_until", nullable = false)
    val validUntil: LocalDateTime,

    @Column(name = "is_active", nullable = false)
    val isActive: Boolean = true,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    /**
     * Checks if the promo code is currently valid based on date range and active status
     */
    fun isCurrentlyValid(): Boolean {
        val now = LocalDateTime.now()
        return isActive && now.isAfter(validFrom) && now.isBefore(validUntil)
    }

    /**
     * Checks if the promo code has reached its usage limit
     */
    fun hasReachedUsageLimit(): Boolean {
        return maxUsageCount != null && currentUsageCount >= maxUsageCount
    }

    /**
     * Checks if the cart subtotal meets the minimum order requirement
     */
    fun meetsMinimumOrderAmount(cartSubtotal: BigDecimal): Boolean {
        return minimumOrderAmount == null || cartSubtotal >= minimumOrderAmount
    }

    /**
     * Calculates the discount amount for the given cart subtotal
     */
    fun calculateDiscount(cartSubtotal: BigDecimal): BigDecimal {
        return when (discountType) {
            DiscountType.PERCENTAGE -> {
                cartSubtotal.multiply(discountValue).divide(BigDecimal(100), 2, RoundingMode.HALF_UP)
            }
            DiscountType.FIXED_AMOUNT -> {
                // Ensure discount doesn't exceed cart subtotal
                if (discountValue > cartSubtotal) cartSubtotal else discountValue
            }
        }
    }

    /**
     * Increments the usage count
     */
    fun incrementUsage() {
        currentUsageCount++
        updatedAt = LocalDateTime.now()
    }
}