package com.oxi.software.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

// Notification.java
@Setter
@Getter
@Entity
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Notification {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "notification_id")
    private Long id;
    private String type; // EMAIL, SMS, PUSH
    private String recipient;
    private String content;
    private LocalDateTime sentAt;
    private Boolean success;

    @ManyToOne
    @JoinColumn(name = "fk_id_delivery")
    private Delivery delivery;
}
