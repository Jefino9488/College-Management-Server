package com.LearnFree.LearnFreeServer.controller;

import com.LearnFree.LearnFreeServer.dto.DepartmentDTO;
import com.LearnFree.LearnFreeServer.entity.*;
import com.LearnFree.LearnFreeServer.repository.CollegeRepository;
import com.LearnFree.LearnFreeServer.repository.DepartmentRepository;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import com.LearnFree.LearnFreeServer.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/department")
@RequiredArgsConstructor
public class DepartmentController {
    private final DepartmentRepository departmentRepository;
    private final CollegeRepository collegeRepository;
    private final UserAuthenticationRepository authRepository;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/add")
    public ResponseEntity<Department> addDepartment(@RequestBody DepartmentDTO departmentDTO) {
        College college = collegeRepository.findById(departmentDTO.getCollegeId()).orElseThrow();
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
            @AuthenticationPrincipal UserAuthentication principal
    ) {
        Department department = departmentRepository.findById(departmentId).orElseThrow();
        UserAccount hod = userAccountRepository.findById(hodUserId).orElseThrow();

        UserAuthentication hodAuth = authRepository.findByUserId(hod.getId())
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        if (!hodAuth.getRole().equals(RoleEnum.HOD)) {
            throw new IllegalArgumentException("User is not an HOD");
        }

        department.setHod(hod);
        return ResponseEntity.ok(departmentRepository.save(department));
    }
}