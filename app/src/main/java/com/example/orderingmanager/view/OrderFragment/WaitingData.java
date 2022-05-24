package com.example.orderingmanager.view.OrderFragment;

public class WaitingData {

    //private Long waitingId;
    private Byte numOfTeamMembers;
    private Integer waitingNum;
    private String phoneNumber;
    private String waitingRequestTime;

    public WaitingData(Integer waitingNum, Byte numOfTeamMembers, String phoneNumber, String waitingRequestTime) {
        //this.waitingId = waitingId;
        this.waitingNum = waitingNum;
        this.numOfTeamMembers = numOfTeamMembers;
        this.phoneNumber = phoneNumber;
        this.waitingRequestTime = waitingRequestTime;
    }

    //public Long getWaitinId() { return waitingId;}

    //public void setWaitingId (Long id) { this.waitingId = id; }

    public Integer getWaitingNum() { return waitingNum;}

    public void setWaitingNum(Integer id){this.waitingNum = id;}

    public Byte getNumOfTeamMembers() {
        return numOfTeamMembers;
    }

    public void setNumOfTeamMembers(Byte num) {
        this.numOfTeamMembers = num;
    }

    public String getPhoneNumber() { return phoneNumber; }

    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getWaitingRequestTime() { return waitingRequestTime; }

    public void setWaitingRequestTime(String time) { this.waitingRequestTime = time; }
}

