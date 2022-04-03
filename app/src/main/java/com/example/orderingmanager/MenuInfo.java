package com.example.orderingmanager;


import com.example.orderingmanager.Dto.FoodDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MenuInfo {
    private static Long foodId;
    private static String menuName;
    private static Integer menuPrice;
    private static String menuIntro;

    public static void setMenuInfo(FoodDto dto) {
        menuName = dto.getFoodName();
        menuPrice = dto.getPrice();
    }

    public static String getRestaurantId() { return getRestaurantId(); }

    public static void setFoodId(Long id){
        foodId = id;
    }
}
