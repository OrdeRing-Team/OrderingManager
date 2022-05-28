package com.example.orderingmanager.Dto.response;

import static lombok.AccessLevel.PROTECTED;

import com.example.orderingmanager.ENUM_CLASS.OrderStatus;
import com.example.orderingmanager.ENUM_CLASS.OrderType;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor(access = PROTECTED)
public class OrderPreviewDto {

    private Long orderId;
    private Integer myOrderNumber;
    private String orderSummary;
    private String checkTime;
    private String cancelOrCompletedTime;
    private String receivedTime;
    private int totalPrice;
    private OrderType orderType;
    private Integer tableNumber;
    private OrderStatus orderStatus;

    public Long getOrderId(){
        return orderId;
    }

    public Integer getMyOrderNumber(){
        return myOrderNumber;
    }

    public String getOrderSummary(){
        return orderSummary;
    }

    public String getCheckTime(){
        return checkTime;
    }

    public String getCancelOrCompletedTime(){
        return cancelOrCompletedTime;
    }

    public String getReceivedTime(){ return receivedTime; }

    public int getTotalPrice(){
        return totalPrice;
    }

    public OrderType getOrderType(){
        return orderType;
    }

    public Integer getTableNumber(){
        return tableNumber;
    }

    public OrderStatus getOrderStatus() {
        return orderStatus;
    }
}
