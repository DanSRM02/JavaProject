package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String password;
    private Boolean state;
    private IndividualDTO individual;
    private RolTypeDTO rolType;
}
