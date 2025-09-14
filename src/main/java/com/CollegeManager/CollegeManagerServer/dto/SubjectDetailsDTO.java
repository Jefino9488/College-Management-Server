package com.CollegeManager.CollegeManagerServer.dto;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class SubjectDetailsDTO {
    private Long id;
    private String name;
    private String code;
    private int credits;
    private String assignedTeacher;
    private long enrolledStudents;
}
