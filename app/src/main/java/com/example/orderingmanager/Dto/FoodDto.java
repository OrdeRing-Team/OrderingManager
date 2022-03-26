package com.example.orderingmanager.Dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class FoodDto {

    private Long foodId;
    private String foodName;
    private int price;
    private boolean soldOut;
    private String imageUrl;
}