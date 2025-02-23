package com.CollegeManager.CollegeManagerServer.service.exam;

import com.CollegeManager.CollegeManagerServer.entity.Exam;
import com.CollegeManager.CollegeManagerServer.repository.ExamRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class ExamServiceImpl implements ExamService {
    private final ExamRepository examRepository;

    @Override
    public List<Exam> getExams(String department, String semester) {
        if ("All".equals(department) && "All".equals(semester)) {
            return examRepository.findAll();
        } else if ("All".equals(department)) {
            return examRepository.findBySemester(semester);
        } else if ("All".equals(semester)) {
            return examRepository.findByDepartment(department);
        } else {
            return examRepository.findByDepartmentAndSemester(department, semester);
        }
    }

    @Override
    public Exam addExam(Exam exam) {
        return examRepository.save(exam);
    }

    @Override
    public void deleteExam(Long id) {
        examRepository.deleteById(id);
    }
}