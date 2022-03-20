package com.example.orderingmanager;

import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantDto;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class UserInfo {

    private static Long ownerId;
    private static Long restaurantId;
    private static String restaurantName;
    private static String ownerName;
    private static String address;
    private static int tableCount;
    private static FoodCategory foodCategory;
    private static RestaurantType restaurantType;

    public static void setRestaurantInfo(OwnerSignInResultDto dto) {
        restaurantName = dto.getRestaurantName();
        ownerName = dto.getOwnerName();
        address = dto.getAddress();
        tableCount = dto.getTableCount();
        foodCategory = dto.getFoodCategory();
        restaurantType = dto.getRestaurantType();
    }
    public static void initRestaurantInfo(RestaurantDto dto) {
        ownerId = dto.getOwnerId();
        restaurantName = dto.getRestaurantName();
        ownerName = dto.getOwnerName();
        address = dto.getAddress();
        tableCount = dto.getTableCount();
        foodCategory = dto.getFoodCategory();
        restaurantType = dto.getRestaurantType();
    }

    public static void setUserInfo(OwnerSignInResultDto dto){
        ownerId = dto.getOwnerId();
        restaurantId = dto.getRestaurantId();
    }

    public static void setRestaurantId(Long id){
        restaurantId = id;
    }
    public static void init(){

        ownerId = null;
        restaurantName = null;
        ownerName = null;
        address = null;
        tableCount = 0;
        foodCategory = null;
        restaurantType = null;
    }

    public static Long getOwnerId() {
        return ownerId;
    }

    public static Long getRestaurantId() {
        return restaurantId;
    }

    public static String getRestaurantName() {
        return restaurantName;
    }

    public static String getOwnerName() {
        return ownerName;
    }

    public static String getAddress() {
        return address;
    }

    public static int getTableCount() {
        return tableCount;
    }

    public static FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public static RestaurantType getRestaurantType() {
        return restaurantType;
    }
}