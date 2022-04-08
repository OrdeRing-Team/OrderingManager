package com.example.orderingmanager.view.QRFragment;

import android.graphics.Bitmap;

import java.util.ArrayList;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QrList {
    /* QrArrayList
    0 : TakeoutQrBitmap
    1 : WaitingQrBitmap
    2~... : TableQrBitmap */

    // 리스트에 담을 Bitmap 변수
    private Bitmap QrImage;
    // Qr Bitmap List
    private static ArrayList<Bitmap> qrArrayList;

    public QrList(ArrayList<Bitmap> qrList) {
        qrArrayList = qrList;
    }

    public static Bitmap getQrBitmap(int i) {
        return qrArrayList.get(i);
    }

    public static int getQrListSize() {
        return qrArrayList.size();
    }

    public static ArrayList<Bitmap> getQrArrayList() {
        return qrArrayList;
    }

}