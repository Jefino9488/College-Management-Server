package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.DepartmentDTO;
import com.CollegeManager.CollegeManagerServer.entity.*;
import com.CollegeManager.CollegeManagerServer.repository.CollegeRepository;
import com.CollegeManager.CollegeManagerServer.repository.DepartmentRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("college-manager/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final UserAuthenticationRepository authRepository;
    private final UserAccountRepository userAccountRepository;

    @GetMapping
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<List<Department>> getDepartmentsByCollege(
            @RequestParam Long collegeId,
            @AuthenticationPrincipal UserAuthentication principal) {
        UserAccount principalAccount = userAccountRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Principal account not found"));
        College college = collegeRepository.findById(collegeId)
                .orElseThrow(() -> new IllegalArgumentException("College not found"));

        if (!college.getPrincipal().getId().equals(principalAccount.getId())) {
            return ResponseEntity.status(403).build();
        }

        List<Department> departments = departmentRepository.findByCollegeId(collegeId);
        return ResponseEntity.ok(departments);
    }

    @PostMapping("/add")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Department> addDepartment(
            @RequestBody DepartmentDTO departmentDTO,
            @AuthenticationPrincipal UserAuthentication principal) {
        College college = collegeRepository.findById(departmentDTO.getCollegeId())
                .orElseThrow(() -> new IllegalArgumentException("College not found"));

        // Verify principal's authority over this college
        UserAccount principalAccount = userAccountRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Principal account not found"));
        if (!college.getPrincipal().getId().equals(principalAccount.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        // Check if department code already exists
        if (departmentRepository.findByCode(departmentDTO.getCode()).isPresent()) {
            throw new IllegalArgumentException("Department code already exists");
        }

        Department department = Department.builder()
                .code(departmentDTO.getCode())
                .name(departmentDTO.getName())
                .description(departmentDTO.getDescription())
                .totalYears(departmentDTO.getTotalYears())
                .semestersPerYear(departmentDTO.getSemestersPerYear())
                .college(college)
                .build();

        return ResponseEntity.ok(departmentRepository.save(department));
    }

    @PostMapping("/{departmentId}/assign-hod")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<Department> assignHodToDepartment(
            @PathVariable Long departmentId,
            @RequestParam Long hodUserId,
            @AuthenticationPrincipal UserAuthentication principal) {
        Department department = departmentRepository.findById(departmentId)
                .orElseThrow(() -> new IllegalArgumentException("Department not found"));
        UserAccount hod = userAccountRepository.findById(hodUserId)
                .orElseThrow(() -> new IllegalArgumentException("HOD account not found"));

        UserAccount principalAccount = userAccountRepository.findById(principal.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Principal account not found"));
        if (!department.getCollege().getPrincipal().getId().equals(principalAccount.getId())) {
            return ResponseEntity.status(403).build(); // Forbidden
        }

        UserAuthentication hodAuth = authRepository.findByUserId(hod.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!hodAuth.getRole().equals(RoleEnum.HOD)) {
            throw new IllegalArgumentException("User is not an HOD");
        }

        department.setHod(hod);
        return ResponseEntity.ok(departmentRepository.save(department));
    }
}