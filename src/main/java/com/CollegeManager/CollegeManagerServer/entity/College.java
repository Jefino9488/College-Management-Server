package com.CollegeManager.CollegeManagerServer.entity;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class College {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code; // CIT

    private String name;
    private String address;
    private String contactEmail;
    private String phoneNumber;

    @OneToOne
    @JoinColumn(name = "principal_id")
    @JsonManagedReference
    private UserAccount principal;
}