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
@Table(name = "units")
public class Unit {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_unit")
    private Long id;
    @Column(name = "unit_type", unique = true, nullable = false, length = 52)
    private String unitType;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations / DONE

    @OneToMany(mappedBy = "unit", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Product> product;
}

