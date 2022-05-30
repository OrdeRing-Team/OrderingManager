package com.example.orderingmanager.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingmanager.Dto.FoodDto;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RepresentativeMenuDto extends FoodDto {

    private Long representativeMenuId;

    public Long getRepresentativeMenuId() {
        return representativeMenuId;
    }

    public RepresentativeMenuDto(String foodName, int price, String menuIntro) {
        super(foodName, price, menuIntro);
    }
}