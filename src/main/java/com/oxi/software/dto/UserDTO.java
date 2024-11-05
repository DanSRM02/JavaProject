package com.oxi.software.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class UserDTO {

    private Long id;
    private String username;
    private String password;

    private Date createdAt;
    private Date updatedAt;

    //TODO add relations

}
