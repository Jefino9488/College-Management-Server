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
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@RequestMapping("/college-manager/staff") // Consolidated mapping
@RequiredArgsConstructor
public class StaffController {

    private final StaffService staffService;
    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final UserAuthenticationRepository authRepository;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/add-students")
    @PreAuthorize("hasAnyRole('STAFF', 'HOD')")
    public ResponseEntity<ResponseDTO> readOperationExcel(
            @RequestParam("students_data") MultipartFile students_data,
            @RequestParam("department") String department,
            @RequestParam("academicYear") Integer academicYear) {
        return ResponseEntity.ok(staffService.addStudents(students_data, department, academicYear));
    }

    @GetMapping("/students")
    @PreAuthorize("hasAnyRole('STAFF', 'HOD')")
    public ResponseEntity<List<StudentDTO>> getStudentsByDepartmentAndYear(
            @RequestParam String department,
            @RequestParam Integer academicYear) {
        List<StudentDTO> students = staffService.getStudentsByDepartmentAndYear(department, academicYear);
        return ResponseEntity.ok(students != null ? students : Collections.emptyList());
    }

    @PostMapping("/add-grades")
    @PreAuthorize("hasAnyRole('STAFF', 'HOD')")
    public ResponseEntity<?> addGrades(
            @RequestParam("grades_data") MultipartFile grades_data,
            @RequestParam("semester") int semester,
            @RequestParam("department") String department,
            @RequestParam("academicYear") int academicYear) {
        return ResponseEntity.ok(staffService.addGrades(grades_data, semester, department, academicYear));
    }

    @GetMapping("/college/{collegeId}")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<UserAccount>> getStaffByCollege(
            @PathVariable Long collegeId,
            @AuthenticationPrincipal UserAuthentication principal) {

        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new IllegalArgumentException("College not found"));

        if (!college.getPrincipal().getId().equals(principal.getUserId())) {
            return ResponseEntity.status(403).build();
        }

        List<UserAccount> staff = staffService.getStaffByCollege(collegeId);
        return ResponseEntity.ok(staff);
    }

    @GetMapping("/by-department/{departmentId}")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<List<UserAccount>> getStaffByDepartment(
            @PathVariable Long departmentId,
            @AuthenticationPrincipal UserAuthentication principal) {

        // Security check: ensure the HOD requesting is the HOD of this department
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));

        UserAccount hod = userAccountRepository.findById(principal.getUserId()).orElseThrow();

        if (department.getHod() == null || !department.getHod().getId().equals(hod.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        List<UserAccount> staff = staffService.getStaffByDepartment(departmentId);
        return ResponseEntity.ok(staff);
    }
}