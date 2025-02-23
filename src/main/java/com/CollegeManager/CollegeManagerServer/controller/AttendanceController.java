
package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.AttendanceRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.entity.Attendance;
import com.CollegeManager.CollegeManagerServer.service.attendance.AttendanceService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college-manager/attendance")
@RequiredArgsConstructor
public class AttendanceController {
    private final AttendanceService attendanceService;

    @PostMapping("/submit")
    public ResponseEntity<ResponseDTO> submitAttendance(@RequestBody AttendanceRequestDTO request) {
        attendanceService.submitAttendance(request);
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(true)
                .message("Attendance submitted successfully")
                .build());
    }

    @GetMapping("/history")
    public ResponseEntity<List<Attendance>> getAttendanceHistory(
            @RequestParam String department,
            @RequestParam Integer academicYear,
            @RequestParam Integer semester
    ) {
        return ResponseEntity.ok(attendanceService.getAttendanceHistory(department, academicYear, semester));
    }
}