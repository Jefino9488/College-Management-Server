package com.LearnFree.LearnFreeServer.repository;

import com.LearnFree.LearnFreeServer.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDepartmentAndAcademicYearAndSemester(
            String department, Integer academicYear, Integer semester);
    List<Attendance> findByUserId(Long userId);
}