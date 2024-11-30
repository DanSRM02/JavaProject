package com.oxi.software.dto;

import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IndividualDTO {

    private Long id;
    private String name;
    private String email;
    private String address;
    private Long document;
    private String phone;

    //Relations
    private DocumentTypeDTO documentType;
    private IndividualTypeDTO individualType;
}
