package com.example.orderingmanager.Dto;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ResultDto<T> {

    private int size;
    private T data;

    public T getData(){
        return data;
    }
}
