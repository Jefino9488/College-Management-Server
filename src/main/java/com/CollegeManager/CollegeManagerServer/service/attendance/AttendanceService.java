package com.CollegeManager.CollegeManagerServer.service.attendance;

import com.CollegeManager.CollegeManagerServer.dto.AttendanceRequestDTO;
import com.CollegeManager.CollegeManagerServer.entity.Attendance;
import com.CollegeManager.CollegeManagerServer.repository.AttendanceRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class AttendanceService {
    private final AttendanceRepository attendanceRepository;

    public void submitAttendance(AttendanceRequestDTO request) {
        LocalDate date = LocalDate.parse(request.getDate());
        request.getStudents().forEach(student -> {
            Attendance attendance = Attendance.builder()
                    .userId(student.getUserId())
                    .date(date)
                    .present(student.isPresent())
                    .department(request.getDepartment())
                    .semester(request.getSemester())
                    .academicYear(request.getAcademicYear())
                    .build();
            attendanceRepository.save(attendance);
        });
    }

    public List<Attendance> getAttendanceHistory(String department, Integer academicYear, Integer semester) {
        return attendanceRepository.findByDepartmentAndAcademicYearAndSemester(
                department, academicYear, semester);
    }
}