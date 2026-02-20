# Software Requirements Specification (SRS) - cloud-ecommerce-api
**Versión:** 0.2
**Autor:** Marco Orlando Bustos Blas
**Fecha:** 19-02-26
---
## 1.Introducción:
Este documento define los requisitos funcionales y no funcionales del sistema `cloud-ecommerce-api`, un backend **Cloud-Native** construido con **Java 21**, **Spring Boot 3.4** y **Gradle**, siguiendo **Arquitectura Limpia**. El sistema soporta catálogo, usuarios, carritos y órdenes, integrándose con **AWS** (S3/RDS) y mensajería asíncrona.


## 2. Objetivos del Sistema
- Proveer una **API REST** robusta y documentada (Swagger/OpenAPI).
- Persistir datos transaccionales en **PostgreSQL (RDS en prod)**.
- Gestionar imágenes de productos en **AWS S3** (no en BD).
- Acelerar el carrito de compras con **Redis**.
- Asegurar endpoints con **Spring Security + JWT**.
- Desacoplar procesos con **mensajería asíncrona** (Kafka o AWS SQS) para notificaciones.
- Entregar un backend **portable con Docker**, listo para CI/CD y deploy en **AWS EC2**.

## 3. Requisitos Funcionales (RF)

### RF1: Gestión de Catálogo
- **RF1.1** CRUD de productos.
- **RF1.2** Cada producto: ID, nombre, descripción, SKU, precio, stock, categoría, URL de imagen.
- **RF1.3** Filtros por categoría, rango de precio, texto.
- **RF1.4** Subida de imágenes: presigned URL o carga directa al backend → **S3**.

### RF2: Gestión de Stock
- **RF2.1** Validación de stock al confirmar compra.
- **RF2.2** Actualización atómica del inventario en transacción.
- **RF2.3** Manejo de concurrencia (optimistic locking o equivalente).

### RF3: Autenticación y Autorización
- **RF3.1** Registro y login mediante email/password (BCrypt).
- **RF3.2** Emisión de **JWT** con expiración y roles (`ADMIN`, `CUSTOMER`).
- **RF3.3** Rutas protegidas por rol: admin para catálogo, customer para compra.

### RF4: Carrito de Compras (Redis)
- **RF4.1** Crear/leer/actualizar/eliminar ítems de carrito por usuario.
- **RF4.2** TTL configurable.
- **RF4.3** Limpieza del carrito posterior a la orden confirmada.

### RF5: Órdenes
- **RF5.1** Crear orden desde carrito.
- **RF5.2** Validar stock, calcular totales/impuestos (si aplica), persistir orden.
- **RF5.3** Transacción: restar stock + crear orden + limpiar carrito.
- **RF5.4** Emitir evento `order.created` a **Kafka/SQS**.

### RF6: Notificaciones (Microservicio Reactivo)
- **RF6.1** Microservicio (WebFlux) suscrito a `order.created`.
- **RF6.2** Enviar correo de confirmación (proveedor a definir: SES/Mailgun).
- **RF6.3** Retries y DLQ (cola de mensajes fallidos) si se usa SQS/SNS.

### RF7: Observabilidad y Salud
- **RF7.1** Actuator (`/actuator/health`, `/metrics`, `/info`).
- **RF7.2** Logs estructurados.
- **RF7.3** Dashboard básico (CloudWatch en prod).

## 4. Requisitos No Funcionales (RNF)

### RNF1: Arquitectura y Diseño
- **RNF1.1** **Arquitectura Limpia**: `domain`, `application`, `infrastructure`.
- **RNF1.2** Inyección de dependencias e IoC con Spring.
- **RNF1.3** Configuración por perfiles (local/dev/prod).

### RNF2: Rendimiento y Escalabilidad
- **RNF2.1** JPA con **HikariCP** (pool).
- **RNF2.2** Redis para lecturas rápidas de carrito.
- **RNF2.3** Stateless JWT para escalar horizontalmente (ALB listo).

### RNF3: Seguridad
- **RNF3.1** Hash de contraseñas con BCrypt.
- **RNF3.2** Principio de privilegio mínimo (IAM en AWS).
- **RNF3.3** Variables sensibles fuera del repo (env/Secrets Manager en prod).
- **RNF3.4** CORS configurado para frontends conocidos.

### RNF4: Calidad de Código
- **RNF4.1** Tests unitarios (servicios y dominio) y de integración (JPA/Controllers).
- **RNF4.2** CI (GitHub Actions) con build y tests obligatorios.
- **RNF4.3** Cobertura objetivo: 60–70% (mínimo en dominio y casos críticos).

### RNF5: Operación y Deploy
- **RNF5.1** Docker y Docker Compose para entorno local.
- **RNF5.2** Imagen de producción multi‑stage.
- **RNF5.3** Deploy en **AWS EC2** + DB en **RDS** + storage **S3**.

## 5. Stakeholders
- **Desarrollador (Marco)**: diseño/implementación.
- **Reclutadores Técnicos**: auditoría del repo/arquitectura.
- **Usuarios finales**: clientes de la tienda (vía frontend externo).

## 6. Supuestos y Dependencias
- El frontend es externo (fuera del alcance).
- Dependencias: PostgreSQL, Redis, Kafka/SQS, AWS S3/RDS/IAM.
- El microservicio de notificaciones no bloquea el flujo de compra.

## 7. Criterios de Aceptación (muestras)
- **CA-01** Login retorna JWT válido y accesos según rol.
- **CA-02** CRUD de productos visible en Swagger, validaciones activas.
- **CA-03** Carrito responde en < 50 ms (local) para operaciones comunes.
- **CA-04** Orden confirma, descuenta stock y envía evento asíncrono.
- **CA-05** S3 almacena imágenes y la API expone URL accesible.
- **CA-06** `/actuator/health` en **UP** en entorno local y prod.

## 8. Roadmap de Entregas (resumen)
- Mes 1: CRUD + Docker + Postgres
- Mes 2: Seguridad + Tests
- Mes 3: S3 + Redis + Secrets
- Mes 4: Docker prod + Deploy AWS + Microservicio Notificaciones
- Mes 5: Pulido + Observabilidad + CLF‑C02

---

