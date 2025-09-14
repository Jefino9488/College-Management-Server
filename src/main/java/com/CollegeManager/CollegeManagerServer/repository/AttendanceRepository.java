package com.CollegeManager.CollegeManagerServer.repository;

import com.CollegeManager.CollegeManagerServer.entity.Attendance;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;

public interface AttendanceRepository extends JpaRepository<Attendance, Long> {
    List<Attendance> findByDepartmentAndAcademicYearAndSemester(
            String department, Integer academicYear, Integer semester);

    List<Attendance> findByUserId(Long userId);

    // NEW: Find all attendance records for a list of student IDs in a single query
    List<Attendance> findByUserIdIn(List<Long> userIds);

    // NEW: Efficiently calculate average attendance for a list of students
    @Query("SELECT CASE WHEN COUNT(a) > 0 THEN (SUM(CASE WHEN a.present = true THEN 1.0 ELSE 0.0 END) / COUNT(a)) * 100.0 ELSE 100.0 END FROM Attendance a WHERE a.userId IN :studentIds")
    Double getAverageAttendanceForStudents(@Param("studentIds") List<Long> studentIds);
}