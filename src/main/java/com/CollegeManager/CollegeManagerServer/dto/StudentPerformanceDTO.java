package com.CollegeManager.CollegeManagerServer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentPerformanceDTO {
    private Long id;
    private String name;
    private String rollNumber;
    private String year;
    private String semester;
    private double gpa;
    private double attendance;
    private String status; // e.g., "excellent", "good", "average", "poor"
}