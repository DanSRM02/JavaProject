package com.oxi.software.entities;

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
    @Column(name = "id_review")
    private Long id;
    @Column(name = "message", nullable = false,  length = 254)
    private String message;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations / DONE

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn (name = "fk_id_product")
    private Product product;

    @ManyToOne
    @JoinColumn(name = "fk_id_user", nullable = false)
    private User user;

}
