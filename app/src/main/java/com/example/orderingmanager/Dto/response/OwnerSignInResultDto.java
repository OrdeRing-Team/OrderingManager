package com.example.orderingmanager.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingmanager.Dto.request.RestaurantInfoDto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class OwnerSignInResultDto extends RestaurantInfoDto {

    private Long ownerId;
    private Long restaurantId;
}