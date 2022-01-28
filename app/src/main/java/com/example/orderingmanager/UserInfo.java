package com.example.orderingmanager;

public class UserInfo {
    private String PhoneNum;
    private String Email;
    private String Nickname;
    private boolean StoreInitInfo;


    public UserInfo(String PhoneNum, String Email, String Nickname, boolean StoreInitInfo){
        this.PhoneNum = PhoneNum;
        this.Email = Email;
        this.Nickname = Nickname;
        this.StoreInitInfo = StoreInitInfo;
    }

    public UserInfo() { }

    public String getPhoneNum(){
        return this.PhoneNum;
    }
    public void setPhoneNum(String PhoneNum){
        this.PhoneNum = PhoneNum;
    }
    public String getNickname(){
        return this.Nickname;
    }
    public void setNickname(String Nickname){
        this.Nickname = Nickname;
    }
    public String getEmail(){
        return this.Email;
    }
    public void setEmail(String Email){
        this.Email = Email;
    }
    public boolean getStoreInitInfo(){
        return this.StoreInitInfo;
    }
    public void setStoreInitInfo(boolean storeInitInfo){
        this.StoreInitInfo = StoreInitInfo;
    }


}