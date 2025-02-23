package com.CollegeManager.CollegeManagerServer.service.exam;

import com.CollegeManager.CollegeManagerServer.entity.Exam;

import java.util.List;

public interface ExamService {
    List<Exam> getExams(String department, String semester);
    Exam addExam(Exam exam);
    void deleteExam(Long id);
}