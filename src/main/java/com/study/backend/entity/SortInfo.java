package com.study.backend.entity;


import lombok.*;

@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@ToString
public class SortInfo {
    private Long id;
    private String name;
    private int price;
    private int quantity;
}
