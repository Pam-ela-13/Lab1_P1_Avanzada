# API de Gestión de Tareas - Documentación Completa

## Descripción General
API REST para gestionar tareas siguiendo arquitectura de capas con Spring Boot 4.0.6 y Java 21.
Incluye validaciones estrictas, paginación, filtrado avanzado y manejo centralizado de excepciones.

## Entidad Tarea

### Campos
- **id**: Identificador único (auto-generado, SERIAL PRIMARY KEY)
- **titulo**: Cadena de 3-100 caracteres (requerido)
- **descripcion**: Cadena de 10-500 caracteres (requerida)
- **estado**: Enum {PENDIENTE, EN_PROGRESO, COMPLETADA} - default: PENDIENTE
- **prioridad**: Entero entre 1 y 5 (requerido)
- **fecha_creacion**: LocalDateTime (auto-generada, no actualizable)

## Arquitectura

### Estructura de Capas
```
controller/       → TareaController (REST endpoints, solo delegación)
service/          → TareaService, TareaServiceImpl (lógica de negocio)
repository/       → TareaRepository (JPA, acceso a datos)
model/entity/     → Tarea (entidad JPA con @Entity)
dto/              → TareaDTO, ResumenTareasDTO (validaciones con @NotBlank, @Size, @Min, @Max)
exception/        → ResourceNotFoundException, GlobalExceptionHandler (@RestControllerAdvice)
```

### Validaciones en DTOs
- **titulo**: @NotBlank, @Size(min=3, max=100)
- **descripcion**: @NotBlank, @Size(min=10, max=500)
- **estado**: @NotNull
- **prioridad**: @NotNull, @Min(1), @Max(5)

### Consultas del Repositorio

#### Por Convención (Derivadas)
```java
List<Tarea> findByEstado(Estado estado);
List<Tarea> findByPrioridad(Integer prioridad);
List<Tarea> findByTituloContainingIgnoreCase(String titulo);
Page<Tarea> findAll(Pageable pageable);
Page<Tarea> findByEstado(Estado estado, Pageable pageable);
```

#### Personalizadas (@Query)
```java
@Query("SELECT t FROM Tarea t WHERE t.prioridad >= :prioridad ORDER BY t.fecha_creacion DESC")
List<Tarea> findTareasPorPrioridadMinima(@Param("prioridad") Integer prioridad);

@Query("SELECT t FROM Tarea t WHERE t.estado = :estado AND t.prioridad <= :prioridad")
List<Tarea> findTareasPorEstadoYPrioridad(@Param("estado") Estado estado, @Param("prioridad") Integer prioridad);

@Query("SELECT t FROM Tarea t WHERE t.estado = 'PENDIENTE' ORDER BY t.prioridad DESC, t.fecha_creacion ASC")
List<Tarea> findTareasPendientesOrdenadas();

@Query("SELECT t.estado, COUNT(t) FROM Tarea t GROUP BY t.estado")
List<Object[]> findResumenTareasPorEstado();
```

---

## FASE 1-3: Endpoints Básicos (CRUD)

### 1. Crear Tarea (POST)
```http
POST /api/tareas
Content-Type: application/json

{
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones",
  "estado": "PENDIENTE",
  "prioridad": 5
}

Respuesta: 201 CREATED
{
  "id": 1,
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones",
  "estado": "PENDIENTE",
  "prioridad": 5,
  "fecha_creacion": "2024-01-15T10:30:00"
}
```

### 2. Obtener Tarea por ID (GET)
```http
GET /api/tareas/1

Respuesta: 200 OK
{
  "id": 1,
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones",
  "estado": "PENDIENTE",
  "prioridad": 5,
  "fecha_creacion": "2024-01-15T10:30:00"
}
```

### 3. Actualizar Tarea (PUT)
```http
PUT /api/tareas/1
Content-Type: application/json

{
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones completas",
  "estado": "EN_PROGRESO",
  "prioridad": 4
}

Respuesta: 200 OK
{
  "id": 1,
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones completas",
  "estado": "EN_PROGRESO",
  "prioridad": 4,
  "fecha_creacion": "2024-01-15T10:30:00"
}
```

### 4. Eliminar Tarea (DELETE)
```http
DELETE /api/tareas/1

Respuesta: 204 NO CONTENT
```

---

## FASE 4: Endpoints Adicionales

### 5. Obtener Todas las Tareas Sin Paginación (GET)
```http
GET /api/tareas/all

Respuesta: 200 OK
[
  {
    "id": 1,
    "titulo": "Implementar API",
    "descripcion": "...",
    "estado": "PENDIENTE",
    "prioridad": 5,
    "fecha_creacion": "2024-01-15T10:30:00"
  }
]
```

### 6. Obtener Tareas con Paginación (GET)
```http
GET /api/tareas?page=0&size=5

Parámetros:
- page: Número de página (default: 0)
- size: Tamaño de página (default: 20)
- sort: Ordenamiento (ej: sort=id,desc)

Respuesta: 200 OK
{
  "content": [
    {
      "id": 1,
      "titulo": "Implementar API",
      "descripcion": "...",
      "estado": "PENDIENTE",
      "prioridad": 5,
      "fecha_creacion": "2024-01-15T10:30:00"
    }
  ],
  "pageable": {
    "pageNumber": 0,
    "pageSize": 5,
    "offset": 0,
    "paged": true,
    "unpaged": false
  },
  "totalElements": 25,
  "totalPages": 5,
  "last": false,
  "size": 5,
  "number": 0,
  "numberOfElements": 5,
  "first": true,
  "empty": false
}
```

### 7. Filtrar por Estado con Paginación (GET)
```http
GET /api/tareas?estado=PENDIENTE&page=0&size=5

Parámetros:
- estado: PENDIENTE, EN_PROGRESO, COMPLETADA (opcional)
- page: Número de página
- size: Tamaño de página

Respuesta: 200 OK (Page<TareaDTO>)
```

### 8. Obtener Resumen de Tareas por Estado (GET)
```http
GET /api/tareas/resumen

Respuesta: 200 OK
{
  "totalTareas": 25,
  "tareasPorEstado": {
    "PENDIENTE": 10,
    "EN_PROGRESO": 8,
    "COMPLETADA": 7
  },
  "tareasCompletadas": 7,
  "tareasPendientes": 10,
  "tareasEnProgreso": 8
}
```

---

## Filtros y Búsquedas Adicionales

### 9. Filtrar por Estado (Sin Paginación)
```http
GET /api/tareas/estado/PENDIENTE

Respuesta: 200 OK
[...]
```

### 10. Filtrar por Prioridad Exacta
```http
GET /api/tareas/prioridad/5

Respuesta: 200 OK
[...]
```

### 11. Filtrar por Prioridad Mínima
```http
GET /api/tareas/prioridad-minima/3

Retorna tareas con prioridad >= 3 ordenadas por fecha descendente

Respuesta: 200 OK
[...]
```

### 12. Buscar por Título
```http
GET /api/tareas/buscar?titulo=Implementar

Búsqueda case-insensitive

Respuesta: 200 OK
[...]
```

### 13. Obtener Tareas Pendientes Ordenadas
```http
GET /api/tareas/pendientes/ordenadas

Retorna tareas PENDIENTES ordenadas por:
  1. Prioridad DESC (mayor prioridad primero)
  2. Fecha de creación ASC (más antiguas primero)

Respuesta: 200 OK
[...]
```

---

## FASE 5: Manejo de Errores y Códigos HTTP

### Códigos HTTP de Éxito Implementados

| Código | Método | Descripción |
|--------|--------|-------------|
| **200** | GET, PUT | Solicitud exitosa, retorna contenido |
| **201** | POST | Recurso creado exitosamente |
| **204** | DELETE | Solicitud exitosa, sin contenido |

### Códigos HTTP de Error Implementados

| Código | Tipo | Descripción |
|--------|------|-------------|
| **400** | Bad Request | Error de validación o argumentos inválidos |
| **404** | Not Found | Recurso no encontrado o ruta no existe |
| **500** | Internal Server Error | Error inesperado del servidor |

### Error de Validación (400)
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 400,
  "error": "Error de Validación",
  "mensaje": "Datos inválidos en la solicitud. Verifique los campos requeridos y sus restricciones.",
  "errores": {
    "titulo": "El título es obligatorio y no puede estar vacío",
    "prioridad": "La prioridad debe ser máximo 5",
    "descripcion": "La descripción debe tener entre 10 y 500 caracteres"
  },
  "ruta": "/api/tareas"
}
```

### Recurso No Encontrado (404)
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 404,
  "error": "Recurso No Encontrado",
  "mensaje": "Tarea no encontrada con ID: 999",
  "ruta": "/api/tareas/999"
}
```

### Ruta No Encontrada (404)
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 404,
  "error": "Ruta No Encontrada",
  "mensaje": "El endpoint solicitado no existe: /api/tareas/invalido",
  "metodo": "GET",
  "ruta": "/api/tareas/invalido"
}
```

### Argumento Inválido (400)
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 400,
  "error": "Argumento Inválido",
  "mensaje": "La prioridad debe estar entre 1 y 5",
  "ruta": "/api/tareas/prioridad/10"
}
```

### Error Interno del Servidor (500)
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 500,
  "error": "Error Interno del Servidor",
  "mensaje": "Ocurrió un error inesperado: ...",
  "ruta": "/api/tareas"
}
```

### Manejador Global de Excepciones

La aplicación implementa `@RestControllerAdvice` (GlobalExceptionHandler) que:
- Captura `MethodArgumentNotValidException` → 400
- Captura `ResourceNotFoundException` → 404
- Captura `NoHandlerFoundException` → 404
- Captura `IllegalArgumentException` → 400
- Captura excepciones genéricas → 500

Todas las respuestas incluyen:
- `timestamp`: Fecha y hora del error
- `status`: Código HTTP
- `error`: Tipo de error
- `mensaje`: Descripción del error
- `ruta`: Endpoint donde ocurrió el error
- `errores`: Detalle por campo (solo en validaciones)

---

## Configuración de Entornos

### Desarrollo (H2)
```properties
spring.profiles.active=dev

# Datos de conexión
spring.datasource.url=jdbc:h2:mem:tareasdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Consola H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Acceso a consola H2**: http://localhost:8080/h2-console

### Producción (PostgreSQL)
```properties
spring.profiles.active=prod

# Datos de conexión
spring.datasource.url=jdbc:postgresql://localhost:5432/tareasdb
spring.datasource.username=postgres
spring.datasource.password=1234

# JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

### Script SQL para PostgreSQL
```sql
CREATE DATABASE tareasdb;

CREATE TABLE tareas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    prioridad INTEGER NOT NULL CHECK (prioridad >= 1 AND prioridad <= 5),
    fecha_creacion TIMESTAMP NOT NULL
);
```

---

## Ejecución

### Compilar
```bash
mvn clean compile
```

### Desarrollo (H2)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Producción (PostgreSQL)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

---

## Pruebas de Ejemplo con cURL

### Crear tarea
```bash
curl -X POST http://localhost:8080/api/tareas \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Desarrollar módulo de autenticación",
    "descripcion": "Implementar autenticación JWT en la aplicación REST",
    "estado": "PENDIENTE",
    "prioridad": 5
  }'
```

### Listar todas (paginado)
```bash
curl "http://localhost:8080/api/tareas?page=0&size=5"
```

### Filtrar por estado (paginado)
```bash
curl "http://localhost:8080/api/tareas?estado=PENDIENTE&page=0&size=5"
```

### Obtener resumen
```bash
curl http://localhost:8080/api/tareas/resumen
```

### Obtener por ID
```bash
curl http://localhost:8080/api/tareas/1
```

### Buscar por título
```bash
curl "http://localhost:8080/api/tareas/buscar?titulo=autenticacion"
```

### Actualizar
```bash
curl -X PUT http://localhost:8080/api/tareas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Desarrollar módulo de autenticación",
    "descripcion": "Implementar autenticación JWT en la aplicación REST",
    "estado": "EN_PROGRESO",
    "prioridad": 4
  }'
```

### Eliminar
```bash
curl -X DELETE http://localhost:8080/api/tareas/1
```

---

## Dependencias Principales

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```

---

## Características Implementadas

### ✅ FASE 1: Persistencia (JPA y Repositorios)
- Entidad Tarea con @Entity y @Table
- TareaRepository heredando de JpaRepository
- Consultas por convención: `findByEstado()`, `findByPrioridad()`, `findByTituloContainingIgnoreCase()`
- Consultas personalizadas con @Query: `findTareasPorPrioridadMinima()`, `findTareasPorEstadoYPrioridad()`, `findTareasPendientesOrdenadas()`, `findResumenTareasPorEstado()`

### ✅ FASE 2: Validaciones (DTOs)
- TareaDTO con @NotBlank, @Size, @Min, @Max
- @Valid en todos los endpoints POST y PUT
- GlobalExceptionHandler captura MethodArgumentNotValidException
- Mensajes de validación personalizados por campo

### ✅ FASE 3: Endpoints Básicos (CRUD)
- GET /api/tareas/all (Listar todas)
- GET /api/tareas/{id} (Obtener por ID)
- POST /api/tareas (Crear) - 201 CREATED
- PUT /api/tareas/{id} (Actualizar) - 200 OK
- DELETE /api/tareas/{id} (Eliminar) - 204 NO CONTENT

### ✅ FASE 4: Endpoints Adicionales
- GET /api/tareas?page=0&size=5 (Paginación)
- GET /api/tareas?estado=PENDIENTE&page=0&size=5 (Filtrado por estado con paginación)
- GET /api/tareas/resumen (Conteo agrupado por estado)
- Endpoints adicionales sin paginación mantienen compatibilidad

### ✅ FASE 5: Manejo de Errores y Códigos HTTP
- @RestControllerAdvice (GlobalExceptionHandler) centralizado
- Códigos de éxito: 200 (GET), 201 (POST), 204 (DELETE)
- Códigos de error: 400 (validación/argumentos), 404 (no encontrado), 500 (genérico)
- Manejo de NoHandlerFoundException (404)
- Respuestas estructuradas con timestamp, status, error, mensaje, ruta

---

## Versión
API v2.0 - Desarrollada con Spring Boot 4.0.6 y Java 21

## Entidad Tarea

### Campos
- **id**: Identificador único (auto-generado, SERIAL PRIMARY KEY)
- **titulo**: Cadena de 3-100 caracteres (requerido)
- **descripcion**: Cadena de 10-500 caracteres (requerida)
- **estado**: Enum {PENDIENTE, EN_PROGRESO, COMPLETADA} - default: PENDIENTE
- **prioridad**: Entero entre 1 y 5 (requerido)
- **fecha_creacion**: LocalDateTime (auto-generada, no actualizable)

## Arquitectura

### Estructura de Capas
```
controller/       → TareaController (REST endpoints, solo delegación)
service/          → TareaService, TareaServiceImpl (lógica de negocio)
repository/       → TareaRepository (JPA, acceso a datos)
model/entity/     → Tarea (entidad JPA con @Entity)
dto/              → TareaDTO (validaciones con @NotBlank, @Size, @Min, @Max)
exception/        → ResourceNotFoundException, GlobalExceptionHandler
```

### Validaciones en DTOs
- **titulo**: @NotBlank, @Size(min=3, max=100)
- **descripcion**: @NotBlank, @Size(min=10, max=500)
- **estado**: @NotNull
- **prioridad**: @NotNull, @Min(1), @Max(5)

### Consultas del Repositorio

#### Por Convención (Derivadas)
```java
List<Tarea> findByEstado(Estado estado);
List<Tarea> findByPrioridad(Integer prioridad);
List<Tarea> findByTituloContainingIgnoreCase(String titulo);
```

#### Personalizadas (@Query)
```java
@Query("SELECT t FROM Tarea t WHERE t.prioridad >= :prioridad ORDER BY t.fecha_creacion DESC")
List<Tarea> findTareasPorPrioridadMinima(@Param("prioridad") Integer prioridad);

@Query("SELECT t FROM Tarea t WHERE t.estado = :estado AND t.prioridad <= :prioridad")
List<Tarea> findTareasPorEstadoYPrioridad(@Param("estado") Estado estado, @Param("prioridad") Integer prioridad);

@Query("SELECT t FROM Tarea t WHERE t.estado = 'PENDIENTE' ORDER BY t.prioridad DESC, t.fecha_creacion ASC")
List<Tarea> findTareasPendientesOrdenadas();
```

## Endpoints de la API

### 1. Crear Tarea (POST)
```http
POST /api/tareas
Content-Type: application/json

{
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones",
  "estado": "PENDIENTE",
  "prioridad": 5
}

Respuesta: 201 CREATED
{
  "id": 1,
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones",
  "estado": "PENDIENTE",
  "prioridad": 5,
  "fecha_creacion": "2024-01-15T10:30:00"
}
```

### 2. Obtener Tarea por ID (GET)
```http
GET /api/tareas/1

Respuesta: 200 OK
{
  "id": 1,
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones",
  "estado": "PENDIENTE",
  "prioridad": 5,
  "fecha_creacion": "2024-01-15T10:30:00"
}
```

### 3. Obtener Todas las Tareas (GET)
```http
GET /api/tareas

Respuesta: 200 OK
[
  {
    "id": 1,
    "titulo": "Implementar API",
    "descripcion": "Crear API REST con Spring Boot y validaciones",
    "estado": "PENDIENTE",
    "prioridad": 5,
    "fecha_creacion": "2024-01-15T10:30:00"
  }
]
```

### 4. Filtrar por Estado (GET)
```http
GET /api/tareas/estado/PENDIENTE

Estados válidos: PENDIENTE, EN_PROGRESO, COMPLETADA

Respuesta: 200 OK
[...]
```

### 5. Filtrar por Prioridad Exacta (GET)
```http
GET /api/tareas/prioridad/5

Prioridad: 1-5

Respuesta: 200 OK
[...]
```

### 6. Filtrar por Prioridad Mínima (GET)
```http
GET /api/tareas/prioridad-minima/3

Retorna tareas con prioridad >= 3 ordenadas por fecha descendente
(Utiliza @Query personalizada)

Respuesta: 200 OK
[...]
```

### 7. Buscar por Título (GET)
```http
GET /api/tareas/buscar?titulo=Implementar

Búsqueda case-insensitive

Respuesta: 200 OK
[...]
```

### 8. Obtener Tareas Pendientes Ordenadas (GET)
```http
GET /api/tareas/pendientes/ordenadas

Retorna tareas PENDIENTES ordenadas por:
  1. Prioridad DESC (mayor prioridad primero)
  2. Fecha de creación ASC (más antiguas primero)
(Utiliza @Query personalizada)

Respuesta: 200 OK
[...]
```

### 9. Actualizar Tarea (PUT)
```http
PUT /api/tareas/1
Content-Type: application/json

{
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones completas",
  "estado": "EN_PROGRESO",
  "prioridad": 4
}

Respuesta: 200 OK
{
  "id": 1,
  "titulo": "Implementar API",
  "descripcion": "Crear API REST con Spring Boot y validaciones completas",
  "estado": "EN_PROGRESO",
  "prioridad": 4,
  "fecha_creacion": "2024-01-15T10:30:00"
}
```

### 10. Eliminar Tarea (DELETE)
```http
DELETE /api/tareas/1

Respuesta: 204 NO CONTENT
```

## Manejo de Errores

### Errores de Validación
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 400,
  "error": "Error de Validación",
  "mensaje": "Datos inválidos en la solicitud",
  "errores": {
    "titulo": "El título es obligatorio y no puede estar vacío",
    "prioridad": "La prioridad debe ser máximo 5"
  },
  "ruta": "/api/tareas"
}
```

### Recurso No Encontrado
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 404,
  "error": "Recurso No Encontrado",
  "mensaje": "Tarea no encontrada con ID: 999",
  "ruta": "/api/tareas/999"
}
```

### Argumento Inválido
```json
{
  "timestamp": "2024-01-15T10:35:22",
  "status": 400,
  "error": "Argumento Inválido",
  "mensaje": "La prioridad debe estar entre 1 y 5",
  "ruta": "/api/tareas/prioridad/10"
}
```

### Códigos de Estado HTTP
- **201 Created**: Tarea creada exitosamente
- **200 OK**: Solicitud exitosa
- **204 No Content**: Solicitud exitosa sin contenido
- **400 Bad Request**: Datos inválidos o validación fallida
- **404 Not Found**: Recurso no encontrado
- **500 Internal Server Error**: Error del servidor

## Configuración de Entornos

### Desarrollo (H2)
```properties
spring.profiles.active=dev

# Datos de conexión
spring.datasource.url=jdbc:h2:mem:tareasdb
spring.datasource.driverClassName=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=

# Consola H2
spring.h2.console.enabled=true
spring.h2.console.path=/h2-console

# JPA
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

**Acceso a consola H2**: http://localhost:8080/h2-console

### Producción (PostgreSQL)
```properties
spring.profiles.active=prod

# Datos de conexión
spring.datasource.url=jdbc:postgresql://localhost:5432/tareasdb
spring.datasource.username=postgres
spring.datasource.password=1234

# JPA
spring.jpa.database-platform=org.hibernate.dialect.PostgreSQLDialect
spring.jpa.hibernate.ddl-auto=validate
spring.jpa.show-sql=true
```

### Script SQL para PostgreSQL
```sql
CREATE DATABASE tareasdb;

CREATE TABLE tareas (
    id SERIAL PRIMARY KEY,
    titulo VARCHAR(100) NOT NULL,
    descripcion VARCHAR(500) NOT NULL,
    estado VARCHAR(20) NOT NULL,
    prioridad INTEGER NOT NULL CHECK (prioridad >= 1 AND prioridad <= 5),
    fecha_creacion TIMESTAMP NOT NULL
);
```

## Ejecución

### Compilar
```bash
mvn clean compile
```

### Desarrollo (H2)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=dev"
```

### Producción (PostgreSQL)
```bash
mvn spring-boot:run -Dspring-boot.run.arguments="--spring.profiles.active=prod"
```

## Pruebas de Ejemplo con cURL

### Crear tarea
```bash
curl -X POST http://localhost:8080/api/tareas \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Desarrollar módulo de autenticación",
    "descripcion": "Implementar autenticación JWT en la aplicación REST",
    "estado": "PENDIENTE",
    "prioridad": 5
  }'
```

### Listar todas
```bash
curl http://localhost:8080/api/tareas
```

### Obtener por ID
```bash
curl http://localhost:8080/api/tareas/1
```

### Filtrar por estado
```bash
curl http://localhost:8080/api/tareas/estado/PENDIENTE
```

### Buscar por título
```bash
curl "http://localhost:8080/api/tareas/buscar?titulo=autenticacion"
```

### Actualizar
```bash
curl -X PUT http://localhost:8080/api/tareas/1 \
  -H "Content-Type: application/json" \
  -d '{
    "titulo": "Desarrollar módulo de autenticación",
    "descripcion": "Implementar autenticación JWT en la aplicación REST",
    "estado": "EN_PROGRESO",
    "prioridad": 4
  }'
```

### Eliminar
```bash
curl -X DELETE http://localhost:8080/api/tareas/1
```

## Dependencias Principales

```xml
<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-web</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<dependency>
  <groupId>org.springframework.boot</groupId>
  <artifactId>spring-boot-starter-validation</artifactId>
</dependency>

<dependency>
  <groupId>com.h2database</groupId>
  <artifactId>h2</artifactId>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>org.postgresql</groupId>
  <artifactId>postgresql</artifactId>
  <scope>runtime</scope>
</dependency>

<dependency>
  <groupId>org.projectlombok</groupId>
  <artifactId>lombok</artifactId>
  <optional>true</optional>
</dependency>
```

## Características Implementadas

✅ **Fase 1: Persistencia (JPA y Repositorios)**
- Entidad Tarea con @Entity y @Table
- TareaRepository heredando de JpaRepository
- Consultas por convención: `findByEstado()`, `findByPrioridad()`, `findByTituloContainingIgnoreCase()`
- Consultas personalizadas con @Query: `findTareasPorPrioridadMinima()`, `findTareasPorEstadoYPrioridad()`, `findTareasPendientesOrdenadas()`

✅ **Fase 2: Validaciones (DTOs)**
- TareaDTO con @NotBlank, @Size, @Min, @Max
- @Valid en todos los endpoints (POST, PUT)
- GlobalExceptionHandler captura MethodArgumentNotValidException
- Mensajes de validación personalizados

✅ **Fase 3: Endpoints Básicos (CRUD)**
- GET /api/tareas (Listar todas)
- GET /api/tareas/{id} (Obtener por ID)
- POST /api/tareas (Crear)
- PUT /api/tareas/{id} (Actualizar)
- DELETE /api/tareas/{id} (Eliminar)

## Versión
API v1.0 - Desarrollada con Spring Boot 4.0.6 y Java 21
