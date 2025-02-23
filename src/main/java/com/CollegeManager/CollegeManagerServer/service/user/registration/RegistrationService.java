package com.CollegeManager.CollegeManagerServer.service.user.registration;

import com.CollegeManager.CollegeManagerServer.dto.RegistrationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import jakarta.mail.MessagingException;
import org.springframework.stereotype.Service;

@Service
public interface RegistrationService {

    ResponseDTO registrationEmailValidation(String email) throws MessagingException;

    ResponseDTO userRegistration(RegistrationRequestDTO registrationRequestDTO) throws MessagingException;

}