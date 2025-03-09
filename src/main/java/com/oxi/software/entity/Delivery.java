package com.oxi.software.entity;

import com.oxi.software.utilities.types.GeoLocation;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;
import java.util.ArrayList;
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
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "delivery_id")
    private Long id;

    @Column(name = "delivery_state", length = 20)
    private String state;

    @Column(name = "start_time")
    private LocalDateTime startTime;

    //Google Maps
    @Embedded
    private GeoLocation destination;

    @Column(columnDefinition = "TEXT")
    private String optimizedRoute;


    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at")
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "updated_at")
    private Date updatedAt;

    // Relación con Order (N Deliveries → 1 Order)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_order", nullable = false)
    private Order order;

    // Relación con Domiciliary (N Deliveries → 1 User)
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "fk_id_domiciliary")
    private User domiciliary;

    // Relación con Notification (1 Deliveries -> N Notification)
    @OneToMany(mappedBy = "delivery")
    private List<Notification> notifications = new ArrayList<>();
}


