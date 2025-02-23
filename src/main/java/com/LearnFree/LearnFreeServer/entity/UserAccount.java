package com.LearnFree.LearnFreeServer.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name="user_account")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class UserAccount {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "user_id")
    private Long id;

    @Column(name="first_name")
    private String firstName;

    @Column(name="last_name")
    private String lastName;

    @Column(name="gender")
    private String gender;

    @Column(name="date_of_birth")
    private String dateOfBirth;

    @Column(name="mobile_number")
    private String mobileNumber;

    @Column(name="personal_email")
    private String personalEmail;

    @Column(name = "registration_number")
    private String registrationNumber;

    @Column(name = "github")
    private String github;

    @Column(name = "linked_in")
    private String linkedIn;

    @Column(name = "qualifications")
    private String qualifications;

    @Column(name = "bio")
    private String bio;

    @Column(name = "academic_year")
    private Integer academicYear;

    @Column(name = "semester")
    private Integer semester;

    @Column(name = "major")
    private String major;

    @Column(name = "gpa")
    private Double gpa;

    @Column(name = "advisor")
    private String advisor;

    @Column(name = "enrollment_date")
    private LocalDate enrollmentDate;

    @Column(name = "expected_graduation")
    private LocalDate expectedGraduation;

    @ElementCollection
    @Builder.Default
    @CollectionTable(name = "user_achievements", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "achievement")
    private List<String> achievements = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    @CollectionTable(name = "user_activities", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "activity")
    private List<String> activities = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    @CollectionTable(name = "user_courses", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "course")
    private List<String> courses = new ArrayList<>();

    @ElementCollection
    @Builder.Default
    @CollectionTable(name = "user_profile_certificates", joinColumns = @JoinColumn(name = "user_id"))
    @Column(name = "certificate")
    private List<String> certificates = new ArrayList<>();

    @ManyToOne
    @JoinColumn(name = "batch_id")
    private Batch batch;

    @ManyToOne
    @JoinColumn(name = "department_id")
    private Department department;
}