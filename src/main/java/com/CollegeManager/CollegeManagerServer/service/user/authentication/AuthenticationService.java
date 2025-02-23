package com.CollegeManager.CollegeManagerServer.service.user.authentication;

import com.CollegeManager.CollegeManagerServer.dto.AuthenticationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.AuthenticationResponseDTO;
import org.springframework.stereotype.Service;

@Service
public interface AuthenticationService {

    AuthenticationResponseDTO authenticate(AuthenticationRequestDTO authenticationRequestDTO);

}