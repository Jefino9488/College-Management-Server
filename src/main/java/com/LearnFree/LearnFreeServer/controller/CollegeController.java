package com.LearnFree.LearnFreeServer.controller;

import com.LearnFree.LearnFreeServer.dto.CollegeRegistrationDTO;
import com.LearnFree.LearnFreeServer.dto.ResponseDTO;
import com.LearnFree.LearnFreeServer.entity.College;
import com.LearnFree.LearnFreeServer.entity.UserAuthentication;
import com.LearnFree.LearnFreeServer.repository.UserAuthenticationRepository;
import com.LearnFree.LearnFreeServer.service.college.CollegeService;
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