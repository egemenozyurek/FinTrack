package com.profileinsight.fintrack.service;

import com.profileinsight.fintrack.dto.response.MonthlyReportResponse;

public interface ReportService {
    MonthlyReportResponse getMonthlyReport(Long userId, int year, int month);
}
