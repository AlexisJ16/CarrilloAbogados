# ADR-005: Estrategia de Base de Datos Compartida

## Contexto
Microservicios pueden usar DB independiente o compartida.

## Decisión
PostgreSQL compartido con schema por servicio.

## Rationale
**Ventajas:**
- Simplicidad operacional (1 PostgreSQL)
- Transacciones cross-schema si necesario
- Menor costo en desarrollo
- Migraciones más simples

**Desventajas:**
- Acoplamiento en BD
- Escalabilidad limitada a futuro

## Consecuencias
- Facilita MVP y desarrollo inicial
- Migración a DBs separadas posible en futuro si necesario
- Flyway gestiona schemas independientes