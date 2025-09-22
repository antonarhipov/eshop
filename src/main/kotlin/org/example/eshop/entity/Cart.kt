package org.example.eshop.entity

import jakarta.persistence.*
import java.math.BigDecimal
import java.time.LocalDateTime

@Entity
@Table(name = "carts")
data class Cart(
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    val id: Long = 0,

    @Column(nullable = false, precision = 10, scale = 2)
    var subtotal: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var vatAmount: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var shippingCost: BigDecimal = BigDecimal.ZERO,

    @Column(nullable = false, precision = 10, scale = 2)
    var total: BigDecimal = BigDecimal.ZERO,

    @Column(name = "promo_code_id")
    var promoCodeId: Long? = null,

    @Column(name = "promo_code_code", length = 50)
    var promoCodeCode: String? = null,

    @Column(name = "discount_amount", nullable = false, precision = 10, scale = 2)
    var discountAmount: BigDecimal = BigDecimal.ZERO,

    @Column(name = "created_at", nullable = false)
    val createdAt: LocalDateTime = LocalDateTime.now(),

    @Column(name = "updated_at", nullable = false)
    var updatedAt: LocalDateTime = LocalDateTime.now(),

    @OneToMany(mappedBy = "cart", cascade = [CascadeType.ALL], fetch = FetchType.LAZY, orphanRemoval = true)
    val items: MutableList<CartItem> = mutableListOf()
) {
    @PreUpdate
    fun preUpdate() {
        updatedAt = LocalDateTime.now()
    }

    fun addItem(cartItem: CartItem) {
        items.add(cartItem)
    }

    fun removeItem(cartItem: CartItem) {
        items.remove(cartItem)
    }

    fun clearItems() {
        items.clear()
    }

    fun calculateTotals() {
        subtotal = items.sumOf { it.lineTotal }
        // VAT and shipping calculations will be handled by service layer
        // Apply discount to total calculation
        total = subtotal - discountAmount + shippingCost
    }

    /**
     * Applies a promo code to the cart
     */
    fun applyPromoCode(promoCodeId: Long, promoCodeCode: String, discountAmount: BigDecimal) {
        this.promoCodeId = promoCodeId
        this.promoCodeCode = promoCodeCode
        this.discountAmount = discountAmount
        this.updatedAt = LocalDateTime.now()
    }

    /**
     * Removes the applied promo code from the cart
     */
    fun removePromoCode() {
        this.promoCodeId = null
        this.promoCodeCode = null
        this.discountAmount = BigDecimal.ZERO
        this.updatedAt = LocalDateTime.now()
    }

    /**
     * Checks if a promo code is currently applied to the cart
     */
    fun hasPromoCode(): Boolean {
        return promoCodeId != null && promoCodeCode != null
    }
}