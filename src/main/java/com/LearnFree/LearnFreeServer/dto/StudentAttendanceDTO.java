package com.LearnFree.LearnFreeServer.dto;

import lombok.Data;

@Data
public class StudentAttendanceDTO {
    private Long userId;
    private boolean present;
}