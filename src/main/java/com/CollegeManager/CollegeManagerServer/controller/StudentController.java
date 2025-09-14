package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.GradeDetailsDTO;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.service.grade.GradeService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/college-manager/student")
@RequiredArgsConstructor
public class StudentController {

    private final GradeService gradeService;

    @GetMapping("/grades")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<List<GradeDetailsDTO>> getStudentGrades(@AuthenticationPrincipal UserAuthentication user) {
        return ResponseEntity.ok(gradeService.getGradesForStudent(user.getUserId()));
    }
}
