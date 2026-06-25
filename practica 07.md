# Programación y Plataformas Web

# Frameworks Backend: Spring Boot – Control Global de Errores y Excepciones

<div align="center">
  <img src="https://cdn.jsdelivr.net/gh/devicons/devicon/icons/spring/spring-original.svg" width="100" alt="Spring Boot Logo">
</div>

---
### Autores

**Cinthya Ramón**

[cramonm1@ups.edu.ec](mailto:cramonm1@ups.edu.ec)

GitHub: [CinthyLu](https://github.com/CinthyLu)

---

# Práctica 7: Manejo Global de Errores y Excepciones

Este documento recopila las evidencias de funcionamiento y el desarrollo paso a paso de la **Práctica 7**, donde se implementó un sistema unificado y centralizado de excepciones utilizando excepciones personalizadas de negocio, un DTO estándar de error y una clase controladora global `@RestControllerAdvice` para capturar cualquier fallo de la aplicación y retornarlo con el formato adecuado.

---

## 1. Paso a Paso de lo Realizado en el Proyecto

### Paso 1: Creación de la Excepción Base y de Dominio
Se estructuró el paquete `ec.edu.ups.icc.fundamentos01.core.exceptions` con las siguientes clases:
- **`ApplicationException`**: Clase abstracta base que extiende de `RuntimeException` para asociar un código de estado `HttpStatus` con cada error.
- **`NotFoundException`**: Excepción para cuando un recurso no existe o se encuentra eliminado lógicamente (`404 Not Found`).
- **`ConflictException`**: Excepción para conflictos lógicos de negocio como claves duplicadas (`409 Conflict`).
- **`BadRequestException`**: Excepción para peticiones mal formadas o reglas de negocio violadas (`400 Bad Request`).

### Paso 2: Diseño del DTO de Respuesta Única
Se actualizó [ErrorResponse.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/core/exceptions/response/ErrorResponse.java) para usar `@JsonInclude(JsonInclude.Include.NON_NULL)` de Jackson. Esto asegura que el campo `details` (que contiene errores de validación de campos) no aparezca en las respuestas de error comunes.

### Paso 3: Implementación del Manejador de Excepciones Global
En [GlobalExceptionHandler.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/core/exceptions/handler/GlobalExceptionHandler.java), se añadieron los tres manejadores principales:
- `@ExceptionHandler(ApplicationException.class)`: Para capturar excepciones de dominio personalizadas.
- `@ExceptionHandler(MethodArgumentNotValidException.class)`: Para capturar fallos sintácticos y de formato de `@Valid` en DTOs, poblando la clave `details` con cada campo fallido.
- `@ExceptionHandler(Exception.class)`: Para capturar errores inesperados del sistema y retornar un genérico `500 Internal Server Error`.

### Paso 4: Adaptación de la Capa de Servicios
- **Usuarios**: Se actualizó [UserServiceImpl.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/users/services/UserServiceImpl.java) para lanzar `NotFoundException` (cuando un usuario no existe o está en `deleted = true`) y `ConflictException` (si se intenta registrar un email duplicado).
- **Productos**: Se actualizó [ProductServiceImpl.java](file:///c:/Users/MSI/Desktop/PPW/Backend/SPRINGBOOT/fundamentos01/src/main/java/ec/edu/ups/icc/fundamentos01/products/services/ProductServiceImpl.java) para lanzar `NotFoundException` en métodos de consulta, actualización y eliminación, y `ConflictException` si se viola la regla de nombre de producto duplicado.

### Paso 5: Corrección de Consultas en Repositorios
Se corrigieron firmas de métodos en `UserRepository` para que coincidan con los atributos reales de las entidades de la base de datos (evitando fallos al arrancar Hibernate), y se agregó `findByName(String name)` en `ProductRepository`.

---

## 2. Evidencias e Informes de Pruebas (Bruno / Postman)

### A. Consulta de Producto Inexistente (`404 Not Found`)
*Prueba de búsqueda sobre un producto que no existe en el sistema.*

- **Petición HTTP:** `GET http://localhost:8080/api/products/999`
- **Respuesta JSON recibida:**
  ```json
  {
    "timestamp": "2026-06-25T10:20:00.000000",
    "status": 404,
    "error": "Not Found",
    "message": "Product not found",
    "path": "/api/products/999"
  }
  ```

> [!NOTE]
> *Captura recomendada:* Realizar un `GET` a un ID de producto que no exista y capturar la respuesta JSON de error con código `404 Not Found`.
> Guardar la captura en: `assets/practicas/07_get_product_not_found.png`

---

### B. Conflicto por Nombre de Producto Duplicado (`409 Conflict`)
*Prueba de validación de negocio: no se permite registrar un producto con un nombre que ya se encuentra activo en el sistema.*

- **Petición HTTP:** `POST http://localhost:8080/api/products`
- **Cuerpo JSON enviado:**
  ```json
  {
    "name": "Teclado Gamer Razer BlackWidow",
    "price": 89.99,
    "stock": 15
  }
  ```
- **Segunda Petición idéntica (Conflicto):**
- **Respuesta JSON recibida:**
  ```json
  {
    "timestamp": "2026-06-25T10:22:15.000000",
    "status": 409,
    "error": "Conflict",
    "message": "Product name already registered",
    "path": "/api/products"
  }
  ```

> [!NOTE]
> *Captura recomendada:* Intentar registrar el mismo producto con el mismo nombre y capturar la respuesta con el código `409 Conflict` indicando `"Product name already registered"`.
> Guardar la captura en: `assets/practicas/07_post_product_conflict.png`

---

### C. Error de Validación del DTO (`400 Bad Request`) con Detalles
*Prueba enviando valores incorrectos (vacíos o negativos) para probar la intercepción de `@Valid` y el desglose de fallos en el mapa de `details`.*

- **Petición HTTP:** `POST http://localhost:8080/api/products`
- **Cuerpo JSON enviado (Inválido):**
  ```json
  {
    "name": "",
    "price": -5.0,
    "stock": -1
  }
  ```
- **Respuesta JSON recibida:**
  ```json
  {
    "timestamp": "2026-06-25T10:25:30.000000",
    "status": 400,
    "error": "Bad Request",
    "message": "Datos de entrada inválidos",
    "path": "/api/products",
    "details": {
      "name": "El nombre es obligatorio",
      "price": "El precio mínimo es 0",
      "stock": "El stock mínimo es 0"
    }
  }
  ```

> [!NOTE]
> *Captura recomendada:* Enviar el payload inválido anterior y capturar la respuesta `400 Bad Request` donde se visualice el mapa de `details` formateado de forma amigable.
> Guardar la captura en: `assets/practicas/07_validation_details_error.png`

---

## 3. Conclusiones

La implementación de este sistema unificado aporta las siguientes ventajas:
1. **Control Total sobre el Formato:** El frontend ahora siempre recibe el mismo formato JSON, facilitando enormemente el manejo de errores en interfaces de usuario.
2. **Reducción de Código Repetitivo:** Los controladores y servicios quedan libres de bloques `try-catch` y del manejo de objetos `ResponseEntity<Error>` manuales, permitiendo enfocar los servicios puramente en la lógica del negocio.
3. **Seguridad y Limpieza:** Los errores internos del sistema ya no exponen información de depuración o de la pila técnica del backend en la respuesta del cliente, aumentando la seguridad de la API en producción.
