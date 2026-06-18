package com.profileinsight.fintrack.entity;

import com.profileinsight.fintrack.enums.*;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "debts", indexes = {
        @Index(name = "idx_debts_user_status", columnList = "user_id, status"),
        @Index(name = "idx_debts_due_date", columnList = "due_date")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Debt extends BaseEntity{

    @Column(name = "person_name", nullable = false, length = 100)
    private String personName;

    @Column(name = "original_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal originalAmount;

    @Column(name = "remaining_amount", nullable = false, precision = 15, scale = 2)
    private BigDecimal remainingAmount;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private com.profileinsight.fintrack.enums.DebtType type;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    @Builder.Default
    private DebtStatus status = com.profileinsight.fintrack.enums.DebtStatus.PENDING;

    @Column(name = "due_date")
    private LocalDate dueDate;

    @Column(length = 255)
    private String note;

    @OneToMany(mappedBy = "debt", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<DebtPayment> payments = new ArrayList<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false,
            foreignKey = @ForeignKey(name = "fk_debt_user"))
    private User user;

    public void applyPayment(BigDecimal paymentAmount) {
        this.remainingAmount  =this.remainingAmount.subtract(paymentAmount);

        if (this.remainingAmount.compareTo(BigDecimal.ZERO) <= 0){
            this.remainingAmount = BigDecimal.ZERO;
            this.status = DebtStatus.PAID;
        } else{
            this.status = DebtStatus.PARTIAL;
        }
    }
}
