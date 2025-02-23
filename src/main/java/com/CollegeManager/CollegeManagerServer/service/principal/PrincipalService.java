package com.CollegeManager.CollegeManagerServer.service.principal;

import com.CollegeManager.CollegeManagerServer.entity.College;
import com.CollegeManager.CollegeManagerServer.entity.RoleEnum;
import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.repository.CollegeRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
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