package com.profileinsight.fintrack.dto.response;

import com.profileinsight.fintrack.enums.TransactionType;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@Builder
public class TransactionResponse {

    private Long id;
    private BigDecimal amount;
    private TransactionType type;
    private LocalDate transactionDate;
    private String note;
    private String receiptImageUrl;
    private boolean isRecurring;
    private LocalDateTime createdAt;

    // Category'nin tamamını değil, sadece UI'da lazım olanları gönder
    private Long categoryId;
    private String categoryName;
    private String categoryIcon;
    private String categoryColor;
}
