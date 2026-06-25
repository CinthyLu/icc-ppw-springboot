package ec.edu.ups.icc.fundamentos01.products.models;

import java.time.LocalDateTime;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.PartialUpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.UpdateProductDto;
import ec.edu.ups.icc.fundamentos01.products.entity.ProductEntity;

public class Product {

    private Long id;
    private String name;
    private Double price;
    private Integer stock;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
    private boolean deleted;

    public Product() {
    }

    public Product(Long id, String name, Double price, Integer stock, LocalDateTime createdAt, LocalDateTime updatedAt, boolean deleted) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.stock = stock;
        this.createdAt = createdAt;
        this.updatedAt = updatedAt;
        this.deleted = deleted;
    }

    // Factory methods
    public static Product fromDto(CreateProductDto dto) {
        if (dto == null) {
            return null;
        }
        Product product = new Product();
        product.setName(dto.getName());
        product.setPrice(dto.getPrice());
        product.setStock(dto.getStock());
        product.setCreatedAt(LocalDateTime.now());
        product.setDeleted(false);
        return product;
    }

    public static Product fromEntity(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        return new Product(
            entity.getId(),
            entity.getName(),
            entity.getPrice(),
            entity.getStock(),
            entity.getCreatedAt(),
            entity.getUpdatedAt(),
            entity.isDeleted()
        );
    }

    // Conversion methods
    public ProductEntity toEntity() {
        ProductEntity entity = new ProductEntity();
        if (this.id != null && this.id > 0) {
            entity.setId(this.id);
        }
        entity.setName(this.name);
        entity.setPrice(this.price);
        entity.setStock(this.stock);
        entity.setCreatedAt(this.createdAt);
        entity.setUpdatedAt(this.updatedAt);
        entity.setDeleted(this.deleted);
        return entity;
    }

    public ProductResponseDto toResponseDto() {
        ProductResponseDto response = new ProductResponseDto();
        response.setId(this.id);
        response.setName(this.name);
        response.setPrice(this.price);
        response.setStock(this.stock);
        response.setCreatedAt(this.createdAt);
        return response;
    }

    // Update operations
    public void update(UpdateProductDto dto) {
        if (dto != null) {
            this.name = dto.getName();
            this.price = dto.getPrice();
            this.stock = dto.getStock();
            this.updatedAt = LocalDateTime.now();
        }
    }

    public void partialUpdate(PartialUpdateProductDto dto) {
        if (dto != null) {
            if (dto.getName() != null) {
                this.name = dto.getName();
            }
            if (dto.getPrice() != null) {
                this.price = dto.getPrice();
            }
            if (dto.getStock() != null) {
                this.stock = dto.getStock();
            }
            this.updatedAt = LocalDateTime.now();
        }
    }

    // Getters and setters
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public Integer getStock() {
        return stock;
    }

    public void setStock(Integer stock) {
        this.stock = stock;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }

    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }

    public boolean isDeleted() {
        return deleted;
    }

    public void setDeleted(boolean deleted) {
        this.deleted = deleted;
    }
}
