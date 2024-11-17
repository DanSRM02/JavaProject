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
@Table(name = "products")
public class Product {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    private Long id;
    @Column(name = "name", nullable = false, length = 25)
    private String name;
    @Column(name = "quantity", nullable = false, columnDefinition = "0")
    private Integer quantity;
    @Column(name = "state", nullable = false)
    private Boolean state;
    @Column(name = "price", nullable = false,  columnDefinition = "0")
    private Integer price; // Check the type of the data

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //TODO relations

    @OneToMany(targetEntity = Review.class, fetch = FetchType.LAZY)
    private List<Review> reviews;

    @ManyToOne(targetEntity = Unit.class)
    private Unit unit;
}
