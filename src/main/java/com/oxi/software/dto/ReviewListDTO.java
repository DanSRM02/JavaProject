package com.oxi.software.dto;

import lombok.*;

import java.io.Serializable;
import java.util.Date;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReviewListDTO implements Serializable {
    private Long id;
    private String title;
    private String description;
    private Date createdAt;
    private int rating;
    private UserSummaryDTO user;
}
