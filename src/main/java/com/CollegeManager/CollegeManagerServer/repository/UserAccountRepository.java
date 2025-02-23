package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserAccountRepository extends JpaRepository<UserAccount, Long> {
    Optional<UserAccount> findById(Long id);

    @Query("SELECT ua FROM UserAccount ua WHERE ua.department.code = :departmentCode AND ua.academicYear = :academicYear AND EXISTS (SELECT auth FROM UserAuthentication auth WHERE auth.userId = ua.id AND auth.role = 'STUDENT')")
    List<UserAccount> findStudentsByDepartmentAndYear(String departmentCode, Integer academicYear);
    Optional<UserAccount> findByRegistrationNumber(String registrationNumber); // New method
//    Object countByRole(RoleEnum roleEnum);
}