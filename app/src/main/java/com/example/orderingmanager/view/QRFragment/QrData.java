package com.example.orderingmanager.view.QRFragment;

import android.graphics.Bitmap;

public class QrData {

    private int pos;
    private String explain;
    private String storeName;
    private Bitmap bitmap;

    public QrData(String explain, String storeName, Bitmap bitmap) {
        this.explain = explain;
        this.storeName = storeName;
        this.bitmap = bitmap;
    }

    public int getPos() {
        return pos;
    }

    public void setPos(int pos) {
        this.pos = pos;
    }

    public String getExplain() {
        return explain;
    }

    public void setExplain(String explain) {
        this.explain = explain;
    }

    public String getStoreName() {
        return storeName;
    }

    public void setTv_price(String storeName) { this.storeName = storeName; }

    public Bitmap getBitmap() {return bitmap; }

    public void setBitmap(Bitmap bitmap) { this.bitmap = bitmap; }


}