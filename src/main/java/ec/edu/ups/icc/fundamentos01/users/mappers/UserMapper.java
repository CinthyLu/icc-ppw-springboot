package ec.edu.ups.icc.fundamentos01.users.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.entity.UserEntity;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

public class UserMapper {

    public static UserModel toModel(CreateUserDto dto) {
        UserModel model = new UserModel();
        model.setName(dto.getName());
        model.setEmail(dto.getEmail());
        model.setPassword(dto.getPassword());
        model.setPasswordHash("HASH_" + dto.getPassword());
        model.setCreatedAt(LocalDateTime.now());
        return model;
    }

    public static UserModel toModelFormDTO(CreateUserDto dto) {
        return toModel(dto);
    }

    public static UserModel toModelFromDTO(CreateUserDto dto) {
        return toModel(dto);
    }

    public static UserModel toModelFromEntity(UserEntity entity) {
        if (entity == null) {
            return null;
        }
        UserModel model = new UserModel();
        model.setId(entity.getId());
        model.setName(entity.getName());
        model.setEmail(entity.getEmail());
        model.setPasswordHash(entity.getPasswordHash()); // para el hash
        model.setCreatedAt(entity.getCreatedAt());
        model.setUpdatedAt(entity.getUpdatedAt());
        model.setDeleted(entity.isDeleted());
        return model;
    }

    public static UserEntity toEntityFromModel(UserModel model) {
        if (model == null) {
            return null;
        }
        UserEntity entity = new UserEntity();
        entity.setId(model.getId());
        entity.setName(model.getName());
        entity.setEmail(model.getEmail());
        entity.setPasswordHash(model.getPasswordHash());
        return entity;
    }

    public static UserResponseDto toResponse(UserModel model) {
        if (model == null) {
            return null;
        }
        UserResponseDto response = new UserResponseDto();
        response.setId(model.getId());
        response.setName(model.getName());
        response.setEmail(model.getEmail());
        response.setPassword(model.getPasswordHash()); // para que salga el hash de la contraseña
        return response;
    }
}
