package com.example.orderingmanager.view.FinishFragment;

public class SalesData {

    private String from;
    private String before;
    private String sales;

//        public SalesData(String from, String before) {
//            this.from = from;
//            this.before = before;
//        }

    public SalesData(String sales) {
        this.sales = sales;
    }


    public String getFrom() {
            return from;
        }

    public void setFrom(String from) {
        this.from = from;
    }

    public String getBefore() {
        return before;
    }

    public void setBefore(String before) {
        this.before = before;
    }

    public String getSales() { return sales; }

    }