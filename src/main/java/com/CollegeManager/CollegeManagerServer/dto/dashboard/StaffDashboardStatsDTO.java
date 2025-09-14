package com.CollegeManager.CollegeManagerServer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffDashboardStatsDTO {
    private long totalStudents;
    private double averageAttendance;
    private long upcomingExams;
    private long pendingGrades;
    private long classesToday;
}
