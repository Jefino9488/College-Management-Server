package com.LearnFree.LearnFreeServer.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class FeeDTO {
    private Long studentId;
    private Double amount;
    private LocalDate paymentDate;
    private Boolean isPaid;
}