package com.profileinsight.fintrack.controller;

import com.profileinsight.fintrack.entity.Category;
import com.profileinsight.fintrack.enums.TransactionType;
import com.profileinsight.fintrack.repository.CategoryRepository;
import lombok.Builder;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/categories")
@RequiredArgsConstructor
public class CategoryController {

    private final CategoryRepository categoryRepository;

    @GetMapping
    public ResponseEntity<List<CategoryDto>> getAll() {
        try {
            List<CategoryDto> categories = categoryRepository.findAll()
                    .stream()
                    .map(this::toDto)
                    .toList();
            return ResponseEntity.ok(categories);
        } catch (Exception e) {
            e.printStackTrace();
            throw e;
        }
    }

    @GetMapping(params = "type")
    public ResponseEntity<List<CategoryDto>> getByType(@RequestParam TransactionType type) {
        List<CategoryDto> categories = categoryRepository.findByType(type)
                .stream()
                .map(this::toDto)
                .toList();
        return ResponseEntity.ok(categories);
    }

    private CategoryDto toDto(Category c) {
        return CategoryDto.builder()
                .id(c.getId())
                .name(c.getName())
                .type(c.getType().name())
                .icon(c.getIcon())
                .color(c.getColor())
                .build();
    }

    @Data
    @Builder
    static class CategoryDto {
        private Long id;
        private String name;
        private String type;
        private String icon;
        private String color;
    }
}