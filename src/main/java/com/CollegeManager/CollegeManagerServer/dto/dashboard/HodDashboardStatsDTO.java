package com.CollegeManager.CollegeManagerServer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.util.List;

@Data
@Builder
public class HodDashboardStatsDTO {
    // --- Core Stats (already implemented) ---
    private long totalStudents;
    private long totalStaff;
    private long totalSubjects;
    private double averageAttendance;
    private double averageGPA;

    // --- EDITED: Add new optional fields for detailed view ---
    private List<RecentActivityDTO> recentActivities;
    private Long studentsAtRisk;
    private Long highPerformers;
    private Double staffSatisfaction;
    private Double courseCompletion;
}