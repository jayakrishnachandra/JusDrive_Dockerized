package com.jusdrive.EnquiryService.entity;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "enquiry_response")
@Data
@NoArgsConstructor
@AllArgsConstructor

public class EnquiryResponse {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "response_id")
    private int responseId;

    @OneToOne
    @JoinColumn(name = "enquiry_id", referencedColumnName = "enquiry_id", unique = true)
    private Enquiry enquiry;

    @Column(nullable = false, columnDefinition = "TEXT")
    private String response;

    @Column(name = "responded_at")
    private LocalDateTime respondedAt = LocalDateTime.now();

    @Column(name = "modified_at")
    private LocalDateTime modifiedAt = LocalDateTime.now();
}
