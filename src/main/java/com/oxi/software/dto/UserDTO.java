package com.oxi.software.dto;

import lombok.*;
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
