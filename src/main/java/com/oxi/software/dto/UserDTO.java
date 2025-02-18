package com.oxi.software.dto;

import com.oxi.software.entities.User;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String password; // Este campo no debería ser expuesto en el DTO
    private Boolean state;
    private IndividualDTO individual;
    private RolTypeDTO rolType;

    // Constructor que acepta un objeto User
    public UserDTO(User user) {
        this.id = user.getId();
        this.username = user.getUsername();
        this.password = user.getPassword(); // No es recomendable incluir la contraseña en el DTO
        this.state = user.getState();
        this.individual = user.getIndividual() != null ? new IndividualDTO(user.getIndividual()) : null;
        this.rolType = user.getRolType() != null ? new RolTypeDTO(user.getRolType()) : null;
    }
}