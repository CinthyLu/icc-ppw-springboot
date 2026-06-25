
# Programación y Plataformas Web

# Frameworks Backend: Spring Boot – API REST y CRUD Inicial sin Servicios

<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="100" alt="Spring Boot Logo">
</div>

---
### Autores

**Cinthya Ramón**

[cramonm1@ups.edu.ec](mailto:cramonm1@ups.edu.ec)

GitHub: [CinthyLu](https://github.com/CinthyLu)

---

# Práctica 6: Validación de DTOs, Control de Datos de Entrada y Reglas de Negocio

Este documento recopila las evidencias de funcionamiento y el desarrollo paso a paso de la **Práctica 6**, donde se introdujo la validación de DTOs utilizando Jakarta Validation, se refactorizó el flujo de transformación de datos en el módulo de productos hacia un modelo de dominio con métodos de conversión (eliminando mappers tradicionales), y se agregaron validaciones de reglas de negocio en la capa de servicios.

---

## 1. Paso a Paso de lo Realizado en el Proyecto

### Paso 1: Configuración de Dependencias
Se verificó que la dependencia necesaria para Jakarta Validation esté correctamente declarada en el archivo `build.gradle`:
```gradle
implementation 'org.springframework.boot:spring-boot-starter-validation'
```

### Paso 2: Validación de DTOs en el Módulo de Usuarios
- Se añadieron anotaciones de validación a [PartialUpdateUserDto.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/users/dtos/PartialUpdateUserDto.java):
  - `@Size(min = 3, max = 150)` para el campo `name`.
  - `@Email` y `@Size(max = 150)` para el campo `email`.
- Se activó la validación en [UsersController.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/users/controllers/UsersController.java) agregando `@Valid` al endpoint de actualización parcial (`PATCH`).

### Paso 3: Validación de Reglas de Negocio en Usuarios
- En [UserServiceImpl.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/users/services/UserServiceImpl.java), se agregó un control de negocio antes de guardar un nuevo usuario para verificar que el email no esté duplicado:
  ```java
  if (userRepository.findByEmail(dto.getEmail()).isPresent()) {
      throw new IllegalStateException("Email already registered");
  }
  ```

### Paso 4: Creación del Modelo de Dominio de Productos
- Se eliminaron las clases obsoletas `ProductModel.java` y `ProductMapper.java`.
- Se creó una nueva clase de dominio unificada [Product.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/products/models/Product.java) que gestiona su propio estado y transformaciones:
  - Métodos de construcción: `Product.fromDto()` y `Product.fromEntity()`.
  - Métodos de conversión: `toEntity()` y `toResponseDto()`.
  - Lógica de actualización: `update()` y `partialUpdate()`.

### Paso 5: Validación de DTOs en el Módulo de Productos
- Se aplicaron anotaciones de validación a `CreateProductDto`, `UpdateProductDto` y `PartialUpdateProductDto`:
  - `name`: obligatorio, tamaño entre 3 y 150 caracteres.
  - `price`: obligatorio (salvo en parcial), mínimo 0.
  - `stock`: obligatorio (salvo en parcial), mínimo 0.
- Se activó `@Valid` en los métodos de creación, actualización completa y parcial en [ProductsController.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/products/controllers/ProductsController.java).

### Paso 6: Reglas de Negocio en Productos
- En [ProductServiceImpl.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/products/services/ProductServiceImpl.java), se implementó el control de borrado lógico y las reglas solicitadas:
  - No permitir actualizar productos eliminados.
  - No permitir eliminar un producto que ya ha sido eliminado.
  - Ocultar los productos eliminados lógicamente de las consultas `findAll` y `findOne`.

---

## 2. Evidencias de Funcionamiento (Pruebas con Postman / Bruno)

### A. Creación de Producto Inválido (Validación DTO)
Al enviar un cuerpo con valores incorrectos como un nombre vacío, un precio negativo o un stock inválido, la petición es interceptada automáticamente por Spring Boot, retornando un error `400 Bad Request`.

- **Endpoint:** `POST /api/products`
- **Cuerpo HTTP enviado (Inválido):**
  ```json
  {
    "name": "",
    "price": -5.0,
    "stock": -1
  }
  ```
- **Respuesta Esperada (`400 Bad Request`):**
  ```json
  {
    "timestamp": "2026-06-25T01:10:00.000Z",
    "status": 400,
    "error": "Bad Request",
    "path": "/products"
  }
  ```

> [!NOTE]
> *Captura de pantalla recomendada:* Enviar la petición inválida anterior en Postman y capturar la respuesta `400 Bad Request`.
> Guardar la captura en: `assets/practicas/06_post_invalid_product.png`

---

### B. Creación de Producto Válido
- **Endpoint:** `POST /api/products`
- **Cuerpo HTTP enviado:**
  ```json
  {
    "name": "Teclado Gamer Razer",
    "price": 79.99,
    "stock": 10
  }
  ```
- **Respuesta Recibida (`201 Created`):**
  ```json
  {
    "id": 1,
    "name": "Teclado Gamer Razer",
    "price": 79.99,
    "stock": 10,
    "createdAt": "2026-06-25T01:12:00.000Z"
  }
  ```

---

### C. Error al Duplicar Email (Usuarios)
- **Endpoint:** `POST /api/users`
- **Cuerpo HTTP enviado:**
  ```json
  {
    "name": "Juan Perez",
    "email": "juan@ups.edu.ec",
    "password": "miSuperPassword123"
  }
  ```
- **Primera Petición:** Retorna `201 Created` con éxito.
- **Segunda Petición (Mismo email):** Retorna `404 Not Found` (o `500 Internal Server Error` según el manejo local actual), con el mensaje `"Email already registered"`.
  ```json
  {
    "message": "Email already registered"
  }
  ```

---

### D. Regla de Negocio: No actualizar ni volver a eliminar productos eliminados
Si se elimina el producto con `id = 1` y se intenta realizar alguna modificación o eliminación posterior sobre él:

1. **Eliminación lógica exitosa:** `DELETE /api/products/1` -> `200 OK` `"Deleted successfully"`.
2. **Intento de actualización:** `PUT /api/products/1`
   - **Respuesta Recibida (`404 Not Found`):**
     ```json
     {
       "message": "Product already deleted"
     }
     ```
3. **Intento de re-eliminación:** `DELETE /api/products/1`
   - **Respuesta Recibida (`404 Not Found`):**
     ```json
     {
       "message": "Product already deleted"
     }
     ```

> [!NOTE]
> *Captura de pantalla recomendada:* Enviar el `PUT` o `DELETE` al producto ya eliminado y capturar la respuesta de error de negocio.
> Guardar la captura en: `assets/practicas/06_already_deleted_error.png`

---

### E. Consulta general (findAll) excluye eliminados
Al realizar un `GET /api/products`, los productos que tengan el estado de borrado lógico en `true` no figuran en el listado.

- **Endpoint:** `GET /api/products`
- **Respuesta Recibida (Excluye producto 1 que fue eliminado):**
  ```json
  [
    {
      "id": 2,
      "name": "Mouse Inalámbrico Logitech",
      "price": 25.50,
      "stock": 50,
      "createdAt": "2026-06-25T01:14:00.000Z"
    }
  ]
  ```

---

## 3. Conclusiones y Resumen del Flujo de Datos

Tras la aplicación de esta práctica:
1. **Flujo de Entrada Seguro:** Jakarta Validation bloquea los datos corruptos o fuera de formato en la frontera (`Controller`), evitando consumir recursos del negocio de forma innecesaria.
2. **Dominio Autogestionado:** Al trasladar el mapeo y la actualización dentro del modelo `Product`, se aplica el principio de encapsulamiento, permitiendo que la capa de servicio invoque directamente los comportamientos de negocio (`product.update(dto)`) en lugar de delegar todo el estado a mappers anémicos.
3. **Consistencia de Negocio:** La capa de servicios valida el estado lógico de los recursos en base de datos (`deleted`) antes de delegar cualquier operación de persistencia adicional.
