package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.dashboard.HodDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.PrincipalDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.StaffDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.dto.dashboard.StudentDashboardStatsDTO;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.service.dashboard.DashboardService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ADDED
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/college-manager/dashboard")
@RequiredArgsConstructor
public class DashboardController {

    private final DashboardService dashboardService;

    @GetMapping("/principal-stats")
    @PreAuthorize("hasRole('PRINCIPAL')") // ADDED
    public ResponseEntity<PrincipalDashboardStatsDTO> getPrincipalStats() {
        return ResponseEntity.ok(dashboardService.getPrincipalStats());
    }

    @GetMapping("/hod-stats")
    @PreAuthorize("hasRole('HOD')") // ADDED
    public ResponseEntity<HodDashboardStatsDTO> getHodStats(@AuthenticationPrincipal UserAuthentication user) {
        return ResponseEntity.ok(dashboardService.getHodStats(user));
    }

    @GetMapping("/staff-stats")
    @PreAuthorize("hasRole('STAFF')") // ADDED
    public ResponseEntity<StaffDashboardStatsDTO> getStaffStats(@AuthenticationPrincipal UserAuthentication user) {
        return ResponseEntity.ok(dashboardService.getStaffStats(user));
    }

    @GetMapping("/student-stats")
    @PreAuthorize("hasRole('STUDENT')") // ADDED
    public ResponseEntity<StudentDashboardStatsDTO> getStudentStats(@AuthenticationPrincipal UserAuthentication user) {
        return ResponseEntity.ok(dashboardService.getStudentStats(user));
    }
}