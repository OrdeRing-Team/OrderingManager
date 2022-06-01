package com.example.orderingmanager.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingmanager.Dto.request.RestaurantDataWithImgUrlDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class OwnerSignInResultDto extends RestaurantDataWithImgUrlDto {

    private Long ownerId;
    private Long restaurantId;
}