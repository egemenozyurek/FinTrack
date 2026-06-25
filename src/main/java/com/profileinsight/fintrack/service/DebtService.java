package com.profileinsight.fintrack.service;

import com.profileinsight.fintrack.dto.request.DebtPaymentRequest;
import com.profileinsight.fintrack.dto.request.DebtRequest;
import com.profileinsight.fintrack.dto.response.DebtResponse;

import java.util.List;

public interface DebtService {
    DebtResponse create(Long userId, DebtRequest request);
    DebtResponse addPayment(Long userId, Long debtId, DebtPaymentRequest request);
    List<DebtResponse> getAll(Long userId);
    List<DebtResponse> getOverdue(Long userId);
    void delete(Long userId, Long debtId);
}
