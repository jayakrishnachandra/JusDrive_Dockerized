package com.jusdrive.CarService.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "car")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Car {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer carId;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private Owner owner;

    private String brand;
    private String model;

    @Column(unique = true)
    private String registrationNumber;

    private Integer year;
    private String color;
    private String carType;
    private Integer seatingCapacity;
    private Double pricePerDay;

    @Enumerated(EnumType.STRING)
    private CarStatus status;

    @Lob
    @Column(name = "image", columnDefinition = "MEDIUMBLOB")
    private byte[] image;

    @Column(name = "created_at", updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();

    // custom constructor without image
    public Car(String brand, String model, String registrationNumber, Integer year, String color,
               String carType, Integer seatingCapacity, Double pricePerDay, CarStatus status, Owner owner) {
        this.brand = brand;
        this.model = model;
        this.registrationNumber = registrationNumber;
        this.year = year;
        this.color = color;
        this.carType = carType;
        this.seatingCapacity = seatingCapacity;
        this.pricePerDay = pricePerDay;
        this.status = status;
        this.owner = owner;
        this.createdAt = LocalDateTime.now();
        this.modifiedAt = LocalDateTime.now();
    }
}
