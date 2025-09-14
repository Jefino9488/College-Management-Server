package com.CollegeManager.CollegeManagerServer.service.user.registration;

import com.CollegeManager.CollegeManagerServer.dto.AuthenticationResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.RegistrationRequestDTO;
import com.CollegeManager.CollegeManagerServer.dto.ResponseDTO;
import com.CollegeManager.CollegeManagerServer.dto.UserDTO;
import com.CollegeManager.CollegeManagerServer.emailsender.EmailService;
import com.CollegeManager.CollegeManagerServer.emailsender.EmailTemplateName;
import com.CollegeManager.CollegeManagerServer.entity.*;
import com.CollegeManager.CollegeManagerServer.repository.*;
import com.CollegeManager.CollegeManagerServer.security.jwt.JwtService;
import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Objects;

@Service
@RequiredArgsConstructor
public class RegistrationServiceImpl implements RegistrationService{

    private final UserAuthenticationRepository userAuthenticationRepository;
    private final UserAccountRepository userAccountRepository;
    private final DepartmentRepository departmentRepository;
    private final VerificationDataRepository verificationDataRepository;
    private final EmailService emailService;
    private final PasswordEncoder passwordEncoder;
    private final CollegeRepository collegeRepository;
    private final JwtService jwtService;

    @Override
    public ResponseDTO registrationEmailValidation(String email) throws MessagingException {

        if(userAuthenticationRepository.existsByEmail(email)){
            return ResponseDTO.builder()
                    .status(false)
                    .message("User Already Exists")
                    .build();
        }
        String generatedActivationCode = generateActivationCode();
        emailService.sendEmail(email,
                "",
                EmailTemplateName.VERIFY_EMAIL,
                generatedActivationCode,
                "Learn Free - Email Verification");
        VerificationData verificationData= VerificationData.builder()
                .email(email)
                .activationCode(generatedActivationCode)
                .createdDate(LocalDateTime.now())
                .expiresDate(LocalDateTime.now().plusMinutes(10))
                .build();
        verificationDataRepository.save(verificationData);
        return ResponseDTO.builder()
                .status(true)
                .message("Verification Email has been Sent Successfully")
                .build();
    }

    @Transactional
    public AuthenticationResponseDTO userRegistration(RegistrationRequestDTO registrationRequestDTO) throws MessagingException {
        ResponseDTO verificationResponse = verifyActivationCode(registrationRequestDTO);

        if (!verificationResponse.isStatus()) {
            throw new RuntimeException(verificationResponse.getMessage());
        }

        RoleEnum role = RoleEnum.valueOf(registrationRequestDTO.getRole().toUpperCase());

        UserAccount userData = UserAccount.builder()
                .firstName(registrationRequestDTO.getFirstName())
                .lastName(registrationRequestDTO.getLastName())
                .gender(registrationRequestDTO.getGender())
                .mobileNumber(registrationRequestDTO.getMobileNumber())
                .role(role)
                .build();

        Department department = null;

        if (role == RoleEnum.PRINCIPAL) {
            userData.setCollege(null);
            userData.setDepartment(null);
        } else {
            if (registrationRequestDTO.getCollegeId() == null) {
                throw new IllegalArgumentException("College ID is required for this role.");
            }
            College college = collegeRepository.findById(registrationRequestDTO.getCollegeId())
                    .orElseThrow(() -> new IllegalArgumentException("Invalid college ID: " + registrationRequestDTO.getCollegeId()));
            userData.setCollege(college);

            if (registrationRequestDTO.getDepartment() == null || registrationRequestDTO.getDepartment().isEmpty()) {
                throw new IllegalArgumentException("Department code is required for this role.");
            }
            department = departmentRepository.findByCodeAndCollege(registrationRequestDTO.getDepartment(), college)
                    .orElseThrow(() -> new IllegalArgumentException("Invalid department code '" + registrationRequestDTO.getDepartment() + "' for the selected college."));
            userData.setDepartment(department);
        }

        userAccountRepository.save(userData);

        if (role == RoleEnum.HOD && department != null) {
            if (department.getHod() != null) {
                throw new IllegalStateException("This department already has an HOD assigned.");
            }
            department.setHod(userData);
            departmentRepository.save(department);
        }

        UserAuthentication userAuthentication = UserAuthentication.builder()
                .userId(userData.getId())
                .email(registrationRequestDTO.getEmail())
                .password(passwordEncoder.encode(registrationRequestDTO.getPassword()))
                .role(role)
                .build();
        userAuthenticationRepository.save(userAuthentication);

        String jwtToken = jwtService.generateToken(userAuthentication);

        UserDTO userDto = UserDTO.builder()
                .id(userData.getId())
                .name(userData.getFirstName() + " " + userData.getLastName())
                .email(userAuthentication.getEmail())
                .role(userAuthentication.getRole().name().toLowerCase())
                .collegeId(userData.getCollege() != null ? userData.getCollege().getId() : null)
                .departmentId(userData.getDepartment() != null ? userData.getDepartment().getId() : null)
                .build();

        return AuthenticationResponseDTO.builder()
                .token(jwtToken)
                .user(userDto)
                .build();
    }

    private ResponseDTO verifyActivationCode(RegistrationRequestDTO registrationRequestDTO) throws MessagingException {
        if(!verificationDataRepository.existsByEmail(registrationRequestDTO.getEmail())){
            return ResponseDTO.builder()
                    .status(false)
                    .message("User Not registered there Email")
                    .build();
        }
        if(!verificationDataRepository.existsByActivationCode(registrationRequestDTO.getActivationCode())){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Invalid Activation code")
                    .build();
        }
        VerificationData verificationData=verificationDataRepository.findByActivationCode(
                registrationRequestDTO.getActivationCode()
        );
        if(!Objects.equals(verificationData.getEmail(), registrationRequestDTO.getEmail())){
            return ResponseDTO.builder()
                    .status(false)
                    .message("Activation Code is Not Match with your Email")
                    .build();
        }
        if(LocalDateTime.now().isAfter(verificationData.getExpiresDate())){
            String generatedActivationCode = generateActivationCode();
            emailService.sendEmail(registrationRequestDTO.getEmail(),
                    "",
                    EmailTemplateName.VERIFY_EMAIL,
                    generatedActivationCode,
                    "Learn Free - Email Verification");
            verificationData.setActivationCode(generatedActivationCode);
            verificationData.setCreatedDate(LocalDateTime.now());
            verificationData.setExpiresDate(LocalDateTime.now().plusMinutes(10));
            verificationDataRepository.save(verificationData);
            return ResponseDTO.builder()
                    .status(false)
                    .message("Activation Code has been Expired , New Activation Code sent Successfully")
                    .build();
        }
        return ResponseDTO.builder()
                .status(true)
                .message("Activation Code has been verified Successfully")
                .build();
    }

    private String generateActivationCode(){
        String characters = "0123456789";
        StringBuilder code = new StringBuilder();
        SecureRandom secureRandom = new SecureRandom();
        for(int i=0;i<6;i++){
            int randomIndex = secureRandom.nextInt(characters.length());
            code.append(characters.charAt(randomIndex));
        }
        return code.toString();
    }
}