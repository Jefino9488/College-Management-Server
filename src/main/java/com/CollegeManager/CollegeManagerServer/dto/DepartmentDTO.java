package com.CollegeManager.CollegeManagerServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DepartmentDTO {
    private String code;
    private String name;
    private String description;
    private int totalYears;
    private int semestersPerYear;
    private Long collegeId;
}