package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.CollegeRegistrationDTO;
import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.entity.College;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
import com.CollegeManager.CollegeManagerServer.service.college.CollegeService;
import jakarta.mail.MessagingException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/college-manager/college")
@RequiredArgsConstructor
public class CollegeController {
    private final CollegeService collegeService;
    private final UserAuthenticationRepository authRepository;

    @PostMapping("/register")
    @PreAuthorize("hasRole('PRINCIPAL')")
    public ResponseEntity<ResponseDTO> registerCollege(
            @RequestBody CollegeRegistrationDTO dto,
            @AuthenticationPrincipal UserAuthentication principal) throws MessagingException {
        College college = collegeService.createCollege(dto, principal.getEmail());
        return ResponseEntity.ok(ResponseDTO.builder()
                .status(true)
                .message("College registered with code: " + college.getCode())
                .build());
    }

    @GetMapping("/all")
    public ResponseEntity<List<College>> getAllColleges() {
        return ResponseEntity.ok(collegeService.getAllColleges());
    }
}