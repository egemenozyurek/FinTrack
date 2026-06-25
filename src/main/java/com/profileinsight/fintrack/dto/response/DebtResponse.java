package com.profileinsight.fintrack.dto.response;

import com.profileinsight.fintrack.enums.DebtStatus;
import com.profileinsight.fintrack.enums.DebtType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Data
@Builder
public class DebtResponse {
    private Long id;
    private String personName;
    private BigDecimal originalAmount;
    private BigDecimal remainingAmount;
    private BigDecimal paidAmount;   // originalAmount - remainingAmount, hesaplanmış
    private DebtType type;
    private DebtStatus status;
    private LocalDate dueDate;
    private String note;
    private LocalDateTime createdAt;
    private List<PaymentResponse> payments;

    @Data
    @Builder
    public static class PaymentResponse {
        private Long id;
        private BigDecimal amount;
        private LocalDate paidAt;
        private String note;
    }
}
