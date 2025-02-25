package com.CollegeManager.CollegeManagerServer.service.user.authentication;

import com.CollegeManager.CollegeManagerServer.dto.AuthenticationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.AuthenticationResponseDTO;
import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
import com.CollegeManager.CollegeManagerServer.security.jwt.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.HashMap;

@Service
@RequiredArgsConstructor
public class AuthenticationServiceImpl implements AuthenticationService {

    private final JwtService jwtService;
    private final AuthenticationManager authenticationManager;
    private final UserAuthenticationRepository userAuthenticationRepository;
    private final UserAccountRepository userAccountRepository;

    @Override
    public AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO) {

        Authentication auth = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(
                        authenticationRequestDTO.getEmail(),
                        authenticationRequestDTO.getPassword()
                )
        );

        UserAuthentication userAuthentication = userAuthenticationRepository.findByEmail(
                authenticationRequestDTO.getEmail()
        );

        UserAccount userData = userAccountRepository.findById(userAuthentication.getUserId())
                .orElseThrow(() -> new UsernameNotFoundException("User Not Found"));

        var claims = new HashMap<String, Object>();
        var user = (UserAuthentication) auth.getPrincipal();
        claims.put("role", user.getRole());
        String generatedJwtToken = jwtService.generateToken(claims, user);

        String departmentName = userData.getDepartment() != null ? userData.getDepartment().getName() : null;
        Long collegeId = userData.getCollege() != null ? userData.getCollege().getId() : null;
        String collegeName = userData.getCollege() != null ? userData.getCollege().getName() : null;

        return AuthenticationResponseDTO.builder()
                .userId(userAuthentication.getUserId())
                .firstName(userData.getFirstName())
                .lastName(userData.getLastName())
                .gender(userData.getGender())
                .mobileNumber(userData.getMobileNumber())
                .email(userAuthentication.getEmail())
                .department(departmentName)
                .role(userAuthentication.getRole().toString())
                .jwtToken(generatedJwtToken)
                .collegeId(collegeId)
                .collegeName(collegeName)
                .build();
    }
}