package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.StudentDTO;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.service.staff.StaffService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;

@RestController
@RequestMapping("/college-manager")
@RequiredArgsConstructor

public class StaffController {

    private final StaffService staffService;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/staff/add-students")
    public ResponseEntity<ResponseDTO> readOperationExcel(
            @RequestParam("students_data") MultipartFile students_data,
            @RequestParam("department") String department,
            @RequestParam("academicYear") Integer academicYear) {
        return ResponseEntity.ok(staffService.addStudents(students_data, department, academicYear));
    }

    @GetMapping("/staff/students")
    public ResponseEntity<List<StudentDTO>> getStudentsByDepartmentAndYear(
            @RequestParam String department,
            @RequestParam Integer academicYear) {
        List<StudentDTO> students = staffService.getStudentsByDepartmentAndYear(department, academicYear);
        return ResponseEntity.ok(students != null ? students : Collections.emptyList());
    }

    @PostMapping("/staff/add-grades")
    public ResponseEntity<?> addGrades(
            @RequestParam("grades_data") MultipartFile grades_data,
            @RequestParam("semester") int semester,
            @RequestParam("department") String department,
            @RequestParam("academicYear") int academicYear) {
        return ResponseEntity.ok(staffService.addGrades(grades_data, semester, department, academicYear));
    }
}