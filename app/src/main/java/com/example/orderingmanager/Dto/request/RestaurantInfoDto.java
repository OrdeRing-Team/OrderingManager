package com.example.orderingmanager.Dto.request;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor(access =PROTECTED)
@NoArgsConstructor(access =PROTECTED)
public abstract class RestaurantInfoDto {

    private String restaurantName;
    private String ownerName;
    private String address;
    private int tableCount;
    private FoodCategory foodCategory;
    private RestaurantType restaurantType;

    protected RestaurantInfoDto(RestaurantInfoDto dto) {
        this.restaurantName = dto.restaurantName;
        this.ownerName = dto.ownerName;
        this.address = dto.address;
        this.tableCount = dto.tableCount;
        this.foodCategory = dto.foodCategory;
        this.restaurantType = dto.restaurantType;
    }
}