package com.LearnFree.LearnFreeServer.service.principal;

import com.LearnFree.LearnFreeServer.entity.College;
import com.LearnFree.LearnFreeServer.entity.RoleEnum;
import com.LearnFree.LearnFreeServer.entity.UserAccount;
import com.LearnFree.LearnFreeServer.entity.UserAuthentication;
import com.LearnFree.LearnFreeServer.repository.CollegeRepository;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import com.LearnFree.LearnFreeServer.repository.UserAuthenticationRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class PrincipalService {
    private final CollegeRepository collegeRepository;
    private final UserAccountRepository userAccountRepository;
    private final UserAuthenticationRepository authRepository;

    public void assignPrincipalToCollege(Long collegeId, Long principalId) {
        College college = collegeRepository.findById(collegeId).orElseThrow();
        UserAccount principal = userAccountRepository.findById(principalId).orElseThrow();

        college.setPrincipal(principal);
        collegeRepository.save(college);

        // Update principal role
        UserAuthentication auth = authRepository.findByUserId(principal.getId())
                .orElseThrow(() -> new RuntimeException("User auth not found"));
        auth.setRole(RoleEnum.PRINCIPAL);
    }
}