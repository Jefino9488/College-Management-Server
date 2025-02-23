package com.LearnFree.LearnFreeServer.service.user.profile;

import com.LearnFree.LearnFreeServer.dto.PrincipalProfileUpdateDTO;
import com.LearnFree.LearnFreeServer.dto.ResponseDTO;
import com.LearnFree.LearnFreeServer.dto.StudentProfileUpdateDTO;
import com.LearnFree.LearnFreeServer.dto.TeacherProfileUpdateDTO;
import com.LearnFree.LearnFreeServer.entity.Department;
import com.LearnFree.LearnFreeServer.entity.RoleEnum;
import com.LearnFree.LearnFreeServer.entity.UserAccount;
import com.LearnFree.LearnFreeServer.repository.DepartmentRepository;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import com.LearnFree.LearnFreeServer.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ProfileServiceImpl implements ProfileService {

    private final UserAccountRepository userAccountRepository;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final DepartmentRepository departmentRepository;

    @Override
    public ResponseDTO updateStudentProfile(String userEmail, StudentProfileUpdateDTO dto) {
        var userAuthentication = userAuthenticationRepository.findByEmail(userEmail);
        if (userAuthentication == null || !userAuthentication.getRole().equals(RoleEnum.STUDENT)) {
            return ResponseDTO.builder().status(false).message("Unauthorized access: Only students can update their profiles.").build();
        }
        var userAccount = userAccountRepository.findById(userAuthentication.getUserId()).orElse(null);
        if (userAccount == null) {
            return ResponseDTO.builder().status(false).message("User account not found.").build();
        }

        // Update fields
        if (dto.getFirstName() != null) userAccount.setFirstName(dto.getFirstName());
        if (dto.getLastName() != null) userAccount.setLastName(dto.getLastName());
        if (dto.getGender() != null) userAccount.setGender(dto.getGender());
        if (dto.getDateOfBirth() != null) userAccount.setDateOfBirth(dto.getDateOfBirth());
        if (dto.getMobileNumber() != null) userAccount.setMobileNumber(dto.getMobileNumber());
        if (dto.getPersonalEmail() != null) userAccount.setPersonalEmail(dto.getPersonalEmail());
        if (dto.getGithub() != null) userAccount.setGithub(dto.getGithub());
        if (dto.getLinkedIn() != null) userAccount.setLinkedIn(dto.getLinkedIn());
        if (dto.getStudentId() != null) userAccount.setRegistrationNumber(dto.getStudentId());
        if (dto.getMajor() != null) userAccount.setMajor(dto.getMajor());
        if (dto.getAcademicYear() != null) userAccount.setAcademicYear(dto.getAcademicYear());
        if (dto.getSemester() != null) userAccount.setSemester(dto.getSemester());
        if (dto.getGpa() != null) userAccount.setGpa(dto.getGpa());
        if (dto.getAdvisor() != null) userAccount.setAdvisor(dto.getAdvisor());
        if (dto.getDepartmentId() != null) {
            Department department = departmentRepository.findById(dto.getDepartmentId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department ID"));
            userAccount.setDepartment(department);
        }
        if (dto.getEnrollmentDate() != null && !dto.getEnrollmentDate().trim().isEmpty())
            userAccount.setEnrollmentDate(LocalDate.parse(dto.getEnrollmentDate()));
        if (dto.getExpectedGraduation() != null && !dto.getExpectedGraduation().trim().isEmpty())
            userAccount.setExpectedGraduation(LocalDate.parse(dto.getExpectedGraduation()));

        // Handle collections
        if (dto.getAchievements() != null) {
            userAccount.getAchievements().clear();
            userAccount.getAchievements().addAll(dto.getAchievements());
        }
        if (dto.getActivities() != null) {
            userAccount.getActivities().clear();
            userAccount.getActivities().addAll(dto.getActivities());
        }
        if (dto.getCourses() != null) {
            userAccount.getCourses().clear();
            userAccount.getCourses().addAll(dto.getCourses());
        }
        if (dto.getCertificates() != null) {
            userAccount.getCertificates().clear();
            userAccount.getCertificates().addAll(dto.getCertificates());
        }

        userAccountRepository.save(userAccount);
        return ResponseDTO.builder().status(true).message("Profile updated successfully.").build();
    }


    @Override
    public ResponseDTO updateTeacherProfile(String userEmail, TeacherProfileUpdateDTO dto) {
        var userAuthentication = userAuthenticationRepository.findByEmail(userEmail);
        if (userAuthentication == null) {
            return ResponseDTO.builder()
                    .status(false)
                    .message("User not found")
                    .build();
        }
        var userAccount = userAccountRepository.findById(userAuthentication.getUserId()).orElse(null);
        if (userAccount == null) {
            return ResponseDTO.builder()
                    .status(false)
                    .message("User account not found")
                    .build();
        }
        userAccount.setFirstName(dto.getFirstName());
        userAccount.setLastName(dto.getLastName());
        userAccount.setGender(dto.getGender());
        userAccount.setDateOfBirth(dto.getDateOfBirth());
        userAccount.setMobileNumber(dto.getMobileNumber());

        userAccount.setPersonalEmail(dto.getPersonalEmail());
        userAccountRepository.save(userAccount);
        return ResponseDTO.builder()
                .status(true)
                .message("Profile updated successfully")
                .build();
    }

    @Override
    public ResponseDTO updatePrincipalProfile(String userEmail, PrincipalProfileUpdateDTO dto) {
        var userAuthentication = userAuthenticationRepository.findByEmail(userEmail);
        if (userAuthentication == null) {
            return ResponseDTO.builder()
                    .status(false)
                    .message("User not found")
                    .build();
        }
        var userAccount = userAccountRepository.findById(userAuthentication.getUserId()).orElse(null);
        if (userAccount == null) {
            return ResponseDTO.builder()
                    .status(false)
                    .message("User account not found")
                    .build();
        }
        userAccount.setFirstName(dto.getFirstName());
        userAccount.setLastName(dto.getLastName());
        userAccount.setGender(dto.getGender());
        userAccount.setDateOfBirth(dto.getDateOfBirth());
        userAccount.setMobileNumber(dto.getMobileNumber());
        userAccount.setPersonalEmail(dto.getPersonalEmail());
        userAccount.setQualifications(dto.getQualifications());
        userAccountRepository.save(userAccount);
        return ResponseDTO.builder()
                .status(true)
                .message("Profile updated successfully")
                .build();
    }

    @Override
    public Object fetchPublicProfile(Long userId) {
        var userAccountOptional = userAccountRepository.findById(userId);
        if (userAccountOptional.isEmpty()) {
            return ResponseDTO.builder().status(false).message("User not found").build();
        }
        var userAccount = userAccountOptional.get();
        Map<String, Object> profile = new HashMap<>();
        profile.put("id", userAccount.getId());
        profile.put("firstName", userAccount.getFirstName());
        profile.put("lastName", userAccount.getLastName());
        profile.put("gender", userAccount.getGender());
        profile.put("dateOfBirth", userAccount.getDateOfBirth());
        profile.put("mobileNumber", userAccount.getMobileNumber());
        profile.put("personalEmail", userAccount.getPersonalEmail());
        profile.put("registrationNumber", userAccount.getRegistrationNumber());
        profile.put("github", userAccount.getGithub());
        profile.put("linkedIn", userAccount.getLinkedIn());
        profile.put("qualifications", userAccount.getQualifications());
        profile.put("bio", userAccount.getBio());
        profile.put("academicYear", userAccount.getAcademicYear());
        profile.put("semester", userAccount.getSemester());
        profile.put("major", userAccount.getMajor());
        profile.put("gpa", userAccount.getGpa());
        profile.put("advisor", userAccount.getAdvisor());
        profile.put("enrollmentDate", userAccount.getEnrollmentDate());
        profile.put("expectedGraduation", userAccount.getExpectedGraduation());
        profile.put("achievements", userAccount.getAchievements());
        profile.put("activities", userAccount.getActivities());
        profile.put("courses", userAccount.getCourses());
        profile.put("certificates", userAccount.getCertificates());
        return profile;
    }


    private Map<String, Object> buildStudentProfile(UserAccount userAccount) {
        Map<String, Object> profile = new HashMap<>();
        profile.put("name", userAccount.getFirstName() + " " + userAccount.getLastName());
        if (userAccount.getPersonalEmail() != null) profile.put("personalEmail", userAccount.getPersonalEmail());
        if (userAccount.getDepartment() != null) profile.put("department", userAccount.getDepartment());
        if (userAccount.getGithub() != null) profile.put("github", userAccount.getGithub());
        if (userAccount.getLinkedIn() != null) profile.put("linkedIn", userAccount.getLinkedIn());
        if (userAccount.getRegistrationNumber() != null) profile.put("studentId", userAccount.getRegistrationNumber());
        if (userAccount.getMajor() != null) profile.put("major", userAccount.getMajor());
        if (userAccount.getAcademicYear() != null) profile.put("academicYear", userAccount.getAcademicYear());
        if (userAccount.getSemester() != null) profile.put("semester", userAccount.getSemester());
        if (userAccount.getGpa() != null) profile.put("gpa", userAccount.getGpa());
        if (userAccount.getAdvisor() != null) profile.put("advisor", userAccount.getAdvisor());
        if (userAccount.getEnrollmentDate() != null) profile.put("enrollmentDate", userAccount.getEnrollmentDate());
        if (userAccount.getExpectedGraduation() != null) profile.put("expectedGraduation", userAccount.getExpectedGraduation());
        if (userAccount.getAchievements() != null && !userAccount.getAchievements().isEmpty()) profile.put("achievements", userAccount.getAchievements());
        if (userAccount.getActivities() != null && !userAccount.getActivities().isEmpty()) profile.put("activities", userAccount.getActivities());
        if (userAccount.getCourses() != null && !userAccount.getCourses().isEmpty()) profile.put("courses", userAccount.getCourses());
        if (userAccount.getCertificates() != null && !userAccount.getCertificates().isEmpty()) profile.put("certificates", userAccount.getCertificates());
        return profile;
    }

    private Map<String, String> buildTeacherProfile(UserAccount userAccount) {
        Map<String, String> profile = new HashMap<>();
        profile.put("name", userAccount.getFirstName() + " " + userAccount.getLastName());
        if (userAccount.getPersonalEmail() != null) profile.put("personalEmail", userAccount.getPersonalEmail());
        if (userAccount.getMobileNumber() != null) profile.put("phoneNumber", userAccount.getMobileNumber());
        return profile;
    }
}
