package com.study.backend.request;


import lombok.*;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SortRaw {
    private Long user_id;
    private Long product_id;
    private int quantity;
}
