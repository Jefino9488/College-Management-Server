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
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/college")
@RequiredArgsConstructor
public class CollegeController {
    private final CollegeService collegeService;
    private final UserAuthenticationRepository authRepository;

    @PostMapping("/register")
    public ResponseEntity<ResponseDTO> registerCollege(
            @RequestBody CollegeRegistrationDTO dto,
            @RequestParam String principalEmail) throws MessagingException {

        UserAuthentication principalAuth = authRepository.findByEmail(principalEmail);
        if (principalAuth == null) {
            return ResponseEntity.badRequest().body(ResponseDTO.builder()
                    .status(false)
                    .message("Principal email not registered")
                    .build());
        }

        College college = collegeService.createCollege(dto);
        collegeService.assignPrincipalToCollege(college, principalEmail);

        return ResponseEntity.ok(ResponseDTO.builder()
                .status(true)
                .message("College registered with code: " + college.getCode())
                .build());
    }
}