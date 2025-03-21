package com.oxi.software.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.Date;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "reviews")
public class Review {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "review_id")
    private Long id;
    @Column(name = "title", nullable = false,  length = 254)
    private String title;
    @Column(name = "description", nullable = false,  length = 254)
    private String description;
    @Column(name = "rating", nullable = false, length = 1)
    private int rating;
    @Column(name = "state", nullable = false, columnDefinition = "boolean default true")
    private Boolean state;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn (name = "fk_id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "fk_id_user", nullable = false)
    private User user;

}
