package com.oxi.software.utilities.types;

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