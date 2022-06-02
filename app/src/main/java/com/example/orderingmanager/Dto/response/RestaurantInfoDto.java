package com.example.orderingmanager.Dto.response;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import static lombok.AccessLevel.PROTECTED;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class RestaurantInfoDto {

    private String notice;
    private double latitude;

    public String getNotice() {
        return notice;
    }

    private double longitude;
}