package com.profileinsight.fintrack.dto.response;

import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
@Builder
public class MonthlyReportResponse {
    private int year;
    private int month;

    private BigDecimal totalIncome;
    private BigDecimal totalExpense;
    private BigDecimal netBalance;       // totalIncome - totalExpense

    private List<CategoryBreakdown> expensesByCategory;

    private BigDecimal totalLent;
    private BigDecimal totalBorrowed;

    @Data
    @Builder
    public static class CategoryBreakdown {
        private String categoryName;
        private String categoryColor;
        private BigDecimal amount;
        private double percentage;
    }
}
