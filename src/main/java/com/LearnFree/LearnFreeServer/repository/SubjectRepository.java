package com.LearnFree.LearnFreeServer.repository;

import com.LearnFree.LearnFreeServer.entity.Subject;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface SubjectRepository extends JpaRepository<Subject, Long> {
    List<Subject> findByDepartmentId(Long departmentId);
}