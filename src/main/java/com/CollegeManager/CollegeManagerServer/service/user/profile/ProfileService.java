package com.CollegeManager.CollegeManagerServer.service.user.profile;

import com.CollegeManager.CollegeManagerServer.dto.PrincipalProfileUpdateDTO;
import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.StudentProfileUpdateDTO;
import com.CollegeManager.CollegeManagerServer.dto.TeacherProfileUpdateDTO;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {
    ResponseDTO updateStudentProfile(String userEmail, StudentProfileUpdateDTO studentProfileUpdateDTO);
    ResponseDTO updateTeacherProfile(String userEmail, TeacherProfileUpdateDTO teacherProfileUpdateDTO);
    ResponseDTO updatePrincipalProfile(String userEmail, PrincipalProfileUpdateDTO principalProfileUpdateDTO);
    Object fetchPublicProfile(Long userId);
}