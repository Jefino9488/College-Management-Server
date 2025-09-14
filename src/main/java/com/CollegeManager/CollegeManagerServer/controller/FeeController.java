package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.FeeDTO;
import com.CollegeManager.CollegeManagerServer.entity.Fee;
import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication; // ADDED
import com.CollegeManager.CollegeManagerServer.repository.FeeRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize; // ADDED
import org.springframework.security.core.annotation.AuthenticationPrincipal; // ADDED
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/college-manager/fees")
@RequiredArgsConstructor
public class FeeController {
    private final FeeRepository feeRepository;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/pay")
    @PreAuthorize("hasRole('STUDENT')") // ADDED
    public ResponseEntity<Fee> recordPayment(@RequestBody FeeDTO feeDTO, @AuthenticationPrincipal UserAuthentication user) { // MODIFIED
        if (!user.getUserId().equals(feeDTO.getStudentId())) {
            return ResponseEntity.status(403).build(); // ADDED OWNERSHIP CHECK
        }
        UserAccount student = userAccountRepository.findById(feeDTO.getStudentId()).orElseThrow();
        Fee fee = Fee.builder()
                .student(student)
                .amount(feeDTO.getAmount())
                .paymentDate(feeDTO.getPaymentDate())
                .isPaid(true)
                .build();
        return ResponseEntity.ok(feeRepository.save(fee));
    }

    @GetMapping("/status/{studentId}")
    @PreAuthorize("hasRole('STUDENT')") // ADDED
    public ResponseEntity<Boolean> checkFeeStatus(@PathVariable Long studentId, @AuthenticationPrincipal UserAuthentication user) { // MODIFIED
        if (!user.getUserId().equals(studentId)) {
            return ResponseEntity.status(403).build(); // ADDED OWNERSHIP CHECK
        }
        boolean isClear = !feeRepository.existsByStudentIdAndIsPaidFalse(studentId);
        return ResponseEntity.ok(isClear);
    }
}