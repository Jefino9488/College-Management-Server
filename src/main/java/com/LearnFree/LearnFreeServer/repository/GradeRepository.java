package com.LearnFree.LearnFreeServer.repository;

import com.LearnFree.LearnFreeServer.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);
    Boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);
}