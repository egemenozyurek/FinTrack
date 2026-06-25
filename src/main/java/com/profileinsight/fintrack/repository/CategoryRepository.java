package com.profileinsight.fintrack.repository;

import com.profileinsight.fintrack.entity.Category;
import com.profileinsight.fintrack.enums.TransactionType;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
    List<Category> findByType(TransactionType type);

    boolean existsByNameAndType(String name, TransactionType type);
}
