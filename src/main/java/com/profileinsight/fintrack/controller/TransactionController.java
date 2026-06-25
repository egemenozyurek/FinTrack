package com.profileinsight.fintrack.controller;

import com.profileinsight.fintrack.dto.request.TransactionRequest;
import com.profileinsight.fintrack.dto.response.TransactionResponse;
import com.profileinsight.fintrack.security.SecurityUtils;
import com.profileinsight.fintrack.service.TransactionService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * Controller'ın tek sorumluluğu:
 * 1. HTTP isteğini karşıla
 * 2. @Valid ile validasyonu tetikle
 * 3. Service'i çağır
 * 4. Doğru HTTP durum koduyla döndür
 *
 * Başka bir şey YOK. İş mantığı, hesaplama, veritabanı — hepsi yasak.
 *
 * userId artık JWT token'dan geliyor — JwtAuthenticationFilter token'ı
 * doğrulayıp SecurityContext'e koyuyor, biz de SecurityUtils ile okuyoruz.
 * İstemcinin başka bir kullanıcının ID'sini header'a yazıp veri çalması
 * artık mümkün değil; token'daki kimlik dışında bir yol yok.
 */
@RestController
@RequestMapping("/api/v1/transactions")
@RequiredArgsConstructor
public class TransactionController {

    private final TransactionService transactionService;
    private final SecurityUtils securityUtils;

    // POST /api/v1/transactions
    @PostMapping
    public ResponseEntity<TransactionResponse> create(
            @Valid @RequestBody TransactionRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId();
        TransactionResponse response = transactionService.create(userId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    // PUT /api/v1/transactions/{id}
    @PutMapping("/{id}")
    public ResponseEntity<TransactionResponse> update(
            @PathVariable Long id,
            @Valid @RequestBody TransactionRequest request
    ) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.update(userId, id, request));
    }

    // DELETE /api/v1/transactions/{id}
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        transactionService.delete(userId, id);
        return ResponseEntity.noContent().build();
    }

    // GET /api/v1/transactions/{id}
    @GetMapping("/{id}")
    public ResponseEntity<TransactionResponse> getById(@PathVariable Long id) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getById(userId, id));
    }

    // GET /api/v1/transactions
    @GetMapping
    public ResponseEntity<List<TransactionResponse>> getAll() {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getAll(userId));
    }

    // GET /api/v1/transactions/monthly?year=2024&month=1
    @GetMapping("/monthly")
    public ResponseEntity<List<TransactionResponse>> getByMonth(
            @RequestParam int year,
            @RequestParam int month
    ) {
        Long userId = securityUtils.getCurrentUserId();
        return ResponseEntity.ok(transactionService.getByDateRange(userId, year, month));
    }
}