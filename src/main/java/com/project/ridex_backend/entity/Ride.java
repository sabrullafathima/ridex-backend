package com.project.ridex_backend.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "ride")
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Getter
@Setter
public class Ride {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;
    @ManyToOne
    @JoinColumn(name = "riderId", nullable = false)
    private User rider;
    @ManyToOne
    @JoinColumn(name = "driverId", nullable = false)
    private User driver;
    private String pickup;
    private String destination;
    private String status;
}
