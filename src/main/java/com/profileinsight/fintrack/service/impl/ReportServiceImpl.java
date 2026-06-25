package com.profileinsight.fintrack.service.impl;

import com.profileinsight.fintrack.dto.response.MonthlyReportResponse;
import com.profileinsight.fintrack.enums.DebtType;
import com.profileinsight.fintrack.enums.TransactionType;
import com.profileinsight.fintrack.repository.DebtRepository;
import com.profileinsight.fintrack.repository.TransactionRepository;
import com.profileinsight.fintrack.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ReportServiceImpl implements ReportService {

    private final TransactionRepository transactionRepository;
    private final DebtRepository debtRepository;

    @Override
    @Transactional(readOnly = true)
    public MonthlyReportResponse getMonthlyReport(Long userId, int year, int month) {
        BigDecimal totalIncome = transactionRepository
                .sumByUserAndTypeAndMonth(userId, TransactionType.INCOME, year, month);

        BigDecimal totalExpense = transactionRepository
                .sumByUserAndTypeAndMonth(userId, TransactionType.EXPENSE, year, month);

        BigDecimal netBalance = totalIncome.subtract(totalExpense);

        // Category dağılımı — ham Object[] listesini anlamlı DTO'ya çeviriyoruz
        List<Object[]> rawBreakdown = transactionRepository.findExpensesByCategoryRaw(
                userId,
                java.time.LocalDate.of(year, month, 1),
                java.time.LocalDate.of(year, month, 1).withDayOfMonth(
                        java.time.LocalDate.of(year, month, 1).lengthOfMonth()
                )
        );

        List<MonthlyReportResponse.CategoryBreakdown> breakdown = rawBreakdown.stream()
                .map(row -> {
                    String name = (String) row[0];
                    String color = (String) row[1];
                    BigDecimal amount = (BigDecimal) row[2];

                    // Yüzde hesabı: toplam 0 ise sıfıra bölme hatasını önle
                    double percentage = totalExpense.compareTo(BigDecimal.ZERO) == 0
                            ? 0.0
                            : amount.divide(totalExpense, 4, RoundingMode.HALF_UP)
                            .multiply(BigDecimal.valueOf(100))
                            .doubleValue();

                    return MonthlyReportResponse.CategoryBreakdown.builder()
                            .categoryName(name)
                            .categoryColor(color)
                            .amount(amount)
                            .percentage(percentage)
                            .build();
                })
                .toList();

        BigDecimal totalLent = debtRepository
                .sumRemainingByUserAndType(userId, DebtType.LENT);

        BigDecimal totalBorrowed = debtRepository
                .sumRemainingByUserAndType(userId, DebtType.BORROWED);

        return MonthlyReportResponse.builder()
                .year(year)
                .month(month)
                .totalIncome(totalIncome)
                .totalExpense(totalExpense)
                .netBalance(netBalance)
                .expensesByCategory(breakdown)
                .totalLent(totalLent)
                .totalBorrowed(totalBorrowed)
                .build();
    }
}
