package com.profileinsight.fintrack.repository;

import com.profileinsight.fintrack.entity.Transaction;
import com.profileinsight.fintrack.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Repository
public interface TransactionRepository extends JpaRepository<Transaction, Long> {
    @Query("""
            SELECT t FROM Transaction t
            JOIN FETCH t.category
            WHERE t.user.id = :userId
            AND t.transactionDate BETWEEN :startDate AND :endDate
            ORDER BY t.transactionDate DESC
            """)
    List<Transaction> findByUserAndDateRange(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );


    @Query("""
            SELECT COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
            AND t.type = :type
            AND YEAR(t.transactionDate) = :year
            AND MONTH(t.transactionDate) = :month
            """)
    BigDecimal sumByUserAndTypeAndMonth(
            @Param("userId") Long userId,
            @Param("type") TransactionType type,
            @Param("year") int year,
            @Param("month") int month
    );

    // Dashboard için kategori bazlı harcama dağılımı (grafik verisi)
    @Query("""
            SELECT c.name, c.color, COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            JOIN t.category c
            WHERE t.user.id = :userId
            AND t.type = 'EXPENSE'
            AND t.transactionDate BETWEEN :startDate AND :endDate
            GROUP BY c.id, c.name, c.color
            ORDER BY SUM(t.amount) DESC
            """)
    List<Object[]> findExpensesByCategoryRaw(
            @Param("userId") Long userId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate
    );

    // Son 6 ayın gelir/gider trendi (grafik verisi)
    @Query("""
            SELECT YEAR(t.transactionDate), MONTH(t.transactionDate),
                   t.type, COALESCE(SUM(t.amount), 0)
            FROM Transaction t
            WHERE t.user.id = :userId
            AND t.transactionDate >= :since
            GROUP BY YEAR(t.transactionDate), MONTH(t.transactionDate), t.type
            ORDER BY YEAR(t.transactionDate), MONTH(t.transactionDate)
            """)
    List<Object[]> findMonthlyTrendRaw(
            @Param("userId") Long userId,
            @Param("since") LocalDate since
    );

    List<Transaction> findByUserIdOrderByTransactionDateDesc(Long userId);
}
