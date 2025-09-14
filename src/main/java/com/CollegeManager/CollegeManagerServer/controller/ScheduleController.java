package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.entity.Exam;
import com.CollegeManager.CollegeManagerServer.service.exam.ExamService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ADDED
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
    @PreAuthorize("hasAnyRole('STAFF', 'HOD')") // ADDED
    public ResponseEntity<Exam> addExam(@RequestBody Exam exam) { // MODIFIED
        return ResponseEntity.ok(examService.addExam(exam));
    }

    // Update exam (STAFF/HOD only)
    @PutMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'HOD')") // ADDED
    public ResponseEntity<Exam> updateExam(
            @PathVariable Long id,
            @RequestBody Exam exam
    ) { // MODIFIED
        return ResponseEntity.ok(examService.updateExam(id, exam));
    }

    // Delete exam (STAFF/HOD only)
    @DeleteMapping("/{id}")
    @PreAuthorize("hasAnyRole('STAFF', 'HOD')") // ADDED
    public ResponseEntity<Void> deleteExam(@PathVariable Long id) { // MODIFIED
        examService.deleteExam(id);
        return ResponseEntity.noContent().build();
    }
}