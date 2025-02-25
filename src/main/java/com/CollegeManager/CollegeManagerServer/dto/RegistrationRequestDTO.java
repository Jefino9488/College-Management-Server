package com.CollegeManager.CollegeManagerServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor

public class RegistrationRequestDTO {

    private String firstName;

    private String lastName;

    private String gender;

    private String mobileNumber;

    private String email;

    private String password;

    private String role;

    private String department;

    private String activationCode;

    private String academicYear;

    private Long collegeId;

}