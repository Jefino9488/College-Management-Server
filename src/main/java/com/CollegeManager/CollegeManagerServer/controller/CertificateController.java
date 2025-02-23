package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.entity.Certificate;
import com.CollegeManager.CollegeManagerServer.repository.CertificateRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping("/college-manager/certificates")
@RequiredArgsConstructor
public class CertificateController {
    private final CertificateRepository certificateRepository;

    @GetMapping
    public ResponseEntity<List<Certificate>> getCertificates(@RequestParam Long userId) {
        return ResponseEntity.ok(certificateRepository.findByUserId(userId));
    }
}