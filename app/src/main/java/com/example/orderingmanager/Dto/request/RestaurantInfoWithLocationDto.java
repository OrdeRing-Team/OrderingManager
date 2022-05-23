package com.example.orderingmanager.Dto.request;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingmanager.Dto.request.RestaurantInfoDto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantInfoWithLocationDto extends RestaurantInfoDto {

    private double latitude;
    private double longitude;
}