package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.VerificationData;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface VerificationDataRepository extends JpaRepository<VerificationData,Long> {

    boolean existsByEmail(String email);

    boolean existsByActivationCode(String activationCode);

    VerificationData findByActivationCode(String activationCode);

}