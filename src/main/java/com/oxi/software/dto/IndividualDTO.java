package com.oxi.software.dto;

import com.oxi.software.entities.Individual;
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

    public IndividualDTO(Individual individual) {
    }
}
