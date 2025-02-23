package com.LearnFree.LearnFreeServer.service.risk;

import com.LearnFree.LearnFreeServer.entity.Attendance;
import com.LearnFree.LearnFreeServer.entity.Grade;
import com.LearnFree.LearnFreeServer.repository.AttendanceRepository;
import com.LearnFree.LearnFreeServer.repository.GradeRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class RiskAssessmentService {
    private final AttendanceRepository attendanceRepository;
    private final GradeRepository gradeRepository;

    public Map<String, Object> assessRisk(Long studentId) {
        Map<String, Object> report = new HashMap<>();

        // Attendance check
        List<Attendance> attendances = attendanceRepository.findByUserId(studentId);
        long presentDays = attendances.stream().filter(Attendance::isPresent).count();
        double attendancePercentage = (double) presentDays / attendances.size() * 100;
        report.put("attendance", attendancePercentage < 75 ? "Low" : "OK");

        // CGPA check
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        double cgpa = grades.stream()
                .mapToDouble(g -> convertGradeToPoints(g.getGrade()))
                .average()
                .orElse(0.0);
        report.put("cgpa", cgpa < 2.0 ? "Low" : "OK");

        return report;
    }

    private double convertGradeToPoints(String grade) {
        return switch (grade) {
            case "A" -> 4.0;
            case "B" -> 3.0;
            case "C" -> 2.0;
            case "D" -> 1.0;
            default -> 0.0;
        };
    }
}