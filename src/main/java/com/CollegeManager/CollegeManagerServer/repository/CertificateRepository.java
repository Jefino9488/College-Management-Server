package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Certificate;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CertificateRepository extends JpaRepository<Certificate, Long> {
    List<Certificate> findByUserId(Long userId);
}