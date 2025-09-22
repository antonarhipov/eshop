package org.example.eshop.repository

import org.example.eshop.entity.PromoCode
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.data.jpa.repository.Query
import org.springframework.data.repository.query.Param
import org.springframework.stereotype.Repository
import java.time.LocalDateTime

@Repository
interface PromoCodeRepository : JpaRepository<PromoCode, Long> {

    /**
     * Find an active promo code by its code string
     */
    @Query("""
        SELECT p FROM PromoCode p 
        WHERE p.code = :code 
        AND p.isActive = true
    """)
    fun findActiveByCode(@Param("code") code: String): PromoCode?

    /**
     * Find a promo code by its code string (regardless of active status)
     */
    fun findByCode(code: String): PromoCode?

    /**
     * Find all currently valid promo codes (active and within date range)
     */
    @Query("""
        SELECT p FROM PromoCode p 
        WHERE p.isActive = true 
        AND p.validFrom <= :now 
        AND p.validUntil > :now
    """)
    fun findCurrentlyValid(@Param("now") now: LocalDateTime = LocalDateTime.now()): List<PromoCode>

    /**
     * Find all active promo codes
     */
    fun findByIsActiveTrue(): List<PromoCode>

    /**
     * Find promo codes that are about to expire (within specified hours)
     */
    @Query("""
        SELECT p FROM PromoCode p 
        WHERE p.isActive = true 
        AND p.validUntil > :now 
        AND p.validUntil <= :expiryThreshold
    """)
    fun findExpiringWithin(
        @Param("now") now: LocalDateTime = LocalDateTime.now(),
        @Param("expiryThreshold") expiryThreshold: LocalDateTime
    ): List<PromoCode>

    /**
     * Find promo codes that have reached their usage limit
     */
    @Query("""
        SELECT p FROM PromoCode p 
        WHERE p.maxUsageCount IS NOT NULL 
        AND p.currentUsageCount >= p.maxUsageCount
    """)
    fun findUsageLimitReached(): List<PromoCode>

    /**
     * Check if a promo code exists and is currently valid
     */
    @Query("""
        SELECT CASE WHEN COUNT(p) > 0 THEN true ELSE false END 
        FROM PromoCode p 
        WHERE p.code = :code 
        AND p.isActive = true 
        AND p.validFrom <= :now 
        AND p.validUntil > :now
    """)
    fun existsValidPromoCode(
        @Param("code") code: String,
        @Param("now") now: LocalDateTime = LocalDateTime.now()
    ): Boolean

    /**
     * Find promo codes by discount type
     */
    fun findByDiscountTypeAndIsActiveTrue(discountType: org.example.eshop.entity.DiscountType): List<PromoCode>
}