package com.oxi.software.entity;


import com.oxi.software.utilities.GeocodingServiceInjector;
import com.oxi.software.utilities.exception.GeocodingException;
import com.oxi.software.dto.google.GeoLocation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "individuals")
public class Individual {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "individual_id")
    private Long id;
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "email", unique = true)
    private String email;
    @Column (name = "address", length = 100)
    private String address;
    @Column (name = "document", unique = true)
    private String document;
    @Column (name = "phone", length = 20)
    private String phone;

    @ElementCollection
    @CollectionTable(name = "fixed_locations", joinColumns = @JoinColumn(name = "individual_id"))
    private List<GeoLocation> fixedLocations = new ArrayList<>();

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations
    @OneToOne(mappedBy = "individual")
    private User user;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_individual_type")
    private IndividualType individualType;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_document_type")
    private DocumentType documentType;

    @Transient
    public GeoLocation toGeoLocation() {
        if (this.address == null || this.address.isBlank()) {
            throw new GeocodingException("Dirección no proporcionada", address);
        }

        try {
            return GeocodingServiceInjector.getGeocodingService()
                    .convertAddressToCoordinates(this.address);
        } catch (GeocodingException e) {
            // loguear error y devolver última ubicación fija como fallback
            if (!fixedLocations.isEmpty()) {
                return fixedLocations.get(fixedLocations.size() - 1);
            }
            throw new GeocodingException("Error en geocodificación y sin ubicaciones de respaldo", address);
        }
    }
}
