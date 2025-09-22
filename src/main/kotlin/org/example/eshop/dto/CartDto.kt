package org.example.eshop.dto

import java.math.BigDecimal
import java.time.LocalDateTime

data class CartDto(
    val id: Long,
    val subtotal: BigDecimal,
    val vatAmount: BigDecimal,
    val shippingCost: BigDecimal,
    val discountAmount: BigDecimal,
    val total: BigDecimal,
    val createdAt: LocalDateTime,
    val updatedAt: LocalDateTime,
    val items: List<CartItemDto>,
    val promoCode: PromoCodeDto? = null
)

data class CartItemDto(
    val id: Long,
    val variantId: Long,
    val qty: Int,
    val priceSnapshot: BigDecimal,
    val lineTotal: BigDecimal,
    val variant: VariantSummaryDto? = null
)

data class VariantSummaryDto(
    val id: Long,
    val sku: String,
    val title: String,
    val price: BigDecimal,
    val stockQty: Int,
    val reservedQty: Int,
    val productTitle: String
)

data class AddToCartRequest(
    val variantId: Long,
    val quantity: Int
)

data class UpdateCartItemRequest(
    val variantId: Long,
    val quantity: Int
)

data class PromoCodeDto(
    val code: String,
    val discountAmount: BigDecimal,
    val description: String? = null
)

data class ApplyPromoCodeRequest(
    val code: String
)

data class CartOperationResponse(
    val success: Boolean,
    val message: String? = null,
    val cart: CartDto? = null,
    val errors: List<String> = emptyList()
)