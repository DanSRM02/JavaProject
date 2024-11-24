package com.oxi.software.entities;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;
import org.springframework.boot.autoconfigure.amqp.RabbitConnectionDetails;

import java.util.Date;
import java.util.List;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_user")
    private Long id;
    @Column(name = "username", length = 25, unique = true)
    private String username;
    @Column(name = "password", length = 254)
    private String password;
    @Column(name = "state")
    private Boolean state;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at", updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations / DONE

    @OneToOne(mappedBy = "user")
    private Delivery delivery;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_individual")
    private Individual individual;

    @OneToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_rol_type")
    private RolType rolType;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Order> order;

    @OneToMany(mappedBy = "user", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Review> reviews;

}
