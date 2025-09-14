package com.CollegeManager.CollegeManagerServer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class GradeDetailsDTO {
    private String subjectName;
    private String subjectCode;
    private int credits;
    private String grade;
    private int semester;
}
