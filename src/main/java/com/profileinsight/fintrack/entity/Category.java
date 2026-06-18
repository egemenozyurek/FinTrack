package com.profileinsight.fintrack.entity;

import com.profileinsight.fintrack.enums.TransactionType;
import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "categories")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Category extends BaseEntity{

    @Column(nullable = false, length = 50)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false, length = 10)
    private TransactionType type;

    @Column(length = 50)
    private String icon;

    @Column(length = 7)
    private String color;

    @OneToMany(mappedBy = "category", cascade = CascadeType.PERSIST)
    @Builder.Default
    private List<Transaction> transactions = new ArrayList<>();
}
