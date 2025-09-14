package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Fee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FeeRepository extends JpaRepository<Fee, Long> {
    List<Fee> findByStudentId(Long studentId);
    Boolean existsByStudentIdAndIsPaidFalse(Long studentId);

    Optional<Double> findTotalDueAmountByStudentId(Long userId);
}