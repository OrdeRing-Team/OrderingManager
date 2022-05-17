package com.example.orderingmanager.Dto.request;

import static lombok.AccessLevel.PROTECTED;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access =PROTECTED)
public class SalesRequestDto {

    private String from; // 부터 ex) 2022-04
    private String before; // 전까지 ex) 2022-05 == 4월달 매출 조회

}