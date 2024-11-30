package com.oxi.software.dto;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class DocumentTypeDTO {

    private Long id;
    private String name;
    private String acronym;

}
