package ec.edu.ups.icc.fundamentos01.users.mappers;

import java.time.LocalDateTime;

import ec.edu.ups.icc.fundamentos01.users.dtos.CreateUserDto;
import ec.edu.ups.icc.fundamentos01.users.dtos.UserResponseDto;
import ec.edu.ups.icc.fundamentos01.users.models.UserModel;


    /*
 * Clase encargada de convertir objetos entre DTOs y modelos.
 *
 * En esta práctica se usa para separar los datos que llegan desde la API
 * de los datos que maneja internamente la aplicación.
 *
 * El mapper evita que el controlador copie manualmente los campos
 * entre CreateUserDto, UserModel y UserResponseDto.
 */
public class UserMapper {

       /*
     * Convierte un CreateUserDto en un UserModel.
     *
     * Se usa cuando llega una petición POST para crear un usuario.
     * El DTO contiene los datos enviados por el cliente.
     * El modelo representa el usuario dentro de la aplicación.
     *
     * En este método también se asigna createdAt porque la fecha de creación
     * debe generarla el backend y no el cliente.
     */
      public static UserModel toModel(CreateUserDto dto) {

        UserModel model = new UserModel();

        model.setName(dto.getName());
        model.setEmail(dto.getEmail());
        model.setPassword(dto.getPassword());

        model.setPasswordHash("HASH_" + dto.getPassword());
        model.setCreatedAt(LocalDateTime.now());

        return model;
    }

    /*
     * Convierte un UserModel en un UserResponseDto.
     *
     * Se usa para construir la respuesta que se devuelve al cliente.
     * El DTO de respuesta solo debe contener datos seguros.
     *
     * No se copia password ni passwordHash porque esos datos
     * no deben exponerse en la respuesta de la API.
     */
    public static UserResponseDto toResponse(UserModel model) {

        UserResponseDto response = new UserResponseDto();

        response.setId(model.getId());
        response.setName(model.getName());
        response.setEmail(model.getEmail());

        return response;
    }
}
    
