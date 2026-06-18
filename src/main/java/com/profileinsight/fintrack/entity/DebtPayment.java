package com.profileinsight.fintrack.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;


@Entity
@Table(name = "debt_payments")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DebtPayment extends BaseEntity{


    private BigDecimal amount;


    private LocalDate paidAt;

    private String note;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "debt_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_payment_debt"))
    private Debt debt;

}
