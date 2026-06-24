# Práctica 4: Controladores + Servicios + Lógica de Negocio (Spring Boot)

Este documento contiene las evidencias y resultados correspondientes al desarrollo de la **Práctica 4**, enfocado en la estructuración de la arquitectura de la aplicación moviendo toda la lógica de negocio a la capa de servicios e implementando inyección de dependencias.

---

## 1. Evidencia: `ProductServiceImpl.java` (Implementación en Memoria)

A continuación se muestra el código completo de la clase `ProductServiceImpl.java` estructurada para operar con almacenamiento en memoria utilizando una lista (`List<ProductModel>`) y generación de identificadores manual:

```java
package ec.edu.ups.icc.fundamentos01.products.services;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import org.springframework.stereotype.Service;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

@Service
public class ProductServiceImpl implements ProductService {

    private final List<ProductModel> products = new ArrayList<>();
    private final AtomicLong currentId = new AtomicLong(1);

    @Override
    public List<ProductResponseDto> findAll() {
        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    @Override
    public ProductResponseDto findOne(Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(ProductMapper::toResponse)
                .orElseThrow(() -> new IllegalStateException("Product not found"));
    }

    @Override
    public ProductResponseDto create(CreateProductDto dto) {
        ProductModel product = ProductMapper.toModel(dto);
        product.setId(currentId.getAndIncrement());
        products.add(product);
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponseDto update(Long id, UpdateProductDto dto) {
        ProductModel product = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        return ProductMapper.toResponse(product);
    }

    @Override
    public ProductResponseDto partialUpdate(Long id, PartialUpdateProductDto dto) {
        ProductModel product = products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .orElseThrow(() -> new IllegalStateException("Product not found"));

        if (dto.getName() != null) {
            product.setName(dto.getName());
        }
        if (dto.getPrice() != null) {
            product.setPrice(dto.getPrice());
        }
        if (dto.getStock() != null) {
            product.setStock(dto.getStock());
        }
        return ProductMapper.toResponse(product);
    }

    @Override
    public void delete(Long id) {
        boolean removed = products.removeIf(p -> p.getId().equals(id));
        if (!removed) {
            throw new IllegalStateException("Product not found");
        }
    }
}
```

### Características de la implementación:
- **Anotación `@Service`**: Registra la clase como un Bean en el contenedor IoC de Spring.
- **Lista en memoria**: La lista mutable `products` encapsula los datos a nivel de servicio.
- **Generación de ID**: Controlada concurrentemente por `AtomicLong`.
- **Uso de Mappers**: Separación clara entre el modelo interno (`ProductModel`) y las estructuras de transferencia de datos (`CreateProductDto` / `ProductResponseDto`).

---

## 2. Evidencia: `ProductsController.java`

El controlador REST no contiene lógica CRUD ni interactúa directamente con estructuras en memoria; delega toda la responsabilidad al servicio inyectado:

```java
package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.List;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import ec.edu.ups.icc.fundamentos01.core.dto.ErrorResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.services.ProductService;

@RestController
@RequestMapping("/products")
public class ProductsController {

    private final ProductService service;

    // Inyección por Constructor
    public ProductsController(ProductService service) {
        this.service = service;
    }

    @GetMapping
    public List<ProductResponseDto> findAll() {
        return service.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Object> findOne(@PathVariable Long id) {
        try {
            return ResponseEntity.ok(service.findOne(id));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PostMapping
    public ResponseEntity<ProductResponseDto> create(@RequestBody CreateProductDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(service.create(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<Object> update(@PathVariable Long id, @RequestBody UpdateProductDto dto) {
        try {
            return ResponseEntity.ok(service.update(id, dto));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Object> partialUpdate(@PathVariable Long id, @RequestBody PartialUpdateProductDto dto) {
        try {
            return ResponseEntity.ok(service.partialUpdate(id, dto));
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Object> delete(@PathVariable Long id) {
        try {
            service.delete(id);
            return ResponseEntity.ok(new Object() {
                public final String message = "Deleted successfully";
            });
        } catch (IllegalStateException e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(new ErrorResponseDto(e.getMessage()));
        }
    }
}
```

---

## 3. Explicación Breve: Inyección de Dependencias

### ¿Cómo se inyecta el servicio en el controlador?

En Spring Boot, la inyección de dependencias se gestiona automáticamente mediante el **contenedor de Inversión de Control (IoC)**:

1. **Registro del Bean**: La clase `ProductServiceImpl` está marcada con la anotación `@Service`. Durante el escaneo de componentes al arrancar la aplicación, Spring detecta esta anotación, instancia la clase de manera singleton y registra ese bean en su contenedor IoC.
2. **Definición del Punto de Inyección**: En `ProductsController`, declaramos una propiedad final de tipo interfaz: `private final ProductService service;` y la solicitamos como parámetro en el constructor de la clase.
3. **Resolución de la Dependencia**: Como Spring Boot 2.x/3.x autoinyecta los parámetros de los constructores automáticamente (sin necesidad de `@Autowired` explícito cuando hay un único constructor), el framework busca en su contenedor IoC un bean que sea compatible con el tipo `ProductService`. Encuentra la instancia singleton de `ProductServiceImpl` y la pasa al constructor al instanciar el controlador.
