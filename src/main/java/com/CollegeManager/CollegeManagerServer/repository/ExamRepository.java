package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Exam;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDate;
import java.util.List;

public interface ExamRepository extends JpaRepository<Exam, Long> {
    List<Exam> findByDepartmentAndSemester(String department, String semester);
    List<Exam> findByDepartment(String department);
    List<Exam> findBySemester(String semester);
    long countByDateAfter(LocalDate localDate);

    // NEW: Find recent exams for a specific department for the activity feed
    List<Exam> findTop5ByDepartmentOrderByDateDesc(String department);
}