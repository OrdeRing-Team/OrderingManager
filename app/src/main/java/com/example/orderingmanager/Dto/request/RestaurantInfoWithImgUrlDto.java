package com.example.orderingmanager.Dto.request;

import static lombok.AccessLevel.PROTECTED;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@NoArgsConstructor(access =PROTECTED)
public class RestaurantInfoWithImgUrlDto extends RestaurantInfoDto {

    private String profileImageUrl;
    private String backgroundImageUrl;

    public RestaurantInfoWithImgUrlDto(RestaurantInfoDto restaurant) {
        super(restaurant.getRestaurantName(), restaurant.getOwnerName(), restaurant.getAddress(),
                restaurant.getTableCount(), restaurant.getFoodCategory(), restaurant.getRestaurantType());

//        this.profileImageUrl = restaurant.getProfileImageUrl();
//        this.backgroundImageUrl = restaurant.getBackgroundImageUrl();
    }
}
