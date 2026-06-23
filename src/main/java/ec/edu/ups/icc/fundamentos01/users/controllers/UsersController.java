package ec.edu.ups.icc.fundamentos01.users.controllers;

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

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.PartialUpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UpdateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.mappers.UserMapper;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;

/*
 * REST controller encargado de exponer los puntos finales HTTP
 * para la gestión de usuarios mediante programación funcional.
 */
@RestController


@RequestMapping("/users")
public class UsersController {

    private final List<UserModel> users = new ArrayList<>();
    private final AtomicLong currentId = new AtomicLong(1);

//GET LISTA DE USUARIOS

    @GetMapping
    public List<UserResponseDto> findAll() {
        return users.stream()
                .map(UserMapper::toResponse)
                .toList();
    }
//GET OBTENER USUARIO POR ID 
    @GetMapping("/{id}")
    public Object findOne(@PathVariable Long id) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(user -> (Object) UserMapper.toResponse(user))
                .orElseGet(() -> new Object() {
                    public String error = "User not found";
                });
    }
// CORRRECION DEL POST API/USERS PARA QUE GENERE AUTOMATICAMENTE EL ID 
// ID unico para cada ususario creado 
    @PostMapping
    public UserResponseDto create(@RequestBody CreateUserDto dto) {
        UserModel user = UserMapper.toModel(dto);
        user.setId(currentId.getAndIncrement());
        users.add(user);
        return UserMapper.toResponse(user);
    }

    @PutMapping("/{id}")
    public Object update(@PathVariable Long id, @RequestBody UpdateUserDto dto) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(user -> {
                    user.setName(dto.getName());
                    user.setEmail(dto.getEmail());
                    return (Object) UserMapper.toResponse(user);
                })
                .orElseGet(() -> new Object() {
                    public String error = "UserModel not found";
                });
    }

    @PatchMapping("/{id}")
    public Object partialUpdate(@PathVariable Long id, @RequestBody PartialUpdateUserDto dto) {
        return users.stream()
                .filter(u -> u.getId().equals(id))
                .findFirst()
                .map(user -> {
                    if (dto.getName() != null) {
                        user.setName(dto.getName());
                    }
                    if (dto.getEmail() != null) {
                        user.setEmail(dto.getEmail());
                    }
                    return (Object) UserMapper.toResponse(user);
                })
                .orElseGet(() -> new Object() {
                    public String error = "UserModel not found";
                });
    }

    @DeleteMapping("/{id}")
    public Object delete(@PathVariable Long id) {
        boolean exists = users.removeIf(u -> u.getId().equals(id));
        if (!exists) {
            return new Object() {
                public String error = "User not found";
            };
        }
        return new Object() {
            public String message = "Deleted successfully";
        };
    }
}