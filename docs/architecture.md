# 🏛️ Arquitectura del Proyecto – cloud-ecommerce-api
**Versión:** v0.1  
**Autor:** Marco Orlando Bustos Blas  
**Fecha:** 2026-02-20

Este documento presenta la arquitectura general del backend **cloud-ecommerce-api**, construido bajo **Clean Architecture**, con **Java 21**, **Spring Boot 3.4**, **Gradle**, y una estructura preparada para entornos cloud (AWS).

---

# 1. Visión General

El objetivo es diseñar un backend **modular, mantenible, escalable y preparado para la nube (cloud-ready)**.


El diseño considera:

- Separación estricta lógica vs infraestructura
- Persistencia transaccional con PostgreSQL
- Cache temporal con Redis
- Almacenamiento de imágenes en S3
- Mensajería asíncrona con Kafka o AWS SQS
- Seguridad con JWT
- Observabilidad con **Actuator**, preparado para integrarse con **Prometheus + Grafana** más adelante

La arquitectura sigue principios de separación estricta entre **Domain → Application → Infrastructure**.

Todo esto facilita auditorías técnicas (empresas cloud-native, startups y equipos con enfoque profesional).

---

# 2. Capas de la Arquitectura (Clean Architecture)

## **1) Domain Layer**
Contiene las piezas centrales del negocio:
- Entidades (Product, Category)
- Reglas de negocio puras
- Interfaces de repositorios

**Características:**
- No depende de Spring ni frameworks.
- 100% Java puro.
- Es estable en el tiempo.

> *Dominio = “qué hace el negocio”, sin depender de frameworks, BD o AWS.*

---

## **2) Application Layer**
Orquesta la lógica:
- Casos de uso (CreateProduct, UpdateStock, ListProducts)
- Coordina entidades y repositorios
- Contiene validaciones de flujo

**Características:**
- Depende SOLO del dominio.
- No depende de infraestructura.
- Fácil de testear (mock de interfaces).

---

## **3) Infrastructure Layer**
La infraestructura se organiza en subpaquetes según su responsabilidad:
```
infrastructure/
├── persistence/
│ └── jpa/
│ ├── entity/ # JPA Entities (@Entity, @Table)
│ ├── repository/ # Spring Data JPA Repositories
│ ├── mapper/ # Conversores Domain ↔ JPA Entity
│ └── adapter/ # Implementaciones de repositorios del dominio
├── web/
│ └── controller/ # REST Controllers (futuro)
├── config/ # Beans, JWT, seguridad (futuro)
├── cache/ # Redis (futuro)
├── aws/ # S3 client (futuro)
├── messaging/ # Kafka/SQS producer (futuro)
└── db/ # Migraciones SQL (futuro)
```

**Patrón implementado:** Repository Pattern + Adapter Pattern + Mapper Pattern

---

## 3. Flujo de una Petición HTTP (Completo)

1. **Cliente** → Controller (infrastructure/web)
2. **Controller** → Application Service (application)
3. **Application Service** → Repository Interface (domain)
4. **Repository Interface** ← implementada por → Repository Adapter (infrastructure/persistence/jpa/adapter)
5. **Repository Adapter** → Mapper (convierte Domain → JPA Entity)
6. **Mapper** → JPA Repository (infrastructure/persistence/jpa/repository)
7. **JPA Repository** → Hibernate → PostgreSQL

**Flujo de retorno (lectura):**

PostgreSQL → Hibernate → JPA Repository → Mapper (convierte JPA Entity → Domain) → Repository Adapter → Application Service → Controller → Cliente

---

# 4. Flujo de Imágenes (AWS S3)
Frontend → Backend (Upload)
→ AWS S3 (store object)
← Backend retorna URL pública o presigned URL

Ventajas:
- No almacena imágenes en el servidor
- Backend liviano
- S3 es escalable, barato y confiable

---

# 5. Flujo de Carrito (Redis)

Cliente → Backend
→ Redis (SET / GET / EXPIRE)

Redis se usa para:
- Lecturas rápidas (O(1))
- Carrito temporal
- Reducción de carga en la DB

---

# 6. Flujo de Órdenes + Mensajería (Kafka / SQS)

Checkout:
Cliente → Caso de Uso → Persistencia
→ Publicar evento order.created
→ Kafka topic / SQS queue

Esto permite:
- Enviar notificaciones sin bloquear la compra.
- Escalar servicios independientemente.

---

# 7. Arquitectura Local vs Cloud

## 🏠 **Local**
- Docker Compose:
    - Postgres
    - Redis (futuro)
- API desde IntelliJ
- S3 fake o carpeta local (opcional)
- Kafka local (futuro)

## ☁️ **Cloud-ready (AWS)**
- EC2 → API backend
- RDS → PostgreSQL administrado
- S3 → imágenes
- ElastiCache → Redis
- MSK o SQS/SNS → mensajería
- IAM → políticas de acceso
- CloudWatch → logs y métricas

---

# 8. Estrategias de Optimización (Performance)

Este proyecto aplicará optimizaciones en niveles clave del backend:

## 8.1 Optimización en Base de Datos (O(log n))
- Creación de índices en:
    - `sku` (unique index)
    - `category_id`
    - `price`
    - `name`
- Índices compuestos:
    - `(category_id, price)`
- Queries paginadas (evitar select * sin paginación)
- Uso de `JOIN FETCH` cuando corresponda

> Beneficio: Reducción de búsquedas de O(n) → O(log n)

---

## 8.2 Optimización en Redis (O(1))
Redis manejará:
- Carrito por usuario
- Consultas rápidas para catálogos muy leídos (futuro)
- TTL para no acumular basura

> Beneficio: Respuestas constantes incluso con miles de usuarios.

---

## 8.3 Optimización de Servicios
- Uso de DTOs para evitar exponer entidades
- Uso de streams con moderación
- Evitar cargas EAGER innecesarias
- Pool de conexiones HikariCP optimizado

---

## 8.4 Optimización en Cloud
- Uso de S3 para imágenes (no en DB)
- Uso de instancias EC2 ligeras (GraalVM eventualmente)
- Separación API / Notificaciones (microservicio reactivo)

---

# 9. Posible Integración Futura: GraphQL (Opcional)

GraphQL **no forma parte del alcance inicial**, pero puede agregarse si el tiempo lo permite.

Ventajas:
- El frontend decide qué campos obtener
- Menos endpoints
- Una sola consulta puede obtener múltiples recursos

Ejemplo:


query {
products(categoryId: "abc") {
id
name
price
}
}

Se explorará solo si:
- El backend base está estable
- Hay tiempo para aplicar teoría + práctica

---

# 10. Próximas Versiones del Documento

Este documento se actualizará con nuevas versiones conforme se agreguen módulos:

- **v0.2:** Modelo extendido (Order, OrderItem)
- **v0.3:** Integración S3 / Redis
- **v0.4:** Mensajería (Kafka/SQS)
- **v1.0:** Arquitectura final

> El avance se registra dentro del mismo archivo, no como nuevos archivos.

---

# 11. Beneficios Finales

- Alto desacoplamiento
- Testeable en todas las capas
- Cloud-ready
- Fácil despliegue
- Preparado para auditorías de empresas cloud-native

---

## 12. Patrones de Diseño Implementados

| **Patrón** | **Dónde se aplica** | **Propósito** |
|-----------|---------------------|---------------|
| **Aggregate Root** | Product, Order, User, Cart | Define límites de consistencia transaccional |
| **Repository** | ProductRepository, UserRepository | Abstrae la capa de persistencia |
| **Adapter** | ProductRepositoryAdapter | Adapta el puerto del dominio a la implementación JPA |
| **Mapper** | ProductMapper, UserMapper | Convierte entre Domain y JPA Entity |
| **Value Object** | Inventory, CartItem (como entidades internas) | Encapsula valores sin identidad propia |
| **Soft Delete** | @SoftDelete en Product, User | Borrado lógico para conservar histórico |
| **Idempotency** | idempotentKey en Order | Previene duplicados en operaciones críticas |
| **Optimistic Locking** | @Version en Inventory, Order | Control de concurrencia sin bloqueos pesados |
