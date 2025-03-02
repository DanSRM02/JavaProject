package com.oxi.software.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.util.*;

@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
@Table(name = "rol_types")
public class RolType {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "rol_type_id")
    private Long id;
    @Column(name = "name", nullable = false, length = 55)
    private String name;
    @Column(name = "description", length = 254)
    private String description;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "rol_permission",
            joinColumns = @JoinColumn(name = "rol_type_id"),
            inverseJoinColumns = @JoinColumn(name = "permission_id")
    )
    private Set<Permission> permissions = new HashSet<>();

    @OneToMany(mappedBy = "rolType")
    private List<User> users = new ArrayList<>();
}
