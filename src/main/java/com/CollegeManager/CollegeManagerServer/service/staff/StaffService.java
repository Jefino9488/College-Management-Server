package com.CollegeManager.CollegeManagerServer.service.staff;

import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.StudentDTO;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Service
public interface StaffService {
    ResponseDTO addStudents(MultipartFile file, String department, Integer academicYear);
    List<StudentDTO> getStudentsByDepartmentAndYear(String department, Integer academicYear);

    ResponseDTO addGrades(MultipartFile file, int semester, String department, int academicYear);
}