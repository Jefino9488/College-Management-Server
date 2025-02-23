package com.LearnFree.LearnFreeServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CollegeRegistrationDTO {
    private String name;
    private String address;
    private String contactEmail;
    private String phoneNumber;
}