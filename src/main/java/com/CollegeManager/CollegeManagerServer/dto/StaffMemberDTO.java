package com.CollegeManager.CollegeManagerServer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class StaffMemberDTO {
    private Long id;
    private String name;
    private String email;
    private String phone;
    private String specialization; // Mapped from 'qualifications'
    private String status; // Default to "active" for now
}