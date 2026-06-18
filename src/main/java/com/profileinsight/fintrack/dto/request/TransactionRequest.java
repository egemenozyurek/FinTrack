package com.profileinsight.fintrack.dto.request;

import com.profileinsight.fintrack.enums.TransactionType;
import lombok.Data;
import jakarta.validation.constraints.*;


import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class TransactionRequest {

    @NotNull(message = "Amount cannot be nullable")
    @DecimalMin(value = "0.01", message = "Amount must be greater than 0")
    @Digits(integer = 13, fraction = 2, message = "Invalid amount")
    private BigDecimal amount;

    @NotNull(message = "Transaction type cannot be nullable")
    private TransactionType type;

    @NotNull(message = "Date cannot be nullable")
    @PastOrPresent(message = "A transaction witha future date cannot be added")
    private LocalDate transactionDate;

    @NotNull(message = "Category cannot be nullable")
    private Long categoryId;

    @Size(max = 255, message = "note must not exceed 255 characters")
    private String note;

    private String receiptImageUrl;

    private boolean isRecurring = false;
}
