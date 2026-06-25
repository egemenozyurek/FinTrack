package com.profileinsight.fintrack.controller;

import com.profileinsight.fintrack.dto.response.MonthlyReportResponse;
import com.profileinsight.fintrack.security.SecurityUtils;
import com.profileinsight.fintrack.service.ReportService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/reports")
@RequiredArgsConstructor
public class ReportController {

    private final ReportService reportService;
    private final SecurityUtils securityUtils;

    // GET /api/v1/reports/monthly?year=2024&month=6
    @GetMapping("/monthly")
    public ResponseEntity<MonthlyReportResponse> getMonthlyReport(
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(reportService.getMonthlyReport(userId, year, month));
    }
}