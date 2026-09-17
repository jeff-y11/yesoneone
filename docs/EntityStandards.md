# Entity Standards and Best Practices

All JPA entities in the project must strictly adhere to the standards outlined below. These guidelines ensure consistency across the domain model, prevent common Hibernate/JPA pitfalls, and enable automatic, uniform auditing.

---

## Core Standards

### 1. Primary Key Naming Convention
* **Standard:** Primary keys must be named `<EntityName>Id` (camelCase), where `<EntityName>` is the singular name of the entity. 
* **Rule:** Do **NOT** use generic names like `id`.
* **Example:**
  ```java
  // Correct
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID userId;

  // Incorrect
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID id;
  ```

### 2. Mandatory Auditing
* **Standard:** All entities should extend `AuditableEntity` unless there is a very explicit, documented reason not to.
* **Rule:** If an entity does not extend `AuditableEntity`, you **must** write a block comment at the top of the entity class explaining why.
* **Example:**
  ```java
  /**
   * This entity is a pure join-table entity mapped as a class for auditing-free logging purposes,
   * and does not require tracking creation or modification audits.
   */
  @Entity
  public class LegacyLog { ... }
  ```

### 3. Lombok Annotations
* **Standard:** Use `@Getter`, `@Setter`, and `@NoArgsConstructor` at the class level.
* **Rule:** **NEVER** use `@Data` on JPA entities.
* **Reason:** `@Data` generates a custom `equals()` and `hashCode()` that evaluates all fields. This overrides `AuditableEntity`'s built-in ID-based `equals()` and `hashCode()`, causing issues with Hibernate proxies, lazy loading, and collections (such as `Set`).
* **Example:**
  ```java
  // Correct
  @Entity
  @Getter
  @Setter
  @NoArgsConstructor
  public class Organization extends AuditableEntity { ... }

  // Incorrect
  @Entity
  @Data
  public class Organization extends AuditableEntity { ... }
  ```

### 4. Surrogate vs. Natural Keys
* **Standard:** Always use an auto-generated surrogate entity ID (preferably UUID).
* **Rule:** Do **NOT** use natural IDs as database primary keys, especially multi-field composite primary keys. If a unique natural key exists (e.g., email, code), map it with a `@Column(unique = true)` constraint, but keep the surrogate UUID as the primary key.
* **Example:**
  ```java
  // Correct
  @Id
  @GeneratedValue(strategy = GenerationType.UUID)
  private UUID organizationId;

  @Column(unique = true, nullable = false)
  private String orgCode;
  ```

---

## Relationship & Performance Guidelines

### 5. Fetch Types
* **Standard:** Always default to lazy loading (`FetchType.LAZY`) for all to-one relationships (`@ManyToOne`, `@OneToOne`).
* **Rule:** Do not leave fetch type default for to-one relationships (as they default to `EAGER` in JPA), which can trigger catastrophic N+1 query loops.
* **Example:**
  ```java
  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "organization_id")
  private Organization organization;
  ```

### 6. Collection Mapping
* **Standard:** Prefer `Set<E>` over `List<E>` for mapping `@OneToMany` or `@ManyToMany` relationships.
* **Reason:** `Set` prevents duplicate elements naturally and avoids Cartesian product issues when fetching multiple collections, and works beautifully with the ID-based `equals()` and `hashCode()` in `AuditableEntity`.

### 7. Cascades and Orphan Removal
* **Standard:** Limit the use of global cascades (`CascadeType.ALL`). Prefer specific cascade types (e.g., `CascadeType.PERSIST`, `CascadeType.MERGE`) and use `orphanRemoval = true` selectively on parent-child aggregate boundaries.
