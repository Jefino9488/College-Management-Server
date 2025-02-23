package com.LearnFree.LearnFreeServer.service.user.authentication;

import com.LearnFree.LearnFreeServer.dto.AuthenticationRequestDTO;
import com.LearnFree.LearnFreeServer.dto.AuthenticationResponseDTO;
import com.LearnFree.LearnFreeServer.entity.UserAccount;
import com.LearnFree.LearnFreeServer.entity.UserAuthentication;
import com.LearnFree.LearnFreeServer.repository.UserAccountRepository;
import com.LearnFree.LearnFreeServer.repository.UserAuthenticationRepository;
import com.LearnFree.LearnFreeServer.security.jwt.JwtService;
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
                .build();
    }
}
