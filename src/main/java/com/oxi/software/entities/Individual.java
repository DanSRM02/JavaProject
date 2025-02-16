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
@Table(name = "individuals")
public class Individual {
    @Id
    @GeneratedValue(strategy = GenerationType.AUTO)
    @Column(name = "id_individual")
    private Long id;
    @Column(name = "name", length = 100)
    private String name;
    @Column(name = "email", unique = true)
    private String email;
    @Column (name = "address", length = 100)
    private String address;
    @Column (name = "document", unique = true)
    private Long document;
    @Column (name = "phone", length = 20)
    private String phone;

    @CreationTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "created_at",updatable = false)
    private Date createdAt;

    @UpdateTimestamp
    @Temporal(TemporalType.TIMESTAMP)
    @Column(name = "update_at")
    private Date updatedAt;

    //relations
    @OneToOne(mappedBy = "individual")
    private User user;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_individual_type")
    private IndividualType individualType;

    @ManyToOne(cascade = CascadeType.REFRESH)
    @JoinColumn(name = "fk_id_document_type")
    private DocumentType documentType;


}
