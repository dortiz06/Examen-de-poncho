# parcel-service

Microservicio REST para registrar y consultar parcelas de una red de huertos urbanos con sensores IoT.

- Java 17
- Spring Boot 3.3
- Maven
- Spring Web, Spring Data JPA, Spring Validation
- PostgreSQL

## Requisitos

- JDK 17
- Maven 3.9+
- Docker y Docker Compose (opcional, recomendado)

## Variables de entorno

| Variable | Descripción | Valor por defecto |
| --- | --- | --- |
| `POSTGRES_HOST` | Host de PostgreSQL | `localhost` |
| `POSTGRES_PORT` | Puerto de PostgreSQL | `5432` |
| `POSTGRES_DB` | Nombre de la base | `huertos` |
| `POSTGRES_USER` | Usuario | `huertos` |
| `POSTGRES_PASSWORD` | Contraseña | `huertos` |
| `SERVER_PORT` | Puerto HTTP del servicio | `8080` |

## Cómo ejecutarlo

### Con Docker Compose (servicio + PostgreSQL)

```bash
docker compose up --build
```

El API queda en `http://localhost:8080`.

### En local (PostgreSQL ya levantado)

```bash
# Solo la base de datos
docker compose up -d postgres

# Compilar y probar
mvn clean verify

# Arrancar
mvn spring-boot:run
```

## Endpoints

| Método | Ruta | Descripción |
| --- | --- | --- |
| `POST` | `/api/v1/parcels` | Crea una parcela (`201 Created` + header `Location`) |
| `GET` | `/api/v1/parcels` | Lista todas las parcelas |
| `GET` | `/api/v1/parcels/{id}` | Obtiene una parcela por UUID |

`soilType` acepta: `ARCILLOSO`, `LIMOSO`, `ARENOSO` (se normaliza a mayúsculas).

`createdAt` lo asigna el servidor. No se envía en el alta.

## Ejemplos de curl

### Creación exitosa (201)

```bash
curl -i -X POST http://localhost:8080/api/v1/parcels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Huerto Centro",
    "latitude": 19.4326,
    "longitude": -99.1332,
    "soilType": "limoso",
    "ownerOrLead": "Ana Lopez"
  }'
```

Respuesta esperada: HTTP `201`, header `Location: /api/v1/parcels/{uuid}` y el cuerpo con `id`, `name`, coordenadas, `soilType`, `ownerOrLead` y `createdAt`.

### Listado (200)

```bash
curl -i http://localhost:8080/api/v1/parcels
```

### Consulta por id (200)

```bash
curl -i http://localhost:8080/api/v1/parcels/11111111-1111-1111-1111-111111111111
```

Reemplaza el UUID por el que devolvió el alta.

### Validación fallida (400)

```bash
curl -i -X POST http://localhost:8080/api/v1/parcels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "ab",
    "latitude": 120,
    "longitude": -200,
    "soilType": "ARENOSO",
    "ownerOrLead": "x"
  }'
```

Respuesta esperada: HTTP `400` con `fieldErrors` para `name`, `latitude`, `longitude` y `ownerOrLead`.

### JSON malformado (400)

```bash
curl -i -X POST http://localhost:8080/api/v1/parcels \
  -H "Content-Type: application/json" \
  -d '{ name: sin comillas }'
```

### Tipo de suelo inválido (400)

```bash
curl -i -X POST http://localhost:8080/api/v1/parcels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Huerto Centro",
    "latitude": 19.4326,
    "longitude": -99.1332,
    "soilType": "VOLCANICO",
    "ownerOrLead": "Ana Lopez"
  }'
```

### Parcela no encontrada (404)

```bash
curl -i http://localhost:8080/api/v1/parcels/00000000-0000-0000-0000-000000000000
```

### Duplicado (409)

El mismo `name` + `ownerOrLead` no se puede registrar dos veces:

```bash
curl -i -X POST http://localhost:8080/api/v1/parcels \
  -H "Content-Type: application/json" \
  -d '{
    "name": "Huerto Centro",
    "latitude": 19.4326,
    "longitude": -99.1332,
    "soilType": "LIMOSO",
    "ownerOrLead": "Ana Lopez"
  }'
```

Ejecuta el mismo comando otra vez. La segunda petición responde `409 Conflict`.

### UUID con formato inválido (400)

```bash
curl -i http://localhost:8080/api/v1/parcels/no-es-un-uuid
```

## Pruebas

```bash
mvn clean verify
```

Los tests de `MockMvc` cubren creación exitosa, validación, parcela no encontrada, duplicado, JSON malformado y `soilType` inválido.
