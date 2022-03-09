package com.example.orderingmanager;

public class ManageData {

    private int iv_menu;
    private String tv_name;
    private String tv_price;

    public ManageData(String tv_name, String tv_price) {
        //this.iv_menu = iv_menu;
        this.tv_name = tv_name;
        this.tv_price = tv_price;
    }

    public int getIv_menu() {
        return iv_menu;
    }

    public void setIv_menu(int iv_menu) {
        this.iv_menu = iv_menu;
    }

    public String getTv_name() {
        return tv_name;
    }

    public void setTv_name(String tv_name) {
        this.tv_name = tv_name;
    }

    public String getTv_price() {
        return tv_price;
    }

    public void setTv_price(String tv_price) { this.tv_price = tv_price; }


}
