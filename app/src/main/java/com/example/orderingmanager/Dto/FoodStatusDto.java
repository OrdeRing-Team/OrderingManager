package com.example.orderingmanager.Dto;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor(access =PROTECTED)

public class FoodStatusDto {

    // 불러오기
    private boolean soldOut;

    public FoodStatusDto(boolean soldOut){
        this.soldOut = soldOut;
    }

}