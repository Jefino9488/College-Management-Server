package com.LearnFree.LearnFreeServer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Entity
@Table(name = "exams")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class Exam {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String examName;

    @Column(nullable = false)
    private String department;

    @Column(nullable = false)
    private LocalDate date;

    @Column(nullable = false)
    private String time;

    @Column(nullable = false)
    private String session; // "FN" or "AN"

    @Column(nullable = false)
    private String type; // "Offline" or "Online"

    @Column(nullable = false)
    private String semester; // "1st", "2nd", etc.

    @ManyToOne
    @JoinColumn(name = "subject_id")
    private Subject subject;
}