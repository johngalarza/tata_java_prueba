# Prueba Tecnica - Arquitectura de Microservicios

Implementacion de una solucion backend para una aplicacion bancaria utilizando una arquitectura basada en microservicios.

La solucion separa las responsabilidades de clientes y cuentas en servicios independientes, cada uno con su propia base de datos PostgreSQL. La comunicacion asincrona entre servicios se realiza mediante RabbitMQ.

## Tecnologias

* Java 21
* Spring Boot 4.0.8
* Spring Data JPA
* Hibernate
* PostgreSQL 16
* RabbitMQ 4
* Maven
* Docker
* Docker Compose
* JUnit 5
* Mockito
* MockMvc
* OpenAPI / Swagger

## Arquitectura

La aplicacion esta dividida en dos microservicios:

```text
                         +-------------------+
                         |      Cliente      |
                         |     Service       |
                         |      :8081        |
                         +---------+---------+
                                   |
                                   | RabbitMQ
                                   |
                         +---------v---------+
                         |      Cuenta       |
                         |     Service       |
                         |      :8082        |
                         +---------+---------+
                                   |
                    +--------------+--------------+
                    |                             |
             +------v------+               +------v------+
             | cliente-db  |               |  cuenta-db  |
             | PostgreSQL  |               | PostgreSQL  |
             +-------------+               +-------------+
```

### cliente-service

Responsable de la gestion de:

* Persona
* Cliente

Puerto:

```text
8081
```

Base de datos:

```text
cliente_db
```

### cuenta-service

Responsable de la gestion de:

* Cuenta
* Movimiento
* Reportes

Puerto:

```text
8082
```

Base de datos:

```text
cuenta_db
```

### RabbitMQ

Se utiliza como broker de mensajeria para permitir comunicacion asincrona entre los microservicios.

Puerto AMQP:

```text
5672
```

Panel de administracion:

```text
http://localhost:15672
```

## Estructura del proyecto

```text
TATA_Dev_Backend_Java/
│
├── cliente-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── cuenta-service/
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/
│   │   │   └── resources/
│   │   └── test/
│   ├── Dockerfile
│   └── pom.xml
│
├── docker-compose.yml
├── BaseDatos.sql
├── postman/
└── README.md
```

## Patrones y buenas practicas

La implementacion utiliza una separacion por capas:

```text
Controller
    |
    v
Service
    |
    v
Repository
    |
    v
Database
```

Se utilizan:

* Repository Pattern mediante Spring Data JPA
* DTOs para entrada y salida de informacion
* Inyeccion de dependencias
* Validacion mediante Jakarta Validation
* Manejo centralizado de excepciones
* Transacciones mediante `@Transactional`
* Separacion de responsabilidades
* Bases de datos independientes por microservicio
* Comunicacion asincrona mediante RabbitMQ

Los microservicios no comparten entidades JPA entre ellos. El `clienteId` funciona como referencia entre el servicio de clientes y el servicio de cuentas.

## Entidades principales

### Persona

Contiene:

* id
* nombre
* genero
* edad
* identificacion
* direccion
* telefono

### Cliente

Hereda de Persona y contiene:

* clienteId
* contrasena
* estado

El `clienteId` e identificacion son valores unicos.

### Cuenta

Contiene:

* id
* numeroCuenta
* tipoCuenta
* saldoInicial
* saldoActual
* estado
* clienteId

El numero de cuenta es unico.

### Movimiento

Contiene:

* id
* fecha
* tipoMovimiento
* valor
* saldo
* cuenta

Los movimientos mantienen el historial de transacciones realizadas sobre una cuenta.

## Funcionalidades

### F1 - CRUD

Se implementan operaciones CRUD para:

```text
/api/clientes
/api/cuentas
/api/movimientos
```

Operaciones disponibles:

```text
POST
GET
PUT
DELETE
```

### F2 - Registro de movimientos

Los movimientos pueden representar depositos y retiros.

Un deposito incrementa el saldo:

```text
saldo actual + valor
```

Un retiro disminuye el saldo:

```text
saldo actual - valor
```

Cada movimiento se almacena junto con el saldo resultante.

La actualizacion del saldo y el registro del movimiento se ejecutan dentro de una transaccion para mantener la consistencia de los datos.

### F3 - Validacion de saldo

Cuando un retiro genera un saldo negativo, la operacion es rechazada.

Respuesta:

```json
{
  "mensaje": "Saldo no disponible"
}
```

HTTP:

```text
400 Bad Request
```

El movimiento rechazado no es registrado y el saldo de la cuenta permanece sin cambios.

### F4 - Estado de cuenta

Se implemento un endpoint para consultar el estado de cuenta de un cliente en un rango de fechas.

```text
GET /api/reportes
```

Parametros:

```text
clienteId
fechaInicio
fechaFin
```

Ejemplo:

```text
GET /api/reportes?clienteId=jose&fechaInicio=2026-09-01T00:00:00&fechaFin=2026-09-30T23:59:59
```

La respuesta contiene las cuentas asociadas al cliente, su saldo actual y los movimientos realizados dentro del rango solicitado.

Ejemplo:

```json
[
  {
    "clienteId": "jose",
    "numeroCuenta": "478758",
    "tipoCuenta": "Ahorros",
    "saldoActual": 1100.00,
    "movimientos": [
      {
        "id": 1,
        "fecha": "2026-09-24T06:35:25",
        "tipoMovimiento": "Deposito",
        "valor": 600,
        "saldo": 1600.00,
        "numeroCuenta": "478758"
      }
    ]
  }
]
```

## Manejo de errores

La aplicacion cuenta con un manejo centralizado de excepciones mediante un `GlobalExceptionHandler`.

Se manejan, entre otros:

* Recursos no encontrados
* Conflictos por registros duplicados
* Errores de validacion
* Saldo insuficiente

Ejemplo de validacion:

```json
{
  "mensaje": "Error de validacion",
  "errores": {
    "nombre": "no debe estar vacio"
  }
}
```

## Validaciones

Se utilizan anotaciones de Jakarta Validation para validar los DTOs.

Entre las validaciones implementadas:

* Campos obligatorios
* Identificadores no vacios
* Edad valida
* Valores monetarios positivos
* Estado obligatorio

## Pruebas

La solucion incluye pruebas automatizadas utilizando JUnit 5, Mockito y MockMvc.

Se prueban escenarios como:

* Creacion correcta de un cliente
* Validacion de campos obligatorios
* Logica de servicio de clientes
* Respuestas HTTP de los endpoints

Para ejecutar las pruebas del cliente:

```bash
cd cliente-service
./mvnw clean test
```

Para ejecutar las pruebas de cuentas:

```bash
cd cuenta-service
./mvnw clean test
```

## Swagger / OpenAPI

La documentacion de la API esta disponible mediante Swagger UI.

Cliente:

```text
http://localhost:8081/swagger-ui/index.html
```

Cuenta:

```text
http://localhost:8082/swagger-ui/index.html
```

## Ejecucion local

### Requisitos

* Java 21
* Maven
* PostgreSQL 16
* RabbitMQ

Cada microservicio utiliza una base de datos independiente.

Cliente:

```text
PostgreSQL
Database: cliente_db
Port: 5433
User: banco
Password: banco
```

Cuenta:

```text
PostgreSQL
Database: cuenta_db
Port: 5434
User: banco
Password: banco
```

## Ejecucion con Docker

La solucion incluye Dockerfiles para ambos microservicios y un archivo `docker-compose.yml`.

Los servicios desplegados son:

```text
cliente-service
cuenta-service
cliente-db
cuenta-db
rabbitmq
```

Para construir y ejecutar toda la solucion:

```bash
docker compose up -d --build
```

Verificar los contenedores:

```bash
docker compose ps
```

Detener la solucion:

```bash
docker compose down
```

Ver logs:

```bash
docker compose logs -f
```

Logs individuales:

```bash
docker compose logs -f cliente-service
```

```bash
docker compose logs -f cuenta-service
```

## Puertos

| Servicio            |       Puerto |
| ------------------- | -----------: |
| cliente-service     |         8081 |
| cuenta-service      |         8082 |
| RabbitMQ AMQP       |         5672 |
| RabbitMQ Management |        15672 |
| cliente PostgreSQL  | 5432 interno |
| cuenta PostgreSQL   | 5432 interno |

Las bases de datos utilizan redes internas de Docker y cada microservicio se conecta utilizando el nombre del servicio correspondiente.

## Ejemplos de uso

### Crear cliente

```http
POST /api/clientes
Content-Type: application/json
```

```json
{
  "nombre": "Jose Lema",
  "genero": "M",
  "edad": 30,
  "identificacion": "0102030405",
  "direccion": "Otavalo",
  "telefono": "0987654321",
  "clienteId": "jose",
  "contrasena": "1234",
  "estado": true
}
```

### Crear cuenta

```http
POST /api/cuentas
Content-Type: application/json
```

```json
{
  "numeroCuenta": "478758",
  "tipoCuenta": "Ahorros",
  "saldoInicial": 1000,
  "estado": true,
  "clienteId": "jose"
}
```

### Registrar deposito

```http
POST /api/movimientos
Content-Type: application/json
```

```json
{
  "numeroCuenta": "478758",
  "tipoMovimiento": "Deposito",
  "valor": 600
}
```

### Registrar retiro

```http
POST /api/movimientos
Content-Type: application/json
```

```json
{
  "numeroCuenta": "478758",
  "tipoMovimiento": "Retiro",
  "valor": 500
}
```

### Retiro sin saldo

```http
POST /api/movimientos
Content-Type: application/json
```

```json
{
  "numeroCuenta": "478758",
  "tipoMovimiento": "Retiro",
  "valor": 2000
}
```

Respuesta:

```json
{
  "mensaje": "Saldo no disponible"
}
```

## Base de datos

El proyecto incluye el archivo:

```text
BaseDatos.sql
```

Este archivo contiene la estructura necesaria para las entidades de la solucion y puede utilizarse como referencia para la creacion de las bases de datos.

La aplicacion tambien puede generar y actualizar las tablas mediante Hibernate durante el despliegue.

## Postman

Se incluye una coleccion de Postman para validar los endpoints de los microservicios.

La coleccion permite probar:

* Clientes
* Cuentas
* Movimientos
* Reportes
* Validaciones
* Errores de negocio

Los endpoints pueden ejecutarse sobre:

```text
http://localhost:8081
```

y

```text
http://localhost:8082
```

## Datos de prueba

Ejemplo de cliente:

```text
Cliente: Jose Lema
Cliente ID: jose
Identificacion: 0102030405
```

Ejemplo de cuenta:

```text
Numero: 478758
Tipo: Ahorros
Saldo inicial: 1000
Cliente: jose
```

Ejemplo de operaciones:

```text
Deposito: +600
Retiro: -500
Saldo final: 1100
```

## Consideraciones de arquitectura

Cada microservicio posee su propia base de datos para reducir el acoplamiento y permitir que cada servicio administre independientemente su modelo de persistencia.

La comunicacion asincrona mediante RabbitMQ permite desacoplar eventos entre servicios y facilita una futura extension de la solucion.

Para escenarios de mayor carga, la arquitectura puede evolucionar incorporando:

* Balanceo de carga
* Escalamiento horizontal
* Health checks
* Circuit breakers
* Reintentos de mensajes
* Dead Letter Queues
* Observabilidad y metricas
* Cache
* Gestion centralizada de configuracion

Estos mecanismos no son necesarios para el alcance funcional actual, pero forman parte de las posibilidades de evolucion de la arquitectura.

## Autor

**John Fernando Galarza Jaramillo**

Ingeniero en Tecnologias de la Informacion

GitHub: `johngalarza`
