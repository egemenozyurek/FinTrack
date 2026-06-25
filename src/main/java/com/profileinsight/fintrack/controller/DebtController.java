package com.profileinsight.fintrack.controller;

import com.profileinsight.fintrack.dto.request.DebtPaymentRequest;
import com.profileinsight.fintrack.dto.request.DebtRequest;
import com.profileinsight.fintrack.dto.response.DebtResponse;
import com.profileinsight.fintrack.security.SecurityUtils;
import com.profileinsight.fintrack.service.DebtService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/debts")
@RequiredArgsConstructor
public class DebtController {

    private final DebtService debtService;
    private final SecurityUtils securityUtils;

    @PostMapping
    public ResponseEntity<DebtResponse> create(@Valid @RequestBody DebtRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(debtService.create(userId, request));
    }

    @GetMapping
    public ResponseEntity<List<DebtResponse>> getAll() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(debtService.getAll(userId));
    }

    @GetMapping("/overdue")
    public ResponseEntity<List<DebtResponse>> getOverdue() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(debtService.getOverdue(userId));
    }

    @PostMapping("/{id}/payments")
    public ResponseEntity<DebtResponse> addPayment(
            @PathVariable Long id,
            @Valid @RequestBody DebtPaymentRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.status(HttpStatus.CREATED).body(debtService.addPayment(userId, id, request));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        debtService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }
}