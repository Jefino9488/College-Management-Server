package com.LearnFree.LearnFreeServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AttendanceRequestDTO {
    private String date;
    private String department;
    private String semester;
    private List<StudentAttendanceDTO> students;
}