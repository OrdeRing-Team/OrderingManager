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

    // 불러오기
    private Long foodId;
    private String imageUrl;

    private String foodName;
    private int price;
    private boolean soldOut;
    private String menuIntro;

    public FoodDto(String foodName, int price, boolean soldOut, String menuIntro){
        this.foodName = foodName;
        this.price = price;
        this.soldOut = soldOut;
        this.menuIntro = menuIntro;
    }

}