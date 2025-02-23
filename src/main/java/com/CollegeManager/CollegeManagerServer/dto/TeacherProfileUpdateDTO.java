package com.CollegeManager.CollegeManagerServer.dto;

import lombok.*;

@EqualsAndHashCode(callSuper = true)
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class TeacherProfileUpdateDTO extends ProfileUpdateDTO {
    private String firstName;
    private String lastName;
    private String gender;
    private String dateOfBirth;
    private String mobileNumber;
    private String department;
    private String personalEmail;
    private String qualifications;
    private String bio;
}