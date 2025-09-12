package com.CollegeManager.CollegeManagerServer.service.user.registration;

import com.CollegeManager.CollegeManagerServer.dto.AuthenticationResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.RegistrationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface RegistrationService {

    ResponseDTO registrationEmailValidation(String email) throws MessagingException;

    AuthenticationResponseDTO userRegistration(RegistrationRequestDTO registrationRequestDTO) throws MessagingException;
}