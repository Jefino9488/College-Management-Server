package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.StudentDTO;
import com.CollegeManager.CollegeManagerServer.entity.*;
import com.CollegeManager.CollegeManagerServer.repository.CollegeRepository;
import com.CollegeManager.CollegeManagerServer.repository.DepartmentRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
import com.CollegeManager.CollegeManagerServer.service.staff.StaffService;
import lombok.RequiredArgsConstructor;
import org.antlr.v4.runtime.tree.pattern.ParseTreePattern;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/college-manager")
@RequiredArgsConstructor

public class StaffController {

    private final StaffService staffService;
    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final UserAuthenticationRepository authRepository;
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
    @GetMapping("/staff/all")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<UserAccount>> getAllStaff(
            @RequestParam Long collegeId,
            @AuthenticationPrincipal UserAuthentication principal) {
        // Validate principal owns collegeId
        College college = collegeRepository.findById(collegeId).orElseThrow();
        if (!college.getPrincipal().getId().equals(principal.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        List<UserAuthentication> staffAuths = authRepository.findAll().stream()
                .filter(auth -> auth.getRole().equals(RoleEnum.STAFF))
                .toList();
        return ResponseEntity.ok(staffAuths.stream()
                .map(auth -> userAccountRepository.findById(auth.getUserId()).orElse(null))
                .filter(Objects::nonNull)
                .collect(Collectors.toList()));
    }
    @GetMapping("/staff/department")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<List<UserAccount>> getStaffByDepartment(
            @RequestParam String department,
            @AuthenticationPrincipal UserAuthentication principal) {
        Department dept = departmentRepository.findByHodId(principal.getUserId()).orElseThrow();
        if (!dept.getName().equals(department)) {
            return ResponseEntity.status(403).build();
        }
        List<UserAuthentication> staffAuths = authRepository.findAll().stream()
                .filter(auth -> auth.getRole().equals(RoleEnum.STAFF))
                .toList();
        return ResponseEntity.ok(staffAuths.stream()
                .map(auth -> userAccountRepository.findById(auth.getUserId()).orElse(null))
                .filter(Objects::nonNull)
                .filter(staff -> staff.getDepartment().equals(department))
                .collect(Collectors.toList()));
    }

}