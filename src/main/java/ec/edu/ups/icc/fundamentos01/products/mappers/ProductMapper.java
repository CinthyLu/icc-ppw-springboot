package ec.edu.ups.icc.fundamentos01.products.mappers;

import java.time.LocalDateTime;
import ec.edu.ups.icc.fundamentos01.products.dtos.CreateProductDto;
import ec.edu.ups.icc.fundamentos01.products.dtos.ProductResponseDto;
import ec.edu.ups.icc.fundamentos01.products.entity.ProductEntity;
import ec.edu.ups.icc.fundamentos01.products.models.ProductModel;

public class ProductMapper {

    public static ProductModel toModel(CreateProductDto dto) {
        if (dto == null) {
            return null;
        }
        ProductModel model = new ProductModel();
        model.setName(dto.getName());
        model.setPrice(dto.getPrice());
        model.setStock(dto.getStock());
        model.setCreatedAt(LocalDateTime.now());
        return model;
    }

    public static ProductModel toModelFromDTO(CreateProductDto dto) {
        return toModel(dto);
    }

    public static ProductModel toModelFromEntity(ProductEntity entity) {
        if (entity == null) {
            return null;
        }
        ProductModel model = new ProductModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setPrice(entity.getPrice());
        model.setStock(entity.getStock());
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());
        return model;
    }

    public static ProductEntity toEntityFromModel(ProductModel model) {
        if (model == null) {
            return null;
        }
        ProductEntity entity = new ProductEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setPrice(model.getPrice());
        entity.setStock(model.getStock());
        return entity;
    }

    public static ProductResponseDto toResponse(ProductModel model) {
        if (model == null) {
            return null;
        }
        ProductResponseDto response = new ProductResponseDto();
        response.setId(model.getId());
        response.setName(model.getName());
        response.setPrice(model.getPrice());
        response.setStock(model.getStock());
        response.setCreatedAt(model.getCreatedAt());
        return response;
    }
}
