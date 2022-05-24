package com.example.orderingmanager.Dto.request;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantInfoDto {

    private String restaurantName;
    private String ownerName;
    private String address;
    private int tableCount;
    private FoodCategory foodCategory;
    private RestaurantType restaurantType;
    private Integer admissionWaitingTime;
    private Integer orderingWaitingTime;
}