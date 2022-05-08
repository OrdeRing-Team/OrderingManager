package com.example.orderingmanager;

import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantInfoDto;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;

import java.util.ArrayList;

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
    private static String userId;
    private static String userPW;

    private static String storeIcon;
    private static String sigMenuImg;

    // food
    private static Long foodId;
    private static String menuName;
    private static Integer menuPrice;
    private static String menuIntro;
    private static ArrayList<String> menuInfo;

    public static void setRestaurantInfo(OwnerSignInResultDto dto) {
        restaurantName = dto.getRestaurantName();
        ownerName = dto.getOwnerName();
        address = dto.getAddress();
        tableCount = dto.getTableCount();
        foodCategory = dto.getFoodCategory();
        restaurantType = dto.getRestaurantType();
    }
    public static void initRestaurantInfo(Long ownerId, RestaurantInfoDto dto) {
        ownerId = ownerId;
        restaurantName = dto.getRestaurantName();
        ownerName = dto.getOwnerName();
        address = dto.getAddress();
        tableCount = dto.getTableCount();
        foodCategory = dto.getFoodCategory();
        restaurantType = dto.getRestaurantType();
    }

    public static void setRestrauntIcon(OwnerSignInResultDto dto) {
        storeIcon = dto.getProfileImageUrl();
    }

    public static void setRestaurantSigMenu(OwnerSignInResultDto dto) {
        sigMenuImg = dto.getBackgroundImageUrl();
    }

    public static void setUserInfo(OwnerSignInResultDto dto){
        ownerId = dto.getOwnerId();
        restaurantId = dto.getRestaurantId();
    }

    public static void setUserId(String id){
        userId = id;
    }

    public static void setRestaurantId(Long id){
        restaurantId = id;
    }

    public static void setFood(FoodDto dto) {
        foodId = dto.getFoodId();
        menuName = dto.getFoodName();
        menuPrice = dto.getPrice();
        menuIntro = dto.getMenuIntro();
    }



    public static void init(){
        ownerId = null;
        restaurantName = null;
        ownerName = null;
        address = null;
        tableCount = 0;
        foodCategory = null;
        restaurantType = null;
        foodId = null;
        menuName = null;
        menuPrice = null;
        menuIntro = null;
    }
    public static String getUserId() { return userId; }

    public static String getUserPW() { return userPW; }

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

    public static String getStoreIcon() {
        return storeIcon;
    }

    public static String getSigMenuImg() {
        return sigMenuImg;
    }


    public static FoodCategory getFoodCategory() {
        return foodCategory;
    }

    public static RestaurantType getRestaurantType() {
        return restaurantType;
    }

    public static String getMenuName() { return menuName; }

    public static Long getFoodId() {
        return foodId;
    }
}