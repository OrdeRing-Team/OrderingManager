package com.example.orderingmanager.Dto.response;


import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantType;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerSignInResultDto {

    private Long ownerId;
    private Long restaurantId;
    private String restaurantName;
    private String ownerName;
    private String address;
    private int tableCount;
    private FoodCategory foodCategory;
    private RestaurantType restaurantType;
}
