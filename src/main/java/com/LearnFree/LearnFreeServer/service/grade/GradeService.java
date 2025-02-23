package com.LearnFree.LearnFreeServer.service.grade;

import com.LearnFree.LearnFreeServer.entity.Grade;
import com.LearnFree.LearnFreeServer.entity.Subject;
import com.LearnFree.LearnFreeServer.entity.UserAccount;
import com.LearnFree.LearnFreeServer.repository.GradeRepository;
import com.LearnFree.LearnFreeServer.repository.SubjectRepository;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class GradeService {
    private final GradeRepository gradeRepository;
    private final SubjectRepository subjectRepository;
    private final UserAccountRepository userAccountRepository;

    public double calculateCGPA(Long studentId) {
        List<Grade> grades = gradeRepository.findByStudentId(studentId);
        return grades.stream()
                .mapToDouble(g -> convertGradeToPoints(g.getGrade()) * g.getSubject().getCredits())
                .sum() / grades.stream().mapToInt(g -> g.getSubject().getCredits()).sum();
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

    public void addGrade(String registrationNumber, String subjectCode, String grade, int semester) {
        UserAccount student = userAccountRepository.findByRegistrationNumber(registrationNumber)
                .orElseThrow(() -> new IllegalArgumentException("Student not found"));
        Subject subject = subjectRepository.findByCode(subjectCode)
                .orElseThrow(() -> new IllegalArgumentException("Subject not found"));

        if (gradeRepository.existsByStudentIdAndSubjectId(student.getId(), subject.getId())) {
            throw new IllegalArgumentException("Grade already exists for this student and subject");
        }

        Grade gradeEntity = Grade.builder()
                .student(student)
                .subject(subject)
                .semester(semester)
                .grade(grade)
                .build();

        gradeRepository.save(gradeEntity);
    }
}