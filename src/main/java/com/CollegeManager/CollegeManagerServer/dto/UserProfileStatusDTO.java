package com.CollegeManager.CollegeManagerServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UserProfileStatusDTO {
    private Long id;
    private String role;
    private String email;
    private Long collegeId;
}