# Domain Logic – Cloud Ecommerce API

---

# Aggregate Root: Product

## Responsibility
Product is the aggregate root. It:
- Controls lifecycle and visibility.
- Owns Inventory (internal entity).
- Enforces cross-entity business rules.
- Guarantees consistency of the aggregate.

---

## Product Status Lifecycle

### DRAFT
- Initial state.
- Fully editable.
- Not visible to customers.
- No purchase operations allowed.

### ACTIVE
Visible for sale.

Activation Requirements:
1. Price must be greater than 0.
2. Name must not be null or blank.
3. Description must not be null or blank.
4. (Optional policy) Available stock must be greater than 0.

While ACTIVE:
- Reservations are allowed.
- Stock confirmation is allowed.
- Inventory mutations are allowed.

### DEACTIVATED
- Not visible to customers.
- New reservations are not allowed.
- Existing reservations must be zero before deactivation.

Rule:
Product cannot transition to DEACTIVATED if:
- Inventory.reservedQuantity > 0

---

# Entity: Inventory (Internal to Product)

## Responsibility
Inventory manages physical stock and reservations.
It does NOT control product lifecycle.

---

## Invariants (Must Always Be True)

1. quantity >= 0
2. reservedQuantity >= 0
3. reservedQuantity <= quantity

Violation of any invariant indicates data corruption.

---

## Stock Model

available = quantity - reservedQuantity

---

## Commands

### restock(amount)
- amount > 0
- Increases quantity
- Must preserve invariants

### reserveStock(amount)
- amount > 0
- amount <= available
- Increases reservedQuantity
- Does NOT reduce quantity

### confirmReservation(amount)
- amount > 0
- amount <= reservedQuantity
- Decreases quantity
- Decreases reservedQuantity

### releaseReservation(amount)
- amount > 0
- amount <= reservedQuantity
- Decreases reservedQuantity
- Does NOT change quantity

### clearAllReservations()
- Sets reservedQuantity = 0
- Used for expiration handling
- Must preserve invariants

---

## Aggregate Boundary Rules

- Inventory commands are package-private.
- Only Product can invoke Inventory mutation methods.
- External layers must never modify Inventory directly.
- All business validations involving status must live in Product.

---

## Inventory Loading & Rehydration Safety

### Problem
When a Product is rehydrated from the database (e.g., via JPA), the `Inventory` object may be `null` if:
- The JPA query did not use `JOIN FETCH` for inventory.
- The mapper did not create an Inventory object.
- The database record has no associated inventory.

This can cause `NullPointerException` when calling methods like `reserveStock()` or `getStock()`.

### Solution
The Product domain enforces that `inventory` is **never null**:

``` java
// Rehydration constructor with inventory parameter
public Product(UUID id, String sku, String name, String description, BigDecimal price,
               UUID categoryId, String imageURL, ProductStatus status,
               LocalDateTime createdAt, LocalDateTime updatedAt,
               Inventory inventory) {
    // ...
    this.inventory = inventory != null ? inventory : new Inventory(0, 0);

```

## Safety Methods
- All inventory mutations trigger an internal verification before executing business logic to guarantee stability.

```java
private void ensureInventoryLoaded() {
    if (this.inventory == null) {
        this.inventory = new Inventory(0, 0);
    }
}
``` 
## Architectural Responsibilities

- Domain (Product): Guarantees invariant self-containment; inventory is never null inside the aggregate boundary.
- Infrastructure (ProductMapper): Converts JPA relationships into domain states, supplying either the mapped object or null safely.
- Data Access (Repository): Eagerly fetches relationships whenever business operations require high transactional consistency.

## State Rules & Preconditions

- restock(amount): Requires amount > 0. Postcondition: quantity += amount.
- reserveStock(amount): Requires amount > 0 and amount <= available. Postcondition: reservedQuantity += amount.
- confirmReservation(amount): Requires amount > 0 and amount <= reserved. Postcondition: quantity -= amount and reservedQuantity -= amount.
- releaseReservation(amount): Requires amount > 0 and amount <= reserved. Postcondition: reservedQuantity -= amount.
- clearAllReservations(): No preconditions. Postcondition: reservedQuantity = 0.

## Concurrency Consideration (Infrastructure Layer)

- Optimistic locking via @Version (future implementation).
- Aggregate must be modified within a transactional boundary.
- Domain protects invariants.
- Database protects concurrent modifications.
