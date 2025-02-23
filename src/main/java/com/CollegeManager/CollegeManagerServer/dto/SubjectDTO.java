package com.CollegeManager.CollegeManagerServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class SubjectDTO {
    private String code;
    private String name;
    private int semester;
    private int year;
    private Long departmentId;
    private int credits;
}