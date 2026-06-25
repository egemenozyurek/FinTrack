package com.profileinsight.fintrack.service;

import com.profileinsight.fintrack.dto.request.TransactionRequest;
import com.profileinsight.fintrack.dto.response.TransactionResponse;

import java.util.List;

public interface TransactionService {
    TransactionResponse create(Long userId, TransactionRequest request);
    TransactionResponse update(Long userId, Long transactionId, TransactionRequest request);
    void delete(Long userId, Long transactionId);
    TransactionResponse getById(Long userId, Long transactionId);
    List<TransactionResponse> getAll(Long userId);
    List<TransactionResponse> getByDateRange(Long userId, int year, int month);
};

