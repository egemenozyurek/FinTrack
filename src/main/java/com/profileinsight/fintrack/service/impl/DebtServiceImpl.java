package com.profileinsight.fintrack.service.impl;

import com.profileinsight.fintrack.dto.request.DebtPaymentRequest;
import com.profileinsight.fintrack.dto.request.DebtRequest;
import com.profileinsight.fintrack.dto.response.DebtResponse;
import com.profileinsight.fintrack.entity.Debt;
import com.profileinsight.fintrack.entity.DebtPayment;
import com.profileinsight.fintrack.entity.User;
import com.profileinsight.fintrack.exception.BusinessException;
import com.profileinsight.fintrack.exception.ResourceNotFoundException;
import com.profileinsight.fintrack.repository.DebtPaymentRepository;
import com.profileinsight.fintrack.repository.DebtRepository;
import com.profileinsight.fintrack.repository.UserRepository;
import com.profileinsight.fintrack.service.DebtService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class DebtServiceImpl implements DebtService {

    private final DebtRepository debtRepository;
    private final DebtPaymentRepository debtPaymentRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public DebtResponse create(Long userId, DebtRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User",userId));

        Debt debt = Debt.builder()
                .personName(request.getPersonName())
                .originalAmount(request.getAmount())
                .remainingAmount(request.getAmount())
                .type(request.getType())
                .dueDate(request.getDueDate())
                .note(request.getNote())
                .user(user)
                .build();
        return toResponse(debtRepository.save(debt));
    }

    @Override
    @Transactional
    public DebtResponse addPayment(Long userId, Long debtId, DebtPaymentRequest request) {
        Debt debt = getOwnedDebt(userId, debtId);

        if (debt.getRemainingAmount().compareTo(request.getAmount()) < 0 ){
            throw new BusinessException(
                    "The payment  amount (" + request.getAmount() +
                            ") cannot exceed the (" + debt.getRemainingAmount() + ") outstanding balance."
            );
        }

        DebtPayment payment = DebtPayment.builder()
                .amount(request.getAmount())
                .paidAt(request.getPaidAt())
                .note(request.getNote())
                .debt(debt)
                .build();

        debtPaymentRepository.save(payment);

        debt.applyPayment(request.getAmount());
        debt.getPayments().add(payment);
        debtRepository.save(debt);

        return toResponse(debt);
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebtResponse> getAll(Long userId) {
        return debtRepository.findByUserIdWithPayments(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<DebtResponse> getOverdue(Long userId) {
        return debtRepository.findOverdue(userId, LocalDate.now())
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional
    public void delete(Long userId, Long debtId) {
        debtRepository.delete(getOwnedDebt(userId, debtId));
    }

    private Debt getOwnedDebt(Long userId, Long debtId) {
        Debt debt = debtRepository.findById(debtId)
                .orElseThrow(() -> new ResourceNotFoundException("Debt", debtId));
        if (!debt.getUser().getId().equals(userId)) {
            throw new ResourceNotFoundException("Debt", debtId);
        }
        return debt;
    }

    private DebtResponse toResponse(Debt d) {
        List<DebtResponse.PaymentResponse> payments = d.getPayments().stream()
                .map(p -> DebtResponse.PaymentResponse.builder()
                        .id(p.getId())
                        .amount(p.getAmount())
                        .paidAt(p.getPaidAt())
                        .note(p.getNote())
                        .build())
                .toList();

        return DebtResponse.builder()
                .id(d.getId())
                .personName(d.getPersonName())
                .originalAmount(d.getOriginalAmount())
                .remainingAmount(d.getRemainingAmount())
                .paidAmount(d.getOriginalAmount().subtract(d.getRemainingAmount()))
                .type(d.getType())
                .status(d.getStatus())
                .dueDate(d.getDueDate())
                .note(d.getNote())
                .createdAt(d.getCreatedAt())
                .payments(payments)
                .build();
    }
}
