package com.oxi.software.dto.google;

import jakarta.persistence.Embeddable;
import lombok.*;

@Embeddable
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class GeoLocation {
    private Double latitude;
    private Double longitude;
}