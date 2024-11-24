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
    @Column(name = "id_product")
    private Long id;
    @Column(name = "name", nullable = false, length = 25, unique = true)
    private String name;
    @Column(name = "quantity", nullable = false)
    private Integer quantity;
    @Column(name = "state", nullable = false)
    private Boolean state;
    @Column(name = "price", nullable = false)
    private Integer price;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations / DONE

    @OneToMany(mappedBy = "product", cascade = CascadeType.REMOVE, fetch = FetchType.LAZY)
    private List<Review> review;

    @ManyToMany(mappedBy = "productList", cascade = CascadeType.REFRESH, fetch = FetchType.EAGER)
    private List<Order> orderList;

    @ManyToOne(fetch = FetchType.EAGER, cascade = CascadeType.REFRESH)
    @JoinColumn (name = "fk_id_unit", nullable = false)
    private Unit unit;
}
