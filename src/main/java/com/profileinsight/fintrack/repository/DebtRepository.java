package com.profileinsight.fintrack.repository;

import com.profileinsight.fintrack.entity.Debt;
import com.profileinsight.fintrack.enums.DebtStatus;
import com.profileinsight.fintrack.enums.DebtType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface DebtRepository extends JpaRepository<Debt, Long> {

    // Kullanıcının tüm borçları (ödemeleriyle birlikte — JOIN FETCH)
    @Query("""
            SELECT d FROM Debt d
            LEFT JOIN FETCH d.payments
            WHERE d.user.id = :userId
            ORDER BY d.createdAt DESC
            """)
    List<Debt> findByUserIdWithPayments(@Param("userId") Long userId);

    // Sadece belirli statüsteki borçlar
    List<Debt> findByUserIdAndStatus(Long userId, DebtStatus status);

    // Vadesi geçmiş, ödenmemiş borçlar (hatırlatıcı için)
    @Query("""
            SELECT d FROM Debt d
            WHERE d.user.id = :userId
            AND d.status != 'PAID'
            AND d.dueDate < :today
            ORDER BY d.dueDate ASC
            """)
    List<Debt> findOverdue(
            @Param("userId") Long userId,
            @Param("today") LocalDate today
    );

    // Toplam alacak miktarı (LENT + PENDING/PARTIAL)
    @Query("""
            SELECT COALESCE(SUM(d.remainingAmount), 0)
            FROM Debt d
            WHERE d.user.id = :userId
            AND d.type = :type
            AND d.status != 'PAID'
            """)
    BigDecimal sumRemainingByUserAndType(
            @Param("userId") Long userId,
            @Param("type") DebtType type
    );
}
