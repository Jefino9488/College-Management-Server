package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Grade;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface GradeRepository extends JpaRepository<Grade, Long> {
    List<Grade> findByStudentId(Long studentId);

    // NEW: Find all grades for a list of student IDs in a single query
    List<Grade> findByStudentIdIn(List<Long> studentIds);

    Boolean existsByStudentIdAndSubjectId(Long studentId, Long subjectId);

    // NEW: Efficiently calculates the average GPA across multiple students
    @Query("SELECT CASE WHEN SUM(s.credits) > 0 THEN SUM(CASE g.grade " +
            "WHEN 'A' THEN 4.0 WHEN 'A+' THEN 4.0 " +
            "WHEN 'A-' THEN 3.7 " +
            "WHEN 'B+' THEN 3.3 " +
            "WHEN 'B' THEN 3.0 " +
            "WHEN 'B-' THEN 2.7 " +
            "WHEN 'C+' THEN 2.3 " +
            "WHEN 'C' THEN 2.0 " +
            "WHEN 'D' THEN 1.0 " +
            "ELSE 0.0 END * s.credits) / SUM(s.credits) ELSE 0.0 END " +
            "FROM Grade g JOIN g.subject s WHERE g.student.id IN :studentIds")
    Double getAverageGpaForStudents(@Param("studentIds") List<Long> studentIds);
}