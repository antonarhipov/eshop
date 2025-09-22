package org.example.eshop.service

import org.example.eshop.entity.DiscountType
import org.example.eshop.entity.PromoCode
import org.example.eshop.repository.PromoCodeRepository
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.Test
import org.junit.jupiter.api.extension.ExtendWith
import org.mockito.InjectMocks
import org.mockito.Mock
import org.mockito.junit.jupiter.MockitoExtension
import org.mockito.Mockito.`when`
import org.junit.jupiter.api.Assertions.*
import java.math.BigDecimal
import java.time.LocalDateTime

@ExtendWith(MockitoExtension::class)
class PromoCodeServiceTest {

    @Mock
    private lateinit var promoCodeRepository: PromoCodeRepository

    @InjectMocks
    private lateinit var promoCodeService: PromoCodeService

    private lateinit var validPercentagePromoCode: PromoCode
    private lateinit var validFixedAmountPromoCode: PromoCode
    private lateinit var expiredPromoCode: PromoCode
    private lateinit var usageLimitReachedPromoCode: PromoCode
    private lateinit var minimumOrderPromoCode: PromoCode

    @BeforeEach
    fun setUp() {
        val now = LocalDateTime.now()
        
        validPercentagePromoCode = PromoCode(
            id = 1L,
            code = "SAVE10",
            description = "10% off",
            discountType = DiscountType.PERCENTAGE,
            discountValue = BigDecimal("10.00"),
            minimumOrderAmount = null,
            maxUsageCount = null,
            currentUsageCount = 0,
            validFrom = now.minusDays(1),
            validUntil = now.plusDays(30),
            isActive = true
        )

        validFixedAmountPromoCode = PromoCode(
            id = 2L,
            code = "FIXED5",
            description = "£5 off",
            discountType = DiscountType.FIXED_AMOUNT,
            discountValue = BigDecimal("5.00"),
            minimumOrderAmount = null,
            maxUsageCount = null,
            currentUsageCount = 0,
            validFrom = now.minusDays(1),
            validUntil = now.plusDays(30),
            isActive = true
        )

        expiredPromoCode = PromoCode(
            id = 3L,
            code = "EXPIRED",
            description = "Expired code",
            discountType = DiscountType.PERCENTAGE,
            discountValue = BigDecimal("20.00"),
            minimumOrderAmount = null,
            maxUsageCount = null,
            currentUsageCount = 0,
            validFrom = now.minusDays(30),
            validUntil = now.minusDays(1),
            isActive = true
        )

        usageLimitReachedPromoCode = PromoCode(
            id = 4L,
            code = "LIMITED",
            description = "Limited use code",
            discountType = DiscountType.PERCENTAGE,
            discountValue = BigDecimal("15.00"),
            minimumOrderAmount = null,
            maxUsageCount = 10,
            currentUsageCount = 10,
            validFrom = now.minusDays(1),
            validUntil = now.plusDays(30),
            isActive = true
        )

        minimumOrderPromoCode = PromoCode(
            id = 5L,
            code = "MIN50",
            description = "£10 off orders over £50",
            discountType = DiscountType.FIXED_AMOUNT,
            discountValue = BigDecimal("10.00"),
            minimumOrderAmount = BigDecimal("50.00"),
            maxUsageCount = null,
            currentUsageCount = 0,
            validFrom = now.minusDays(1),
            validUntil = now.plusDays(30),
            isActive = true
        )
    }

    @Test
    fun `validatePromoCode should return valid result for valid percentage promo code`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")
        `when`(promoCodeRepository.findActiveByCode("SAVE10")).thenReturn(validPercentagePromoCode)

        // When
        val result = promoCodeService.validatePromoCode("SAVE10", cartSubtotal)

        // Then
        assertTrue(result.isValid)
        assertNotNull(result.promoCode)
        assertEquals(BigDecimal("10.00"), result.discountAmount)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return valid result for valid fixed amount promo code`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")
        `when`(promoCodeRepository.findActiveByCode("FIXED5")).thenReturn(validFixedAmountPromoCode)

        // When
        val result = promoCodeService.validatePromoCode("FIXED5", cartSubtotal)

        // Then
        assertTrue(result.isValid)
        assertNotNull(result.promoCode)
        assertEquals(BigDecimal("5.00"), result.discountAmount)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for non-existent promo code`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")
        `when`(promoCodeRepository.findActiveByCode("NONEXISTENT")).thenReturn(null)

        // When
        val result = promoCodeService.validatePromoCode("NONEXISTENT", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Promo code not found", result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for expired promo code`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")
        `when`(promoCodeRepository.findActiveByCode("EXPIRED")).thenReturn(expiredPromoCode)

        // When
        val result = promoCodeService.validatePromoCode("EXPIRED", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Promo code has expired", result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for usage limit reached`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")
        `when`(promoCodeRepository.findActiveByCode("LIMITED")).thenReturn(usageLimitReachedPromoCode)

        // When
        val result = promoCodeService.validatePromoCode("LIMITED", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Promo code usage limit exceeded", result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for minimum order not met`() {
        // Given
        val cartSubtotal = BigDecimal("30.00") // Less than £50 minimum
        `when`(promoCodeRepository.findActiveByCode("MIN50")).thenReturn(minimumOrderPromoCode)

        // When
        val result = promoCodeService.validatePromoCode("MIN50", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Minimum order amount not met (requires $50.00)", result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return valid result when minimum order is met`() {
        // Given
        val cartSubtotal = BigDecimal("75.00") // More than £50 minimum
        `when`(promoCodeRepository.findActiveByCode("MIN50")).thenReturn(minimumOrderPromoCode)

        // When
        val result = promoCodeService.validatePromoCode("MIN50", cartSubtotal)

        // Then
        assertTrue(result.isValid)
        assertNotNull(result.promoCode)
        assertEquals(BigDecimal("10.00"), result.discountAmount)
        assertNull(result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for empty code`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")

        // When
        val result = promoCodeService.validatePromoCode("", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Promo code cannot be empty", result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for code too short`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")

        // When
        val result = promoCodeService.validatePromoCode("AB", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Promo code must be between 3 and 50 characters", result.errorMessage)
    }

    @Test
    fun `validatePromoCode should return invalid result for code with special characters`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")

        // When
        val result = promoCodeService.validatePromoCode("SAVE-10", cartSubtotal)

        // Then
        assertFalse(result.isValid)
        assertNull(result.promoCode)
        assertEquals(BigDecimal.ZERO, result.discountAmount)
        assertEquals("Promo code can only contain letters and numbers", result.errorMessage)
    }

    @Test
    fun `calculateDiscount should calculate percentage discount correctly`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")

        // When
        val discount = promoCodeService.calculateDiscount(validPercentagePromoCode, cartSubtotal)

        // Then
        assertEquals(BigDecimal("10.00"), discount)
    }

    @Test
    fun `calculateDiscount should calculate fixed amount discount correctly`() {
        // Given
        val cartSubtotal = BigDecimal("100.00")

        // When
        val discount = promoCodeService.calculateDiscount(validFixedAmountPromoCode, cartSubtotal)

        // Then
        assertEquals(BigDecimal("5.00"), discount)
    }

    @Test
    fun `calculateDiscount should not exceed cart subtotal for fixed amount`() {
        // Given
        val cartSubtotal = BigDecimal("3.00") // Less than £5 discount
        val largeFixedDiscount = PromoCode(
            id = 6L,
            code = "LARGE",
            discountType = DiscountType.FIXED_AMOUNT,
            discountValue = BigDecimal("10.00"),
            validFrom = LocalDateTime.now().minusDays(1),
            validUntil = LocalDateTime.now().plusDays(30),
            isActive = true
        )

        // When
        val discount = promoCodeService.calculateDiscount(largeFixedDiscount, cartSubtotal)

        // Then
        assertEquals(cartSubtotal, discount) // Should not exceed cart subtotal
    }
}