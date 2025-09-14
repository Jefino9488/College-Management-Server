package com.CollegeManager.CollegeManagerServer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class PrincipalDashboardStatsDTO {
    private long totalColleges;
    private long totalDepartments;
    private long totalStaff;
    private long totalStudents;
    private long totalHODs;
}
