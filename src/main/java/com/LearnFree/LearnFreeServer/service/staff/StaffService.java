package com.LearnFree.LearnFreeServer.service.staff;

import com.LearnFree.LearnFreeServer.dto.ResponseDTO;
import com.LearnFree.LearnFreeServer.dto.StudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface StaffService {
    ResponseDTO addStudents(MultipartFile file, String department, Integer academicYear);
    List<StudentDTO> getStudentsByDepartmentAndYear(String department, Integer academicYear);

    ResponseDTO addGrades(MultipartFile file, int semester, String department, int academicYear);
}
