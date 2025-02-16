package com.oxi.software.entities;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "individual_types")
public class IndividualType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_individual_type")
    private Long id;
    @Column(name = "name_individual", nullable = false, length = 15)
    private String name;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    @OneToMany(mappedBy = "individualType")
    private List<Individual> individuals;

}