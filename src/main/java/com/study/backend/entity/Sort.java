package com.study.backend.entity;


import com.fasterxml.jackson.annotation.JsonBackReference;
import com.study.backend.user.User;
import jakarta.persistence.*;

import jakarta.validation.constraints.Positive;
import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
@Entity
@Table(name = "sorts")
public class Sort {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne(fetch = FetchType.LAZY)
    @JsonBackReference
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;

    @Column(nullable = false)
    @Positive(message = "Quantity must be greater than zero")
    private int quantity;
}
