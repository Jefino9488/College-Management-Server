package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDepartmentAndAcademicYearAndSemester(
            String department, Integer academicYear, Integer semester);
    List<Attendance> findByUserId(Long userId);
}