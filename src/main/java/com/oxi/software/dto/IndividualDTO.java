package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
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
    @JsonProperty("document_type")
    private DocumentTypeDTO documentType;
    @JsonProperty("individual_type")
    private IndividualTypeDTO individualType;
}
