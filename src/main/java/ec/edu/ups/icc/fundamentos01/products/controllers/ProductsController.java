package ec.edu.ups.icc.fundamentos01.products.controllers;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.mappers.ProductMapper;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

/*
 * REST controller in charge of exposing HTTP endpoints
 * for product management using functional programming.
 */
@RestController
@RequestMapping("/products")
public class ProductsController {

    private final List<ProductModel> products = new ArrayList<>();
    private final AtomicLong currentId = new AtomicLong(1);

    // GET: List all products
    @GetMapping
    public List<ProductResponseDto> findAll() {
        return products.stream()
                .map(ProductMapper::toResponse)
                .toList();
    }

    // GET: Find a single product by ID
    @GetMapping("/{id}")
    public Object findOne(@PathVariable Long id) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(product -> (Object) ProductMapper.toResponse(product))
                .orElseGet(() -> new Object() {
                    public String error = "Product not found";
                });
    }

    // POST: Create a new product
    @PostMapping
    public ProductResponseDto create(@RequestBody CreateProductDto dto) {
        ProductModel product = ProductMapper.toModel(dto);
        product.setId(currentId.getAndIncrement());
        products.add(product);
        return ProductMapper.toResponse(product);
    }

    // PUT: Replace a product completely
    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody UpdateProductDto dto) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(product -> {
                    product.setName(dto.getName());
                    product.setPrice(dto.getPrice());
                    product.setStock(dto.getStock());
                    return (Object) ProductMapper.toResponse(product);
                })
                .orElseGet(() -> new Object() {
                    public String error = "Product not found";
                });
    }

    // PATCH: Partial update a product
    @PatchMapping("/{id}")
    public Object partialUpdate(@PathVariable Long id, @RequestBody PartialUpdateProductDto dto) {
        return products.stream()
                .filter(p -> p.getId().equals(id))
                .findFirst()
                .map(product -> {
                    if (dto.getName() != null) {
                        product.setName(dto.getName());
                    }
                    if (dto.getPrice() != null) {
                        product.setPrice(dto.getPrice());
                    }
                    if (dto.getStock() != null) {
                        product.setStock(dto.getStock());
                    }
                    return (Object) ProductMapper.toResponse(product);
                })
                .orElseGet(() -> new Object() {
                    public String error = "Product not found";
                });
    }

    // DELETE: Delete a product by ID
    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        boolean exists = products.removeIf(p -> p.getId().equals(id));
        if (!exists) {
            return new Object() {
                public String error = "Product not found";
            };
        }
        return new Object() {
            public String message = "Deleted successfully";
        };
    }
}
