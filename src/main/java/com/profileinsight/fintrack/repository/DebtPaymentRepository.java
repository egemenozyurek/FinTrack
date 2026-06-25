package com.profileinsight.fintrack.repository;

import com.profileinsight.fintrack.entity.DebtPayment;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface DebtPaymentRepository extends JpaRepository<DebtPayment,Long> {
    List<DebtPayment> findByDebtIdOrderByPaidAtDesc(Long debtId);
}
