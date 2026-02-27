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

## Concurrency Consideration (Infrastructure Layer)

- Optimistic locking via @Version (future implementation).
- Aggregate must be modified within a transactional boundary.
- Domain protects invariants.
- Database protects concurrent modifications.
