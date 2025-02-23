package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("rol_type")
    private RolTypeDTO rolType;
}
