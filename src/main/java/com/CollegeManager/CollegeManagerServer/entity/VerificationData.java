package com.CollegeManager.CollegeManagerServer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Entity
@Table(name="veirification_data")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class VerificationData {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "auto_id")
    private Long id;

    @Column(name="email")
    private String email;

    @Column(name="activationCode")
    private String activationCode;

    @Column(name="createdData")
    private LocalDateTime createdDate;

    @Column(name="expiresData")
    private LocalDateTime expiresDate;

}