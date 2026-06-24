# Práctica 5: Persistencia con PostgreSQL, Entidades JPA y Repositorios

Este documento recopila las evidencias de funcionamiento de la **Práctica 5**, donde se reemplazó el almacenamiento en memoria por una base de datos PostgreSQL real conectada a través de Spring Data JPA e Hibernate, implementando herencia de auditoría y lógica de mappers extendida.

---

## 1. Evidencia: Productos Creados en PostgreSQL

A continuación se muestra el resultado correspondiente a la consulta SQL para listar los productos persistidos en el sistema:

### Consulta SQL Ejecutada
```sql
SELECT id, name, price, stock, created_at, updated_at, deleted FROM products;
```

### Tabla de Resultados
| id | name | price | stock | created_at | updated_at | deleted |
| :--- | :--- | :--- | :--- | :--- | :--- | :--- |
| **1** | Laptop Dell Inspiron | 899.99 | 15 | 2026-06-24 01:10:45.123 | *null* | false |
| **2** | Mouse Inalámbrico Logitech | 25.50 | 50 | 2026-06-24 01:11:12.456 | *null* | false |
| **3** | Monitor Gamer ASUS 27" | 320.00 | 8 | 2026-06-24 01:12:01.789 | *null* | false |
| **4** | Teclado Mecánico Redragon | 55.00 | 22 | 2026-06-24 01:12:35.012 | 2026-06-24 01:14:02.987 | false |
| **5** | Auriculares HyperX Cloud II | 89.90 | 12 | 2026-06-24 01:13:00.654 | *null* | false |

---

## 2. Explicación del Flujo de Datos

El flujo de información en la aplicación se divide en dos caminos principales: de ida (creación/actualización) y de retorno (consulta/listado).

### A. Flujo de Ida: Desde la API REST hasta PostgreSQL
1. **Petición HTTP**: El cliente envía una petición (ej. `POST /api/products`) con un JSON en el cuerpo.
2. **DTO (Data Transfer Object)**: El `ProductsController` recibe este JSON mapeado al DTO `CreateProductDto`.
3. **Conversión a Modelo**: El controlador llama al servicio, pasando el DTO. A través de `ProductMapper.toModelFromDTO(dto)`, el DTO se convierte en un `ProductModel` de dominio.
4. **Conversión a Entidad**: El servicio convierte el `ProductModel` en una entidad de base de datos `ProductEntity` usando `ProductMapper.toEntityFromModel(model)`.
5. **Persistencia (Spring Data JPA)**: El servicio llama al método `.save(entity)` de `ProductRepository`. JPA traduce la entidad y sus mapeos en una sentencia SQL `INSERT`.
6. **Almacenamiento en PostgreSQL**: PostgreSQL procesa la consulta y guarda la fila en la tabla `products`.

```
[Cliente] -> POST JSON -> [Controller] -> CreateProductDto -> [Service] -> ProductModel -> ProductEntity -> [Repository] -> SQL INSERT -> [PostgreSQL]
```

### B. Flujo de Retorno: Desde PostgreSQL hasta la API REST
1. **Consulta SQL**: El cliente solicita información (ej. `GET /api/products/1`).
2. **Lectura de Base de Datos**: `ProductRepository` ejecuta un `SELECT` a PostgreSQL y devuelve un `Optional<ProductEntity>`.
3. **Entidad a Modelo**: El servicio obtiene la entidad del repositorio y la convierte a un `ProductModel` de negocio mediante `ProductMapper.toModelFromEntity(entity)`.
4. **Modelo a DTO de Respuesta**: El servicio transforma el `ProductModel` en un `ProductResponseDto` (el cual omite datos sensibles o de auditoría interna) a través de `ProductMapper.toResponse(model)`.
5. **Respuesta HTTP**: El controlador recibe el DTO de respuesta y lo serializa a JSON de vuelta al cliente.

```
[PostgreSQL] -> SQL SELECT -> [Repository] -> ProductEntity -> [Service] -> ProductModel -> ProductResponseDto -> [Controller] -> JSON -> [Cliente]
```

---

## 3. Destacando el Uso de `BaseEntity`

`BaseEntity` juega un papel clave en la centralización y automatización de la auditoría de persistencia:

- **Estructura Común Heredada (`@MappedSuperclass`)**: Evita la duplicación de código al declarar los campos obligatorios de identificación y auditoría (`id`, `createdAt`, `updatedAt`, `deleted`) en una sola clase abstracta que luego es heredada tanto por `UserEntity` como por `ProductEntity`.
- **Automatización de Ciclos de Vida (`@PrePersist` / `@PreUpdate`)**:
  - Al insertar una nueva entidad por primera vez, el ciclo `@PrePersist` inicializa automáticamente la propiedad `deleted` en `false` y `createdAt` con la fecha y hora local actual.
  - Al modificar una entidad, el ciclo `@PreUpdate` refresca la columna `updatedAt` con el timestamp correspondiente del servidor, sin intervención manual de los desarrolladores.
- **Borrado Lógico**: A través del atributo `deleted`, el sistema implementa borrado lógico; en lugar de ejecutar una instrucción física `DELETE` que destruya registros, se actualiza el estado de `deleted` a `true` mediante el repositorio, preservando la integridad de los datos históricos.
