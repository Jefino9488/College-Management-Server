package com.LearnFree.LearnFreeServer.entity;

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
public class Department {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true)
    private String code; // CSE

    private String name;
    private int totalYears;
    private int semestersPerYear;
    private String description;

    @ManyToOne
    @JoinColumn(name = "college_id")
    private College college;

    @OneToOne
    @JoinColumn(name = "hod_id")
    private UserAccount hod;
}