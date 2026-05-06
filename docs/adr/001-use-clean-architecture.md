# ADR 001: Adopción de Clean Architecture con Java 21 y Spring Boot 3

## Estado
Aceptado  
Fecha: 2026-02-20
**Última revisión: 2026-05-01**

---

## Contexto
Necesitamos construir un sistema de e-commerce que sea **mantenible**, **escalable** y **cloud-ready**.  
El backend debe permitir reemplazar tecnologías (base de datos, almacenamiento, mensajería, proveedores cloud) sin afectar la lógica de negocio.

Para lograrlo, necesitamos una arquitectura que desacople completamente:

- Regla de negocio (dominio)
- Casos de uso
- Infraestructura (Postgres, Redis, S3, Kafka/SQS)

Lo anterior se traduce en **Capas Estrictas:** 
- `domain`: Reglas de negocio puras (sin frameworks).
- `application`: Casos de uso.
- `infrastructure`: Adaptadores externos (Postgres, AWS, REST).

---

## Decisión
Adoptar **Clean Architecture** como base organizacional, complementada con:

- **Domain-Driven Design (DDD)** para modelar el negocio
- **Arquitectura Hexagonal (Ports & Adapters)** para conectar el dominio con la infraestructura

### ¿Por qué esta combinación?

| **Enfoque** | **Rol en el proyecto** | **Implementación en código** |
|-------------|----------------------|------------------------------|
| **DDD** | Define **QUÉ** programar (reglas de negocio) | `Product`, `Order`, `User` como Aggregate Roots; `Inventory` como entidad interna; invariantes en métodos como `activate()` |
| **Arquitectura Hexagonal** | Define **CÓMO** conectar el dominio con el exterior | `ProductRepository` (puerto en domain) y `ProductRepositoryAdapter` (adaptador en infrastructure) |
| **Clean Architecture** | Define **DÓNDE** poner cada archivo | Capas `domain/`, `application/`, `infrastructure/` |

### Justificaciones técnicas

1. **Clean Architecture**
   - Desacopla dominio → aplicación → infraestructura.
   - Permite cambiar Postgres por RDS, Kafka por SQS o S3 por almacenamiento local sin tocar casos de uso.
   - Facilita pruebas unitarias sin frameworks.

2. **DDD (Domain-Driven Design)**
   - Modela el negocio mediante Aggregates (`Product`, `Order`), Value Objects y Entidades internas (`Inventory`).
   - Protege invariantes (ej: `quantity >= reservedQuantity`).
   - Define transiciones de estado (ej: `DRAFT → ACTIVE → DEACTIVATED`).

3. **Arquitectura Hexagonal**
   - Define **puertos** (interfaces en domain) como `ProductRepository`.
   - Implementa **adaptadores** (clases en infrastructure) como `ProductRepositoryAdapter`.
   - Permite cambiar de JPA a MongoDB sin modificar el dominio.

4. **Java 21 (LTS)**
   - Versión estable y soportada a largo plazo.
   - Compatible con Virtual Threads para futuras optimizaciones.
   - API moderna (pattern matching, records).
   - **Compatibilidad madura con Mockito y JUnit 5**  
     (Java 22+ presenta fallos por cambios internos del bytecode, por lo que Java 21 es la opción segura).

5. **Spring Boot 3.4**
   - Integración inmediata con JPA, Redis, S3, Kafka/SQS.
   - Actuator para métricas y health-checks.

### Patrones de Implementación Específicos

| **Patrón** | **Ubicación** | **Propósito** |
|-----------|--------------|---------------|
| **Aggregate Root** | `domain/product/Product.java` | Define límites de consistencia transaccional |
| **Repository Interface (Puerto)** | `domain/product/ProductRepository.java` | Contrato de persistencia (DDD + Hexagonal) |
| **JPA Entity** | `infrastructure/persistence/jpa/entity/` | Mapeo a tablas de BD |
| **Mapper** | `infrastructure/persistence/jpa/mapper/` | Convierte Domain ↔ JPA Entity |
| **Repository Adapter** | `infrastructure/persistence/jpa/adapter/` | Implementa el puerto usando JPA |
| **Spring Data JPA Repository** | `infrastructure/persistence/jpa/repository/` | CRUD automático |

**Flujo de persistencia:**

Domain → Adapter → Mapper → JPA Entity → JPA Repository → BD

### Soft Delete (Borrado Lógico)
- Anotación `@SoftDelete(columnName = "active", strategy = SoftDeleteType.ACTIVE)`
- Campos `active` y `deactivatedAt` para auditoría

### Idempotencia en Operaciones Críticas
- **Idempotency Key**: Cliente genera UUID por request
- **Validación**: Repositorio verifica existencia antes de crear
- **Unique Constraint**: `idempotent_key` es UNIQUE en BD

---

## Consecuencias

### Positivas
- Código testeable y desacoplado.
- Migraciones tecnológicas simples.
- Preparado para microservicios y AWS
- Separación clara de responsabilidades (DDD + Hexagonal + Clean).

### Negativas
- Más archivos y clases (Entities, Mappers, Adapters, Repositories).
- Curva de aprendizaje mayor.

---

## Alternativas Rechazadas

### ❌ DDD puro
**Motivo:** No resuelve la organización técnica ni la conexión con infraestructura.

### ❌ Hexagonal pura
**Motivo:** No proporciona guías para modelar el negocio complejo.

### ❌ Clean Architecture pura
**Motivo:** No define cómo manejar relaciones complejas entre entidades (Aggregates).

### ❌ Microservicios desde el día 1
**Motivo:** Complejidad innecesaria. Monolito modular es óptimo.

---

## Aclaración Final

**Este proyecto no es solo Clean Architecture. Es un híbrido profesional que combina:**

- **DDD** para modelar el negocio (el "qué")
- **Hexagonal** para conectar con el exterior (el "cómo")
- **Clean Architecture** para organizar el código (el "dónde")

Esta combinación es el **estándar de la industria** para sistemas complejos y mantenibles.

---

## Decisión Final
El backend se construirá bajo esta hibridación, utilizando **Java 21** y **Spring Boot 3.4**.

---

## Aclaración: Hibridación con DDD y Arquitectura Hexagonal

El proyecto no utiliza Clean Architecture de forma aislada, sino como parte de una **hibridación profesional** que combina tres enfoques complementarios:

| **Enfoque** | **Rol en el proyecto** | **Implementación en código** |
|-------------|----------------------|------------------------------|
| **Domain-Driven Design (DDD)** | Define **QUÉ** programar (las reglas de negocio) | `Product`, `Order`, `User` como Aggregate Roots; `Inventory` como entidad interna; invariantes en métodos como `activate()` |
| **Arquitectura Hexagonal (Ports & Adapters)** | Define **CÓMO** conectar el dominio con el exterior | `ProductRepository` (puerto en domain) y `ProductRepositoryAdapter` (adaptador en infrastructure) |
| **Clean Architecture** | Define **DÓNDE** poner cada archivo | Capas `domain/`, `application/`, `infrastructure/` |

### ¿Por qué esta combinación?

- **DDD solo** → Tendrías el negocio bien modelado, pero el código sería un desorden técnico.
- **Hexagonal sola** → Tendrías buena conexión externa, pero el negocio sería anémico (solo getters/setters).
- **Clean Architecture sola** → Tendrías buena organización de carpetas, pero sin guía para modelar relaciones complejas (como Product → Inventory).

**La combinación de las tres es el estándar de la industria para sistemas complejos y mantenibles.**