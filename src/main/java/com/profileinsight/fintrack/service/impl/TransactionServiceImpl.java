package com.profileinsight.fintrack.service.impl;

import com.profileinsight.fintrack.dto.request.TransactionRequest;
import com.profileinsight.fintrack.dto.response.TransactionResponse;
import com.profileinsight.fintrack.entity.Category;
import com.profileinsight.fintrack.entity.Transaction;
import com.profileinsight.fintrack.entity.User;
import com.profileinsight.fintrack.exception.BusinessException;
import com.profileinsight.fintrack.exception.ResourceNotFoundException;
import com.profileinsight.fintrack.repository.CategoryRepository;
import com.profileinsight.fintrack.repository.TransactionRepository;
import com.profileinsight.fintrack.repository.UserRepository;
import com.profileinsight.fintrack.service.TransactionService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDate;
import java.util.List;

@Service
@RequiredArgsConstructor
public class TransactionServiceImpl implements TransactionService {

    private final TransactionRepository transactionRepository;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    @Transactional
    public TransactionResponse create(Long userId, TransactionRequest request) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("Kullanıcı", userId));

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", request.getCategoryId()));

        // İş kuralı: EXPENSE işlem sadece EXPENSE kategorisine atanabilir
        if (!category.getType().equals(request.getType())) {
            throw new BusinessException(
                    "İşlem tipi (" + request.getType() + ") ile kategori tipi (" +
                            category.getType() + ") uyuşmuyor."
            );
        }

        Transaction transaction = Transaction.builder()
                .amount(request.getAmount())
                .type(request.getType())
                .transactionDate(request.getTransactionDate())
                .note(request.getNote())
                .receiptImageUrl(request.getReceiptImageUrl())
                .isRecurring(request.isRecurring())
                .category(category)
                .user(user)
                .build();

        Transaction saved = transactionRepository.save(transaction);
        return toResponse(saved);
    }

    @Override
    @Transactional
    public TransactionResponse update(Long userId, Long transactionId, TransactionRequest request) {
        Transaction transaction = getOwnedTransaction(userId, transactionId);

        Category category = categoryRepository.findById(request.getCategoryId())
                .orElseThrow(() -> new ResourceNotFoundException("Kategori", request.getCategoryId()));

        if (!category.getType().equals(request.getType())) {
            throw new BusinessException("İşlem tipi ile kategori tipi uyuşmuyor.");
        }

        transaction.setAmount(request.getAmount());
        transaction.setType(request.getType());
        transaction.setTransactionDate(request.getTransactionDate());
        transaction.setNote(request.getNote());
        transaction.setReceiptImageUrl(request.getReceiptImageUrl());
        transaction.setRecurring(request.isRecurring());
        transaction.setCategory(category);

        // @Transactional sayesinde save() çağırmana gerek yok —
        // method biterken Hibernate dirty checking ile otomatik kaydeder.
        // Ama açıkça yazmak okunabilirliği artırır.
        return toResponse(transactionRepository.save(transaction));
    }

    @Override
    @Transactional
    public void delete(Long userId, Long transactionId) {
        Transaction transaction = getOwnedTransaction(userId, transactionId);
        transactionRepository.delete(transaction);
    }

    @Override
    @Transactional(readOnly = true)  // readOnly: Hibernate snapshot almaz, biraz daha hızlı
    public TransactionResponse getById(Long userId, Long transactionId) {
        return toResponse(getOwnedTransaction(userId, transactionId));
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getAll(Long userId) {
        return transactionRepository.findByUserIdOrderByTransactionDateDesc(userId)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    @Override
    @Transactional(readOnly = true)
    public List<TransactionResponse> getByDateRange(Long userId, int year, int month) {
        LocalDate start = LocalDate.of(year, month, 1);
        LocalDate end = start.withDayOfMonth(start.lengthOfMonth());

        return transactionRepository.findByUserAndDateRange(userId, start, end)
                .stream()
                .map(this::toResponse)
                .toList();
    }

    // ---- Yardımcı metotlar ----

    /**
     * Başkasının işlemine erişimi engeller.
     * Her CRUD operasyonundan önce bu kontrolü yapıyoruz.
     */
    private Transaction getOwnedTransaction(Long userId, Long transactionId) {
        Transaction transaction = transactionRepository.findById(transactionId)
                .orElseThrow(() -> new ResourceNotFoundException("İşlem", transactionId));

        if (!transaction.getUser().getId().equals(userId)) {
            // 403 gibi görünse de 404 döndürmek daha iyi pratik —
            // başkasının kaydının varlığını bile açıklamamış olursun.
            throw new ResourceNotFoundException("İşlem", transactionId);
        }
        return transaction;
    }

    private TransactionResponse toResponse(Transaction t) {
        return TransactionResponse.builder()
                .id(t.getId())
                .amount(t.getAmount())
                .type(t.getType())
                .transactionDate(t.getTransactionDate())
                .note(t.getNote())
                .receiptImageUrl(t.getReceiptImageUrl())
                .isRecurring(t.isRecurring())
                .createdAt(t.getCreatedAt())
                .categoryId(t.getCategory().getId())
                .categoryName(t.getCategory().getName())
                .categoryIcon(t.getCategory().getIcon())
                .categoryColor(t.getCategory().getColor())
                .build();
    }
}
