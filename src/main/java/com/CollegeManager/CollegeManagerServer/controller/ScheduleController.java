package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.entity.Exam;
import com.CollegeManager.CollegeManagerServer.entity.RoleEnum;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.service.exam.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college-manager/schedule")
@RequiredArgsConstructor
public class ScheduleController {
    private final ExamService examService;

    // Get exams (students and staff)
    @GetMapping
    public ResponseEntity<List<Exam>> getExams(
            @RequestParam(required = false, defaultValue = "All") String department,
            @RequestParam(required = false, defaultValue = "All") String semester
    ) {
        return ResponseEntity.ok(examService.getExams(department, semester));
    }

    // Add exam (STAFF/HOD only)
    @PostMapping
    public ResponseEntity<Exam> addExam(
            @RequestBody Exam exam,
            @AuthenticationPrincipal UserAuthentication user
    ) {
        if (!user.getRole().equals(RoleEnum.STAFF) && !user.getRole().equals(RoleEnum.HOD)) {
            return ResponseEntity.status(403).build();
        }
        return ResponseEntity.ok(examService.addExam(exam));
    }

    // Delete exam (STAFF/HOD only)
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteExam(
            @PathVariable Long id,
            @AuthenticationPrincipal UserAuthentication user
    ) {
        if (!user.getRole().equals(RoleEnum.STAFF) && !user.getRole().equals(RoleEnum.HOD)) {
            return ResponseEntity.status(403).build();
        }
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}