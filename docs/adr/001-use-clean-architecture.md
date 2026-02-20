# ADR 001: Adopción de Clean Architecture con Java 21 y Spring Boot 3

## Estado
Aceptado  
Fecha: 2026-02-20

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

Además, el proyecto debe poder evolucionar hacia microservicios en el Mes 4 sin reescrituras grandes, y debe integrarse fácilmente con AWS en el Mes 3.

---

## Decisión
Adoptar **Clean Architecture** con **Java 21 (LTS)** y **Spring Boot 3.4** como base del proyecto.

### Justificaciones técnicas

1. **Clean Architecture**
    - Desacopla dominio → aplicación → infraestructura.
    - Permite cambiar Postgres por RDS, Kafka por SQS o S3 por almacenamiento local sin tocar casos de uso.
    - Facilita pruebas unitarias sin frameworks.
    - Alineado a prácticas modernas de backend en empresas cloud-native.

2. **Java 21 (LTS)**
    - Versión estable y soportada a largo plazo.
    - Compatible con Virtual Threads para futuras optimizaciones.
    - API moderna (pattern matching, records).
    - **Compatibilidad madura con Mockito y JUnit 5**  
      (Java 22+ presenta fallos por cambios internos del bytecode, por lo que Java 21 es la opción segura).

3. **Spring Boot 3.4**
    - Integración inmediata con JPA, Redis, S3, Kafka/SQS.
    - Actuator para métricas y health-checks (necesario para AWS).
    - Simplifica despliegue en Docker y EC2.

---

## Consecuencias

### Positivas
- Código testeable y desacoplado.
- Migraciones tecnológicas simples (infra “plug-and-play”).
- Preparado para microservicios.
- Preparado para AWS (Mes 3) sin reescrituras.
- Facilidad para CI/CD.

### Negativas
- Más archivos y clases iniciales.
- Mayor disciplina en separaciones de capas.
- Curva de aprendizaje mayor para juniors.

---

## Alternativas Rechazadas

### ❌ Opción A: Arquitectura en N Capas tradicional
**Motivo de rechazo:** Alto acoplamiento, difícil de testear, compleja para migrar a AWS o microservicios.

### ❌ Opción B: Microservicios desde el día 1
**Motivo de rechazo:** Aumenta complejidad, tiempo y costos sin beneficios en la etapa inicial. El monolito modular es la opción óptima.

### ❌ Opción C: Arquitectura Hexagonal “pura”
**Motivo de rechazo:**  
Aunque similar a Clean Architecture, su implementación estricta requiere mayor complejidad y estructura desde el día 1. CA ofrece un modelo más flexible y accesible sin perder beneficios.

---

## Decisión Final
El backend se construirá siguiendo **Clean Architecture**, utilizando **Java 21** y **Spring Boot 3.4**.  
Esta combinación garantiza un diseño desacoplado, altamente testeable y preparado para integrarse con servicios cloud (AWS) cuando el proyecto lo requiera, sin necesidad de reescribir la lógica de negocio.

Además, la arquitectura permitirá agregar un **microservicio reactivo** para notificaciones.  
Un microservicio reactivo es un servicio creado con **Spring WebFlux**, diseñado para manejar eventos o mensajes (por ejemplo, una compra creada) de manera **asíncrona, no bloqueante y altamente eficiente**.  
Este servicio opera independiente del monolito principal, escucha un mensaje en Kafka o SQS y envía la notificación correspondiente, sin bloquear ni cargar la API principal.

Esta decisión asegura que el sistema pueda crecer en complejidad y escala de forma natural, sin rupturas, sin reescrituras importantes y sin comprometer el rendimiento.
