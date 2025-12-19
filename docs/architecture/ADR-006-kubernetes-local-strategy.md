# ADR-006: Estrategia de Desarrollo Local Kubernetes

## Contexto
El equipo necesita un entorno de desarrollo local para Kubernetes. Las opciones evaluadas son:
1. **Minikube** (en WSL2)
2. **Kind** (Kubernetes in Docker)
3. **Docker Desktop con Kubernetes**

## Problema Identificado
Durante las pruebas en WSL2 con Docker Engine nativo, encontramos problemas persistentes de **cgroups v2 + systemd**:
- Error: `Couldn't move process to requested cgroup: Device or resource busy`
- Los contenedores mueren después de 30-60 segundos
- Afecta tanto a Minikube como a Kind

## Análisis de Opciones

### Minikube en WSL2
| Aspecto | Evaluación |
|---------|------------|
| Compatibilidad | ⚠️ Problemas de cgroups en WSL2 |
| Recursos | Alto consumo (VM dentro de WSL) |
| Features | Completo (addons, ingress, dashboard) |
| Popularidad | Alta, bien documentado |

### Kind (Kubernetes in Docker)
| Aspecto | Evaluación |
|---------|------------|
| Compatibilidad | ⚠️ Mismos problemas de cgroups |
| Recursos | Ligero (contenedores) |
| Features | Básico (ideal para CI/CD) |
| Popularidad | Creciente, usado en CI |

### Docker Desktop con Kubernetes
| Aspecto | Evaluación |
|---------|------------|
| Compatibilidad | ✅ Estable en Windows |
| Recursos | Moderado |
| Features | Básico pero suficiente |
| Popularidad | Alta en Windows |

## Decisión
**Docker Desktop con Kubernetes integrado** para desarrollo local en Windows.

## Justificación

1. **Estabilidad**: Docker Desktop maneja correctamente los cgroups en Windows/WSL2
2. **Simplicidad**: Un solo clic para habilitar Kubernetes
3. **Compatibilidad**: Funciona sin configuraciones adicionales de WSL
4. **Soporte**: Mantenido activamente por Docker Inc.
5. **Integración VS Code**: Funciona bien con Dev Containers y extensiones

## Consecuencias

### Positivas
- Desarrollo local estable sin errores de cgroups
- Menor complejidad en onboarding de desarrolladores
- Consistencia entre ambientes (dev → staging → prod)

### Negativas
- Requiere licencia Docker Desktop para empresas > 250 empleados (no aplica a Carrillo Abogados)
- Menos control granular que Minikube
- No disponible en Linux headless (solo para Windows/Mac)

## Implementación

### Habilitar Kubernetes en Docker Desktop
1. Abrir Docker Desktop
2. Settings → Kubernetes → Enable Kubernetes
3. Apply & Restart
4. Verificar: `kubectl cluster-info`

### Para producción y CI/CD
- **GKE Autopilot**: Producción
- **Kind**: GitHub Actions CI/CD (sí funciona en CI)

## Fecha
18 de Diciembre, 2025

## Estado
**Aceptada**
