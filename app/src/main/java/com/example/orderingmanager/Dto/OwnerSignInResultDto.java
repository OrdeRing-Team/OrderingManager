package com.example.orderingmanager.Dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class OwnerSignInResultDto {

    private Long ownerId;
    private Long restaurantId;
}