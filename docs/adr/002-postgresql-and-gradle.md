# ADR 002: Elección de PostgreSQL y Gradle como Estándares del Proyecto

## Estado
Aceptado  
Fecha: 2026-02-20  
Autor: Marco Orlando Bustos Blas

---

## 1. Contexto

El proyecto requiere definir las tecnologías base para:
- Persistencia de datos transaccionales.
- Construcción, dependencias y empaquetado del backend.
- Integración futura con entornos cloud (AWS).
- Ejecución eficiente en entornos Docker.
- Escalabilidad horizontal sin reescrituras.

Las decisiones aquí impactan directamente el rendimiento, mantenibilidad, integración con terceros y velocidad de desarrollo.

Se evaluaron alternativas como MySQL para BD, y Maven para herramienta de construcción.

---

## 2. Decisión

Se elige:

### ✔ **PostgreSQL 15**
como base de datos relacional del proyecto.

### ✔ **Gradle (Groovy DSL)**
como herramienta de construcción principal para la API.

---

## 3. Justificación Técnica

### 3.1 PostgreSQL 15

- **ACID completo** → adecuado para compras, stock y transacciones.
- Compatibilidad nativa con **AWS RDS**, sin cambios en código.
- Excelente soporte para:
    - UUID nativos
    - JSONB
    - Índices avanzados (B-Tree, Hash, GIN, GiST)
- Estándar moderno de empresas cloud-native.
- Funciona perfectamente en contenedores Docker.
- Integración directa con Spring Data JPA.

> PostgreSQL es un motor probado, maduro, confiable y libre — ideal para cargas mixtas como un e-commerce.

---

### 3.2 Gradle (Groovy DSL)

- Builds **más rápidos** que Maven (incremental).
- Sintaxis **más flexible y legible** que XML.
- Plugin system moderno y fácil de extender.
- Mejor experiencia en CI/CD (GitHub Actions, Jenkins, etc.).
- Ideal para modularizar más adelante (microservicios).
- Excelente soporte para Spring Boot.

> Gradle reduce fricción en el desarrollo, facilita refactors y acelera la productividad diaria.

---

## 4. Consecuencias

### Positivas
- Base confiable para lógica transaccional.
- Infraestructura alineada con AWS (RDS, ECS, EC2).
- Tiempo de build significativamente menor.
- Escalabilidad técnica sin cambios radicales.
- Fácil integración con Docker y GitHub Actions.

### Negativas
- Gradle tiene una curva de aprendizaje inicial si uno viene de Maven.
- PostgreSQL avanza rápido en nuevas features (requiere mantenerse actualizado).

---

## 5. Alternativas Rechazadas

### ❌ MySQL / MariaDB
**Motivo de rechazo:**  
Menor solidez en concurrencia y menor soporte en cargas complejas.  
Menor popularidad en ecosistemas cloud-native modernos.

---

### ❌ Maven
**Motivo de rechazo:**
- Builds más lentos.
- Sintaxis XML más rígida.
- Menor flexibilidad para proyectos que evolucionarán a microservicios.

---

## 6. Decisión Final

El proyecto utilizará **PostgreSQL 15 + Gradle** como tecnologías estándar para desarrollo local, despliegue, CI/CD y futura integración con AWS.  
Esta decisión permite una evolución orgánica del proyecto sin refactors profundos y prepara la base para un backend totalmente Cloud-Native.