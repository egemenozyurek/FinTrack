package com.profileinsight.fintrack.dto.request;

import com.profileinsight.fintrack.enums.DebtType;
import jakarta.validation.constraints.*;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DebtRequest {

    @NotBlank(message = "Person name cannot be empty")
    @Size(max = 100)
    private String personName;

    @NotNull(message = "amount cannot be nullable")
    @DecimalMin(value = "0.01", message = " amount must be greater then 0")
    private BigDecimal amount;

    @NotNull(message = "Debt type cannot be nullable")
    private DebtType type;

    @Future(message = "Due date must be future")
    private LocalDate dueDate;

    @Size(max = 255)
    private String note;

}
