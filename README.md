# Tempo Backend

Tempo es una API REST para gestión de tareas y productividad. El backend está construido con Java, Spring Boot, Spring Data JPA y PostgreSQL.

El estado actual corresponde al Sprint 1 e incluye perfiles de usuario, proyectos, tareas, categorías, etiquetas y estados configurables. Las funcionalidades de Pomodoro, sesiones de trabajo, recurrencia, planificación avanzada, estadísticas, colaboración y autenticación completa todavía no forman parte de la API pública.

## Tecnologías

- Java 21
- Spring Boot 4.1.1
- Spring Web MVC
- Spring Data JPA
- Hibernate
- PostgreSQL
- Bean Validation
- springdoc-openapi 3.1.0
- Gradle Wrapper
- JUnit 5 y Mockito

## Arquitectura

```text
HTTP request
    ↓
Controller / DTO validation
    ↓
Service / business rules / transactions
    ↓
Repository / Spring Data JPA
    ↓
PostgreSQL
```

Los controllers no acceden directamente a la base de datos. Los DTOs definen el contrato HTTP y evitan exponer entidades JPA, relaciones lazy o campos sensibles como la contraseña.

## Estructura principal

```text
src/main/java/com/ashvyn/tempo/
├── config/       Configuración de OpenAPI
├── controller/   Endpoints REST
├── dto/          Requests y responses por recurso
├── entity/       Entidades JPA
├── enums/        Enumeraciones del dominio
├── exception/    Excepciones y respuestas de error
├── repository/   Acceso a datos con Spring Data JPA
└── service/      Reglas de negocio y transacciones
```

## Configuración local

La aplicación utiliza el perfil `dev` y espera PostgreSQL en:

```text
jdbc:postgresql://localhost:5433/tempo_db
```

Revisa `src/main/resources/application-dev.properties` para conocer la configuración local actual. No publiques credenciales reales en repositorios públicos; para otros entornos se recomienda usar variables de entorno o un gestor de secretos.

## Ejecutar el proyecto

En Windows:

```powershell
.\gradlew.bat bootRun
```

En Linux o macOS:

```bash
./gradlew bootRun
```

La aplicación queda disponible en:

```text
http://localhost:8080/tempo/api
```

## Build y tests

```powershell
.\gradlew.bat clean build
.\gradlew.bat test
```

## Datos iniciales de desarrollo

El perfil `dev` incluye un inicializador en `config/DevDataInitializer.java`. Cuando la tabla de usuarios está vacía, al arrancar la aplicación crea automáticamente un conjunto relacionado de datos para probar la API:

- 2 usuarios: Ana Torres y Mateo Ruiz
- 2 proyectos: Tempo MVP y REST API
- 3 tareas personales/de proyecto, incluida una subtarea
- 3 categorías globales/de usuario
- 3 labels globales/de usuario
- 4 estados globales/de usuario
- relaciones entre tareas y labels

El proceso es seguro para una base que ya contiene usuarios: en ese caso no inserta ni modifica información. Para deshabilitarlo incluso en una base vacía, cambia la propiedad:

```properties
tempo.seed.enabled=false
```

El inicializador solo está activo con el perfil `dev`; los tests deshabilitan explícitamente la carga para no contaminar la base durante el build. Las credenciales incluidas son únicamente demostrativas y no deben utilizarse fuera del entorno local:

```text
ana@tempo.local / tempo-demo
mateo@tempo.local / tempo-demo
```

## Documentación interactiva

Con la aplicación iniciada:

- Swagger UI: `http://localhost:8080/tempo/api/swagger-ui/index.html`
- OpenAPI JSON: `http://localhost:8080/tempo/api/v3/api-docs`

Swagger UI permite consultar esquemas, parámetros y respuestas, además de ejecutar solicitudes directamente desde el navegador.

## Colección de Postman

El directorio `postman/` contiene archivos importables para probar la API sin frontend:

- `Tempo API.postman_collection.json`: colección con todos los endpoints del Sprint 1, OpenAPI y Swagger UI.
- `Tempo API.postman_environment.json`: entorno local con `baseUrl` y variables para los IDs.

En Postman, importa ambos archivos y selecciona el environment **Tempo Local**. Inicia la aplicación y ejecuta las carpetas en el orden mostrado. Cada request `Create` guarda automáticamente el ID de su respuesta para que las operaciones `Get`, `Update` y las relaciones posteriores lo reutilicen.

La carpeta **Delete operations** se ejecuta manualmente al final. Tasks, Projects, Categories y Labels usan soft delete. La eliminación de un estado o usuario puede devolver `409 Conflict` si conserva relaciones con registros eliminados lógicamente; este comportamiento protege la integridad referencial existente.

## Endpoints del Sprint 1

La ruta declarada por los controllers comienza en `/api/v1`. Como el proyecto usa el context path `/tempo/api`, una llamada HTTP completa tiene la forma:

```text
http://localhost:8080/tempo/api/api/v1/tasks
```

| Método | Ruta | Descripción |
|---|---|---|
| POST | `/api/v1/users` | Crear usuario |
| GET | `/api/v1/users` | Listar usuarios |
| GET | `/api/v1/users/{id}` | Consultar usuario |
| PUT | `/api/v1/users/{id}` | Actualizar perfil |
| DELETE | `/api/v1/users/{id}` | Eliminar usuario |
| POST | `/api/v1/projects` | Crear proyecto |
| GET | `/api/v1/projects` | Listar proyectos activos; acepta `ownerId` |
| GET | `/api/v1/projects/{id}` | Consultar proyecto activo |
| PUT | `/api/v1/projects/{id}` | Actualizar proyecto |
| DELETE | `/api/v1/projects/{id}` | Eliminar proyecto lógicamente |
| POST | `/api/v1/tasks` | Crear tarea |
| GET | `/api/v1/tasks` | Listar tareas activas con un filtro opcional |
| GET | `/api/v1/tasks/{id}` | Consultar tarea activa |
| PUT | `/api/v1/tasks/{id}` | Actualizar tarea |
| DELETE | `/api/v1/tasks/{id}` | Eliminar tarea lógicamente |
| POST | `/api/v1/categories` | Crear categoría global o de usuario |
| GET | `/api/v1/categories` | Listar categorías activas |
| GET | `/api/v1/categories/{id}` | Consultar categoría activa |
| PUT | `/api/v1/categories/{id}` | Actualizar categoría |
| DELETE | `/api/v1/categories/{id}` | Eliminar categoría lógicamente |
| POST | `/api/v1/labels` | Crear etiqueta global o de usuario |
| GET | `/api/v1/labels` | Listar etiquetas activas |
| GET | `/api/v1/labels/{id}` | Consultar etiqueta activa |
| PUT | `/api/v1/labels/{id}` | Actualizar etiqueta |
| DELETE | `/api/v1/labels/{id}` | Eliminar etiqueta lógicamente |
| POST | `/api/v1/task-statuses` | Crear estado global o personalizado |
| GET | `/api/v1/task-statuses` | Listar estados ordenados por posición |
| GET | `/api/v1/task-statuses/{id}` | Consultar estado |
| PUT | `/api/v1/task-statuses/{id}` | Actualizar estado |
| DELETE | `/api/v1/task-statuses/{id}` | Eliminar estado |

### Filtros disponibles

- Projects: `ownerId`
- Categories y labels: `ownerId` o `global=true`
- Task statuses: `ownerId` o `global=true`
- Tasks: `personalOwnerId`, `projectId`, `responsibleUserId` o `parentTaskId`

Para Tasks solo se admite un filtro por solicitud. Esta limitación mantiene las consultas simples durante el MVP.

## Reglas de dominio relevantes

- Una tarea pertenece exactamente a un contexto: usuario personal o proyecto, nunca a ambos ni a ninguno.
- Toda tarea tiene un usuario responsable y un estado.
- Una tarea no puede ser su propia tarea padre.
- Un proyecto no puede ser su propio padre y el proyecto padre debe pertenecer al mismo usuario.
- Tasks, Projects, Categories y Labels usan eliminación lógica mediante `deletedAt`.
- Los recursos eliminados lógicamente no aparecen en consultas normales.
- Categories, Labels y TaskStatus pueden ser globales cuando `owner` es `null`.

## Formato de errores

La API devuelve errores consistentes:

```json
{
  "timestamp": "2026-10-05T20:30:00Z",
  "status": 404,
  "error": "Not Found",
  "message": "Task not found: 00000000-0000-0000-0000-000000000000",
  "path": "/tempo/api/api/v1/tasks/00000000-0000-0000-0000-000000000000"
}
```

- `400 Bad Request`: JSON inválido, validación o regla de negocio.
- `404 Not Found`: recurso inexistente o eliminado lógicamente.
- `409 Conflict`: duplicados o restricciones de integridad.
- `500 Internal Server Error`: error inesperado.

## Ejemplo: crear una categoría global

```bash
curl -X POST "http://localhost:8080/tempo/api/api/v1/categories" \
  -H "Content-Type: application/json" \
  -d '{"ownerId":null,"name":"Trabajo"}'
```

## Estado de seguridad

Spring Security, JWT, OAuth y el tratamiento definitivo de contraseñas todavía no están implementados. Los IDs de usuario se reciben explícitamente en los requests cuando son necesarios. Esta decisión es temporal para el MVP y no representa un modelo definitivo de autenticación o autorización.

## Alcance pendiente

- Autenticación y autorización completas
- Pomodoro y sesiones de trabajo
- Recurrencia
- Planificación avanzada
- Estadísticas
- Colaboración
- Funciones de AI integradas al flujo principal
