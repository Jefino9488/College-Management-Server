package com.CollegeManager.CollegeManagerServer.controller;

import com.CollegeManager.CollegeManagerServer.dto.FeeDTO;
import com.CollegeManager.CollegeManagerServer.entity.Fee;
import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import com.CollegeManager.CollegeManagerServer.repository.FeeRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/fees")
@RequiredArgsConstructor
public class FeeController {
    private final FeeRepository feeRepository;
    private final UserAccountRepository userAccountRepository;

    @PostMapping("/pay")
    public ResponseEntity<Fee> recordPayment(@RequestBody FeeDTO feeDTO) {
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
    public ResponseEntity<Boolean> checkFeeStatus(@PathVariable Long studentId) {
        boolean isClear = !feeRepository.existsByStudentIdAndIsPaidFalse(studentId);
        return ResponseEntity.ok(isClear);
    }
}