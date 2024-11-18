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
@Table(name = "deliveries")
public class Delivery {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_delivery")
    private Long id;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //Relations

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_id_order", referencedColumnName = "id_order", nullable = false)
    private Order order;

    @OneToOne(fetch = FetchType.LAZY, optional = false, cascade = CascadeType.ALL)
    @JoinColumn(name = "fk_id_user", referencedColumnName = "id_user", nullable = false)
    private User user;

    @OneToMany(mappedBy = "delivery", cascade = CascadeType.ALL, fetch = FetchType.EAGER)
    private List<Purchase> purchases;
}


