package com.oxi.software.entities;

import jakarta.persistence.*;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Table;
import lombok.*;
import org.hibernate.annotations.*;

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
    private Long id;
    @Column(name = "username", length = 25, unique = true)
    private String username;
    @Column(name = "password", length = 254)
    private String password;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //TODO relations

    @OneToOne(targetEntity = Individual.class)
    @JoinColumn(name = "individual_id")
    private Individual individual;

    @OneToOne(targetEntity = RolType.class)
    @JoinColumn(name = "rol_type_id")
    private RolType rolType;



    @OneToMany(targetEntity = Order.class, fetch = FetchType.LAZY)
    private List<Order> orders;

    @OneToMany(targetEntity = Review.class, fetch = FetchType.LAZY)
    private List<Review> Reviews;



}
