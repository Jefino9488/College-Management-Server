package com.CollegeManager.CollegeManagerServer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StudentDashboardStatsDTO {
    private double currentGPA;
    private double overallAttendance;
    private long upcomingExams;
    private double pendingFees;
    private long completedCredits;
}
