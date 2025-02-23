package com.LearnFree.LearnFreeServer.service.college;

import com.LearnFree.LearnFreeServer.dto.CollegeRegistrationDTO;
import com.LearnFree.LearnFreeServer.entity.College;
import com.LearnFree.LearnFreeServer.entity.RoleEnum;
import com.LearnFree.LearnFreeServer.entity.UserAccount;
import com.LearnFree.LearnFreeServer.entity.UserAuthentication;
import com.LearnFree.LearnFreeServer.repository.CollegeRepository;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import com.LearnFree.LearnFreeServer.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CollegeService {
    private final CollegeRepository collegeRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAuthenticationRepository authRepository;
    private final PasswordEncoder passwordEncoder;

    public College createCollege(CollegeRegistrationDTO dto) {
        String collegeCode = generateCollegeCode(dto.getName());

        College college = College.builder()
                .code(collegeCode)
                .name(dto.getName())
                .address(dto.getAddress())
                .contactEmail(dto.getContactEmail())
                .phoneNumber(dto.getPhoneNumber())
                .build();

        return collegeRepository.save(college);
    }

    public void assignPrincipalToCollege(College college, String principalEmail) {
        UserAuthentication principalAuth = authRepository.findByEmail(principalEmail);

        UserAccount principal = userAccountRepository.findById(principalAuth.getUserId())
                .orElseThrow(() -> new IllegalArgumentException("Principal account not found"));

        college.setPrincipal(principal);
        collegeRepository.save(college);

        // Update principal role
        principalAuth.setRole(RoleEnum.PRINCIPAL);
        authRepository.save(principalAuth);
    }

    private String generateCollegeCode(String name) {
        return Arrays.stream(name.split(" "))
                .map(word -> word.substring(0, 1))
                .collect(Collectors.joining())
                .toUpperCase();
    }
}