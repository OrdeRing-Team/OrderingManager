package com.example.orderingmanager.Dto.request;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantInfoWithImgUrlDto extends RestaurantInfoDto {

    private String profileImageUrl;
    private String backgroundImageUrl;
}
