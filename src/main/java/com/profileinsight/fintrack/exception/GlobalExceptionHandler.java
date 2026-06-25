package com.profileinsight.fintrack.exception;

import lombok.Builder;
import lombok.Data;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

/**
 * Tüm exception'ları bu sınıf karşılar.
 * Controller'larda try-catch yazmak zorunda kalmıyoruz.
 *
 * @RestControllerAdvice = @ControllerAdvice + @ResponseBody
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 404 — kayıt bulunamadı
    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(ResourceNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(ErrorResponse.builder()
                        .status(404)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 401 — yanlış e-posta/şifre. AuthenticationManager bunu fırlatır.
    @ExceptionHandler(org.springframework.security.authentication.BadCredentialsException.class)
    public ResponseEntity<ErrorResponse> handleBadCredentials(
            org.springframework.security.authentication.BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(ErrorResponse.builder()
                        .status(401)
                        .message("E-posta veya şifre hatalı.")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 400 — iş kuralı ihlali (örn: borç ödemesi borç miktarını aşıyor)
    @ExceptionHandler(BusinessException.class)
    public ResponseEntity<ErrorResponse> handleBusiness(BusinessException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .message(ex.getMessage())
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 400 — @Valid annotation'ından gelen validasyon hataları
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();
        ex.getBindingResult().getAllErrors().forEach(error -> {
            String field = ((FieldError) error).getField();
            String message = error.getDefaultMessage();
            errors.put(field, message);
        });

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(ErrorResponse.builder()
                        .status(400)
                        .message("Validasyon hatası")
                        .errors(errors)
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // 500 — beklenmedik her şey buraya düşer
    // Exception.class yerine RuntimeException kullanıyoruz —
    // Spring MVC'nin kendi exception'larına (NoResourceFoundException vb.)
    // müdahale etmiyoruz, Swagger da bu sayede düzgün çalışır.
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleGeneral(RuntimeException ex) {
        // Zaten yakaladığımız exception türleri buraya düşmez
        // (BusinessException, ResourceNotFoundException kendi handler'larında)
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(ErrorResponse.builder()
                        .status(500)
                        .message("Beklenmedik bir hata oluştu")
                        .timestamp(LocalDateTime.now())
                        .build());
    }

    // ---- İç sınıflar ----

    @Data
    @Builder
    public static class ErrorResponse {
        private int status;
        private String message;
        private Map<String, String> errors;
        private LocalDateTime timestamp;
    }
}