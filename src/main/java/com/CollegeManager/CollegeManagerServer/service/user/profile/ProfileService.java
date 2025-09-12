package com.CollegeManager.CollegeManagerServer.service.user.profile;

import com.CollegeManager.CollegeManagerServer.dto.*;
import org.springframework.stereotype.Service;

@Service
public interface ProfileService {
    ResponseDTO updateStudentProfile(String userEmail, StudentProfileUpdateDTO studentProfileUpdateDTO);
    ResponseDTO updateTeacherProfile(String userEmail, TeacherProfileUpdateDTO teacherProfileUpdateDTO);
    ResponseDTO updatePrincipalProfile(String userEmail, PrincipalProfileUpdateDTO principalProfileUpdateDTO);
    Object fetchPublicProfile(Long userId);
    UserProfileStatusDTO getProfileStatus(String email);
}