package com.LearnFree.LearnFreeServer.dto;

import lombok.*;

import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class StudentProfileUpdateDTO extends ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String mobileNumber;
    private String department;
    private String personalEmail;
    private String github;
    private String linkedIn;
    private String bio;
    private String studentId;
    private String major;
    private Integer academicYear;
    private Integer semester;
    private Double gpa;
    private String advisor;
    private String enrollmentDate;
    private String expectedGraduation;
    private List<String> achievements;
    private List<String> activities;
    private List<String> courses;
    private List<String> certificates;
    private Long departmentId;
}