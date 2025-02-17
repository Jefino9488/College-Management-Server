package com.LearnFree.LearnFreeServer.controller;

import com.LearnFree.LearnFreeServer.dto.RegistrationRequestDTO;
import com.LearnFree.LearnFreeServer.dto.SubjectDTO;
import com.LearnFree.LearnFreeServer.entity.Department;
import com.LearnFree.LearnFreeServer.entity.Subject;
import com.LearnFree.LearnFreeServer.entity.UserAccount;
import com.LearnFree.LearnFreeServer.entity.UserAuthentication;
import com.LearnFree.LearnFreeServer.repository.DepartmentRepository;
import com.LearnFree.LearnFreeServer.repository.SubjectRepository;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/api/hod")
@RequiredArgsConstructor
public class HodController {
    private final UserAccountRepository userAccountRepository;
    private final DepartmentRepository departmentRepository;
    private final SubjectRepository subjectRepository;

    @PostMapping("/add-subject")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<Subject> addSubject(
            @RequestBody SubjectDTO dto,
            @AuthenticationPrincipal UserAuthentication user
    ) {
        UserAccount hod = userAccountRepository.findById(user.getUserId()).orElseThrow();
        Department department = departmentRepository.findByHodId(hod.getId())
                .orElseThrow(() -> new IllegalArgumentException("HOD not assigned to any department"));

        Subject subject = Subject.builder()
                .code(dto.getCode())
                .name(dto.getName())
                .semester(dto.getSemester())
                .year(dto.getYear())
                .credits(dto.getCredits())
                .department(department)
                .build();

        return ResponseEntity.ok(subjectRepository.save(subject));
    }

    @PostMapping("/add-teacher")
    @PreAuthorize("hasRole('HOD')")
    public ResponseEntity<UserAccount> addTeacher(@RequestBody RegistrationRequestDTO dto,
                                                  @AuthenticationPrincipal UserAuthentication user) {
        // Implementation similar to student registration but with STAFF role
        return null;
    }
}