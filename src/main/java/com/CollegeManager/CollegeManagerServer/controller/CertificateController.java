package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.entity.Certificate;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication; // ADDED
import com.CollegeManager.CollegeManagerServer.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ADDED
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ADDED
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import java.util.List;

@RestController
@RequestMapping("/college-manager/certificates")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateRepository certificateRepository;

    @GetMapping
    @PreAuthorize("hasRole('STUDENT')") // ADDED
    public ResponseEntity<List<Certificate>> getCertificates(@AuthenticationPrincipal UserAuthentication user) {
        return ResponseEntity.ok(certificateRepository.findByUserId(user.getUserId()));
    }
}