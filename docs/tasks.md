# Development Task List — Tea Shop Prototype

Last updated: 2025-09-22

This task list is derived from the implementation plan in docs/plan.md. Tasks are organized by development phases with checkboxes for tracking completion. Each task includes priority indicators (P0/P1/P2) and requirement mappings (R1-R19).

**Legend:**
- `[ ]` = Not started
- `[x]` = Completed
- **P0** = Must-have for prototype readiness
- **P1** = Important, near-term
- **P2** = Nice-to-have/stretch
- **R#** = Requirement mapping (see docs/requirements.md)

---

## Phase 0 — Project Skeleton & Config [P0]

### Infrastructure Setup
- [x] 1. Configure Spring Boot project with Kotlin support [P0] [R12, R13]
- [x] 2. Add core dependencies (Spring Web, JPA, Security, Thymeleaf) [P0] [R12, R13]
- [x] 3. Set up Flyway for database migrations [P0] [R18]
- [x] 4. Configure PostgreSQL database container in compose.yaml [P0] [R18]
- [x] 5. Configure application logging to file (logs/eshop.log) and console [P0] [R19]
- [x] 6. Create application.yml with dev/prod profiles [P0] [R2, R11]
- [x] 7. Configure basic logging and error handling [P0] [R12]

---

## Phase 1 — Data Model & Repositories [P0]

### Entity Implementation
- [x] 8. Create Product entity (id, slug, title, type, description, status) [P0] [R18]
- [x] 9. Create Variant entity (id, productId, sku, title, price, weight, shippingWeight, stockQty, reservedQty, lotId) [P0] [R18]
- [x] 10. Create Lot entity (id, productId, harvestYear, season, storageType, pressDate) [P0] [R18]
- [x] 11. Create Cart entity (id, items[], totals) [P0] [R18]
- [x] 12. Create CartItem entity (id, variantId, qty, priceSnapshot) [P0] [R18]
- [x] 13. Create Order entity (id, number, email, address, items[], totals, tax, shipping, status, paymentStatus, fulfillmentStatus, trackingUrl) [P0] [R18]
- [x] 14. Create OrderItem entity (id, orderId, variantId, titleSnapshot, qty, priceSnapshot) [P0] [R18]

### Repository & Migration Setup
- [x] 15. Add optimistic locking (@Version) to Variant and Order entities [P0] [R10]
- [x] 16. Create JPA repositories for all entities [P0] [R18]
- [x] 17. Write Flyway migration scripts for database schema [P0] [R18]
- [x] 18. Add referential integrity constraints [P0] [R18]
- [x] 19. Create seed data for testing (products, variants, lots) [P0] [R18]
- [x] 20. Add basic auditing fields (createdAt, updatedAt) where needed [P0] [R18]

---

## Phase 2 — Pricing & Shipping Calculators [P0]

### Business Logic Implementation
- [x] 21. Implement VAT extractor service (extract VAT from VAT-inclusive prices) [P0] [R11]
- [x] 22. Create shipping calculator service with zone × weight brackets [P0] [R11]
- [x] 23. Configure VAT rate in application.yml (shop.vatRate) [P0] [R11]
- [x] 24. Configure shipping zones and weight brackets in application.yml [P0] [R11]
- [x] 25. Write unit tests for VAT calculation [P0] [R11]
- [x] 26. Write unit tests for shipping calculation [P0] [R11]
- [x] 27. Create totals calculator service combining VAT and shipping [P0] [R11]

---

## Phase 3 — Catalog & PDP [P0]

### Public API Endpoints
- [x] 28. Implement GET /api/products with filtering (type, region, harvestYear, price, inStock) [P0] [R1, R12]
- [x] 29. Add URL-driven filter state management [P0] [R1]
- [x] 30. Handle invalid filter parameters gracefully [P0] [R1]
- [x] 31. Implement GET /api/products/{slug} with variant details [P0] [R3, R12]
- [x] 32. Add stock status indicators (In Stock, Low Stock, Out of Stock) [P0] [R1, R3]

### Server-Rendered UI Pages
- [x] 33. Create Catalog page template with Thymeleaf [P0] [R14]
- [x] 34. Implement filter UI with form submission [P0] [R1, R14]
- [x] 35. Add empty state handling for no results [P0] [R1, R14]
- [x] 36. Create Product Detail Page (PDP) template [P0] [R14]
- [x] 37. Implement variant selector with AJAX/HTMX for price/stock updates [P0] [R3, R14]
- [x] 38. Disable add-to-cart button when variant is out of stock [P0] [R3, R14]
- [x] 39. Add basic responsive CSS styling [P0] [R14]

---

## Phase 4 — Cart [P0]

### Cart Management API
- [x] 40. Implement POST /api/cart (create cart) [P0] [R4, R12]
- [x] 41. Implement PATCH /api/cart/{id} (add/update/remove items) [P0] [R4, R12]
- [x] 42. Add cart session management with cookies [P0] [R4]
- [x] 43. Implement quantity validation (prevent negative, cap by stock) [P0] [R4]
- [x] 44. Add real-time totals recalculation [P0] [R4]
- [x] 45. Implement cart persistence in database [P0] [R4]

### Cart UI
- [x] 46. Create Cart page template [P0] [R14]
- [x] 47. Add quantity adjustment controls [P0] [R4, R14]
- [x] 48. Implement item removal functionality [P0] [R4, R14]
- [x] 49. Display cart totals with VAT breakdown [P0] [R4, R11, R14]
- [x] 50. Add error handling for insufficient stock [P0] [R4, R14]

---

## Phase 5 — Checkout & Order Creation [P0]

### Checkout API
- [x] 51. Implement POST /api/checkout/{cartId}/submit [P0] [R5, R12]
- [x] 52. Add email and address validation [P0] [R5]
- [x] 53. Implement order number generation [P0] [R5]
- [x] 54. Add inventory reservation logic (reservedQty += qty) [P0] [R5, R10]
- [x] 55. Create order with paymentStatus=PENDING [P0] [R5]
- [x] 56. Implement GET /api/orders/{orderNumber} for public lookup [P0] [R7, R12]

### Event Logging
- [x] 57. Define "Order Received" event log format [P0] [R6]
- [x] 58. Implement notification service to write order events to application log [P0] [R6]
- [x] 59. Write confirmation event to log after order creation [P0] [R6]
- [x] 60. Include order number, items, totals, VAT, shipping in the logged event [P0] [R6]

### Checkout UI
- [x] 61. Create Checkout page template with mock payment form [P0] [R14]
- [x] 62. Add form validation for required fields [P0] [R5, R14]
- [x] 63. Create Order Confirmation page [P0] [R14]
- [x] 64. Display order summary and confirmation details [P0] [R14]

---

## Phase 6 — Admin APIs (Catalog + Orders) [P0]

### Admin Authentication & Security
- [x] 65. Configure Spring Security with form login [P0] [R13]
- [x] 66. Create ADMIN role and user management [P0] [R13]
- [x] 67. Secure all /api/admin/* endpoints with ROLE_ADMIN [P0] [R13]
- [x] 68. Enable CSRF protection for admin operations [P0] [R13]

### Admin Catalog Management
- [x] 69. Implement POST /api/admin/products [P0] [R8]
- [x] 70. Implement POST /api/admin/variants [P0] [R8]
- [x] 71. Implement POST /api/admin/lots [P0] [R8]
- [x] 72. Add PATCH/DELETE endpoints for catalog entities [P0] [R8]
- [x] 73. Enforce referential integrity in admin operations [P0] [R8]
- [x] 74. Add validation for admin API requests [P0] [R8]

### Admin Order Management
- [x] 75. Implement GET /api/admin/orders (list all orders) [P0] [R9]
- [x] 76. Implement PATCH /api/admin/orders/{id}/mark-paid [P0] [R9]
- [x] 77. Add stock adjustment logic for paid orders (stockQty -= qty, reservedQty -= qty) [P0] [R9, R10]
- [x] 78. Write "Payment Received" event to application log when marked as paid [P0] [R9, R6]
- [x] 79. Implement PATCH /api/admin/orders/{id}/ship [P0] [R9]
- [x] 80. Add tracking URL support for shipped orders [P0] [R9]
- [x] 81. Write "Order Shipped" event to application log with tracking info [P0] [R9, R6]
- [x] 82. Implement PATCH /api/admin/orders/{id}/cancel [P0] [R9]
- [x] 83. Add reservation release logic for canceled orders [P0] [R9, R10]
- [x] 84. Add order state validation (prevent invalid transitions) [P0] [R9]

### Admin UI
- [x] 85. Create Admin Login page [P0] [R14]
- [x] 86. Create Admin Dashboard [P0] [R14]
- [x] 87. Create Admin Products management page [P0] [R14]
- [x] 88. Create Admin Orders management page [P0] [R14]
- [x] 89. Add order status update controls [P0] [R9, R14]

---

## Phase 7 — Security & Privacy Hardening [P0/P1]

### Security Implementation
- [ ] 90. Audit and enforce CSRF protection on all state-changing endpoints [P0] [R13]
- [ ] 91. Add audit logging for admin operations (who/when) [P0] [R13]
- [ ] 92. Implement correlation IDs for error tracking [P0] [R12]
- [ ] 93. Add input validation and sanitization [P0] [R15]

### Privacy & Legal Pages
- [ ] 94. Create Privacy Policy static page [P1] [R15]
- [ ] 95. Create Terms of Service static page [P1] [R15]
- [ ] 96. Implement cookie banner [P1] [R15]
- [ ] 97. Add minimal PII storage validation [P1] [R15]

---

## Phase 8 — Search Synonyms [P1]

### Search Enhancement
- [ ] 98. Configure synonyms list in application.yml (shop.search.synonyms) [P1] [R2]
- [ ] 99. Implement query expansion for synonym matching [P1] [R2]
- [ ] 100. Add search functionality to catalog filtering [P1] [R2]
- [ ] 101. Write tests for synonym matching ("puer"/"pu-erh") [P1] [R2]

---

## Phase 9 — Analytics [P1]

### Analytics Implementation
- [ ] 102. Create analytics event publisher abstraction [P1] [R16]
- [ ] 103. Implement server-side event logging [P1] [R16]
- [ ] 104. Add pageview tracking [P1] [R16]
- [ ] 105. Add add-to-cart event tracking [P1] [R16]
- [ ] 106. Add purchase completion event tracking [P1] [R16]
- [ ] 107. Implement opt-out functionality [P1] [R16]
- [ ] 108. Add optional frontend JavaScript hooks [P1] [R16]

---

## Phase 10 — Accessibility & Mobile Polish [P1]

### UI Enhancement
- [ ] 109. Implement responsive layouts for all pages [P1] [R17]
- [ ] 110. Add ARIA labels to form controls [P1] [R17]
- [ ] 111. Ensure keyboard navigation for all interactive elements [P1] [R17]
- [ ] 112. Test and fix mobile usability issues [P1] [R17]
- [ ] 113. Add focus indicators and screen reader support [P1] [R17]
- [ ] 114. Validate accessibility compliance [P1] [R17]

---

## Phase 11 — Robustness & Concurrency [P2]

### Reliability Improvements
- [ ] 115. Add structured logging improvements (enrichment/rotation) [P2] [R19]
- [ ] 116. Implement additional integration tests for concurrent orders [P2] [R10]
- [ ] 117. Add comprehensive error handling and recovery [P2] [R12]
- [ ] 118. Implement health checks and monitoring endpoints [P2]
- [ ] 119. Add performance optimization for high-load scenarios [P2]

---

## Phase 12 — Promo Code Functionality [P0]

- [x] 144. Create PromoCode entity with DiscountType enum [P0] [R4]
- [x] 145. Create PromoCodeRepository with custom queries [P0] [R4]
- [x] 146. Update Cart entity to support promo codes (promoCodeId, promoCodeCode, discountAmount) [P0] [R4]
- [x] 147. Create database migration V3 for promo_codes table [P0] [R18]
- [x] 148. Create database migration V4 for cart table updates [P0] [R18]
- [x] 149. Add seed data for testing promo codes [P0] [R18]
- [x] 150. Create PromoCodeService with validation and discount calculation [P0] [R4]
- [x] 151. Create PromoCodeValidationResult data class [P0] [R4]
- [x] 152. Update CartService to integrate promo code functionality [P0] [R4]
- [x] 153. Update CartService.recalculateCartTotals() to include discount calculation [P0] [R4]
- [x] 154. Add promo code application and removal methods to CartService [P0] [R4]
- [x] 155. Create ApplyPromoCodeRequest and PromoCodeDto classes [P0] [R4, R12]
- [x] 156. Update CartDto to include promo code information [P0] [R4, R12]
- [x] 157. Add POST /api/cart/{cartId}/promo-code endpoint [P0] [R4, R12]
- [x] 158. Add DELETE /api/cart/{cartId}/promo-code endpoint [P0] [R4, R12]
- [x] 159. Update CartOperationResponse to handle promo code errors [P0] [R4, R12]
- [x] 160. Update cart.html template to include promo code input section [P0] [R4, R14]
- [x] 161. Add promo code display in cart summary with discount amount [P0] [R4, R14]
- [x] 162. Implement promo code validation error display [P0] [R4, R14]
- [x] 163. Add remove promo code functionality to UI [P0] [R4, R14]
- [x] 164. Update CartViewController to handle promo code operations [P0] [R4, R14]
- [x] 165. Write unit tests for PromoCodeService validation logic [P0] [R4]
- [x] 166. Write unit tests for discount calculations (percentage and fixed amount) [P0] [R4]
- [x] 167. Write unit tests for cart total recalculation with promo codes [P0] [R4]
- [x] 168. Write integration tests for promo code API endpoints [P0] [R4]
- [x] 169. Write integration tests for promo code edge cases (expired, usage limits) [P0] [R4]
- [x] 170. Write end-to-end tests for promo code UI functionality [P0] [R4]

---

## Testing & Validation Tasks

### Unit Testing
- [x] 120. Write unit tests for all pricing and VAT calculations [P0] [R11]
- [x] 121. Write unit tests for shipping calculator [P0] [R11]
- [ ] 122. Write unit tests for order number generator [P0] [R5]
- [ ] 123. Write unit tests for validators [P0] [R12]

### Integration Testing
- [ ] 124. Write integration tests for cart operations [P0] [R4]
- [ ] 125. Write integration tests for checkout → order creation flow [P0] [R5]
- [ ] 126. Write integration tests for inventory reservation/release [P0] [R10]
- [ ] 127. Write integration tests for admin order state transitions [P0] [R9]
- [ ] 128. Write integration tests for public order lookup (200/404) [P0] [R7]
- [ ] 129. Write integration tests for admin authentication [P0] [R13]

### UI Testing
- [ ] 130. Write smoke tests for page rendering [P1] [R14]
- [ ] 131. Write tests for variant selector behavior [P1] [R3]
- [ ] 132. Write tests for out-of-stock disabled states [P1] [R3]
- [ ] 133. Test end-to-end user flow [P0] [R19]

---

## Documentation & Deployment

### Documentation
- [ ] 134. Create API documentation/README [P0]
- [ ] 135. Document environment setup and configuration [P0]
- [ ] 136. Create deployment guide [P1]
- [ ] 137. Document testing procedures [P1]

### Final Validation
- [ ] 138. Verify all P0 requirements are implemented [P0] [R19]
- [ ] 139. Test complete end-to-end flow: browse → PDP → cart → checkout → confirmation [P0] [R19]
- [ ] 140. Validate admin can mark orders as Paid/Shipped [P0] [R19]
- [ ] 141. Confirm inventory reservation and release works correctly [P0] [R19]
- [ ] 142. Verify basic analytics are captured [P0] [R19]
- [ ] 143. Ensure pages are accessible and mobile-friendly [P1] [R19]

---

**Total Tasks: 170**
- **P0 (Critical):** 145 tasks
- **P1 (Important):** 21 tasks  
- **P2 (Nice-to-have):** 4 tasks

**Progress Tracking:**
- Completed: 107/170 (63%)
- In Progress: 0/170 (0%)
- Not Started: 63/170 (37%)