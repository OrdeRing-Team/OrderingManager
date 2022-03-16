package com.example.orderingmanager.Dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class VerificationDto {

    private String totalNum;
    private String code;
}
