package com.CollegeManager.CollegeManagerServer.dto.dashboard;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RecentActivityDTO {
    private String id;
    private String type; // e.g., "subject", "meeting", "attendance"
    private String message;
    private LocalDateTime timestamp;
}