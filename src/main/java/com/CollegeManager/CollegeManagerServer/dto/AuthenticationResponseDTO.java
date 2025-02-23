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

public class AuthenticationResponseDTO {

    private Long userId;

    private String firstName;

    private String lastName;

    private String gender;

    private Integer age;

    private String mobileNumber;

    private String email;

    private String department;

    private String role;

    private String jwtToken;

}