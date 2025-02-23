package com.oxi.software.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class OrderSummaryDTO   {

    private Long id;
    private String state;
    private Double total;
    @JsonProperty("individual_name")
    private String userIndividualName;
    private String email;
    private String address;
    private String createdAt;

}