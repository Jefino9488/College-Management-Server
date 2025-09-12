package com.CollegeManager.CollegeManagerServer.service.user.authentication;

import com.CollegeManager.CollegeManagerServer.dto.AuthenticationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.AuthenticationResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.UserDTO;
import com.CollegeManager.CollegeManagerServer.entity.UserAccount;
import com.CollegeManager.CollegeManagerServer.entity.UserAuthentication;
import com.CollegeManager.CollegeManagerServer.repository.UserAccountRepository;
import com.CollegeManager.CollegeManagerServer.repository.UserAuthenticationRepository;
import com.CollegeManager.CollegeManagerServer.security.jwt.JwtService;
import jakarta.persistence.EntityNotFoundException;
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
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authenticationRequestDTO.getEmail(), authenticationRequestDTO.getPassword())
        );

        UserAuthentication userAuth = userAuthenticationRepository.findByEmail(authenticationRequestDTO.getEmail())
                .orElseThrow(() -> new EntityNotFoundException("User not found with email: " + authenticationRequestDTO.getEmail()));

        UserAccount userAccount = userAccountRepository.findById(userAuth.getUserId())
                .orElseThrow(() -> new EntityNotFoundException("User account not found for ID: " + userAuth.getUserId()));

        String jwtToken = jwtService.generateToken(userAuth);

        UserDTO userDto = UserDTO.builder()
                .id(userAccount.getId())
                .name(userAccount.getFirstName() + " " + userAccount.getLastName())
                .email(userAuth.getEmail())
                .role(userAuth.getRole().name().toLowerCase())
                .collegeId(userAccount.getCollege() != null ? userAccount.getCollege().getId() : null)
                .departmentId(userAccount.getDepartment() != null ? userAccount.getDepartment().getId() : null)
                .build();

        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }
}