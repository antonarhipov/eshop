package org.example.eshop.service

import org.example.eshop.entity.PromoCode
import org.example.eshop.repository.PromoCodeRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.math.BigDecimal
import java.time.LocalDateTime

data class PromoCodeValidationResult(
    val isValid: Boolean,
    val promoCode: PromoCode? = null,
    val errorMessage: String? = null,
    val discountAmount: BigDecimal = BigDecimal.ZERO
)

@Service
@Transactional
class PromoCodeService(
    private val promoCodeRepository: PromoCodeRepository
) {

    /**
     * Validates a promo code and calculates the discount amount
     */
    fun validatePromoCode(code: String, cartSubtotal: BigDecimal): PromoCodeValidationResult {
        // Validate input
        if (code.isBlank()) {
            return PromoCodeValidationResult(
                isValid = false,
                errorMessage = "Promo code cannot be empty"
            )
        }

        if (code.length < 3 || code.length > 50) {
            return PromoCodeValidationResult(
                isValid = false,
                errorMessage = "Promo code must be between 3 and 50 characters"
            )
        }

        if (!code.matches(Regex("^[A-Za-z0-9]+$"))) {
            return PromoCodeValidationResult(
                isValid = false,
                errorMessage = "Promo code can only contain letters and numbers"
            )
        }

        // Find the promo code
        val promoCode = promoCodeRepository.findActiveByCode(code.uppercase())
            ?: return PromoCodeValidationResult(
                isValid = false,
                errorMessage = "Promo code not found"
            )

        // Check if promo code is currently valid (date range)
        if (!promoCode.isCurrentlyValid()) {
            val now = LocalDateTime.now()
            return when {
                now.isBefore(promoCode.validFrom) -> PromoCodeValidationResult(
                    isValid = false,
                    errorMessage = "Promo code is not yet valid"
                )
                now.isAfter(promoCode.validUntil) -> PromoCodeValidationResult(
                    isValid = false,
                    errorMessage = "Promo code has expired"
                )
                !promoCode.isActive -> PromoCodeValidationResult(
                    isValid = false,
                    errorMessage = "Promo code is not active"
                )
                else -> PromoCodeValidationResult(
                    isValid = false,
                    errorMessage = "Promo code is not valid"
                )
            }
        }

        // Check usage limit
        if (promoCode.hasReachedUsageLimit()) {
            return PromoCodeValidationResult(
                isValid = false,
                errorMessage = "Promo code usage limit exceeded"
            )
        }

        // Check minimum order amount
        if (!promoCode.meetsMinimumOrderAmount(cartSubtotal)) {
            val minAmount = promoCode.minimumOrderAmount!!
            return PromoCodeValidationResult(
                isValid = false,
                errorMessage = "Minimum order amount not met (requires $${minAmount})"
            )
        }

        // Calculate discount
        val discountAmount = calculateDiscount(promoCode, cartSubtotal)

        return PromoCodeValidationResult(
            isValid = true,
            promoCode = promoCode,
            discountAmount = discountAmount
        )
    }

    /**
     * Calculates the discount amount for a given promo code and cart subtotal
     */
    fun calculateDiscount(promoCode: PromoCode, cartSubtotal: BigDecimal): BigDecimal {
        return promoCode.calculateDiscount(cartSubtotal)
    }

    /**
     * Applies a promo code by incrementing its usage count
     */
    fun applyPromoCode(promoCode: PromoCode): PromoCode {
        promoCode.incrementUsage()
        return promoCodeRepository.save(promoCode)
    }

    /**
     * Finds an active promo code by its code string
     */
    @Transactional(readOnly = true)
    fun findActivePromoCodeByCode(code: String): PromoCode? {
        return promoCodeRepository.findActiveByCode(code.uppercase())
    }

    /**
     * Finds all currently valid promo codes
     */
    @Transactional(readOnly = true)
    fun findCurrentlyValidPromoCodes(): List<PromoCode> {
        return promoCodeRepository.findCurrentlyValid()
    }

    /**
     * Checks if a promo code exists and is currently valid
     */
    @Transactional(readOnly = true)
    fun isPromoCodeValid(code: String): Boolean {
        return promoCodeRepository.existsValidPromoCode(code.uppercase())
    }

    /**
     * Gets promo code statistics for admin purposes
     */
    @Transactional(readOnly = true)
    fun getPromoCodeStats(): Map<String, Any> {
        val allCodes = promoCodeRepository.findAll()
        val activeCodes = promoCodeRepository.findByIsActiveTrue()
        val currentlyValid = promoCodeRepository.findCurrentlyValid()
        val usageLimitReached = promoCodeRepository.findUsageLimitReached()

        return mapOf(
            "totalCodes" to allCodes.size,
            "activeCodes" to activeCodes.size,
            "currentlyValid" to currentlyValid.size,
            "usageLimitReached" to usageLimitReached.size,
            "totalUsage" to allCodes.sumOf { it.currentUsageCount }
        )
    }
}