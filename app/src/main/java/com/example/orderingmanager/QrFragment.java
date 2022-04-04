package com.example.orderingmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.databinding.FragmentQrBinding;
import com.example.orderingmanager.databinding.ViewQrTableBinding;
import com.example.orderingmanager.databinding.ViewQrTakeoutBinding;
import com.example.orderingmanager.databinding.ViewQrWaitingBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class QrFragment extends Fragment {
    private View view;

    //viewbinding
    private FragmentQrBinding binding;
    Bundle extra;

    Boolean storeInitInfo;
    String url;

    ArrayList<QrData> qrList = new ArrayList<>();
    ArrayList<Bitmap> qrBitmapList = new ArrayList<>();
    //MultiFormatWriter multiFormatWriter;
    int table_count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            /* 이곳에 받아올 데이터를 추가하십셩 */
        }

        binding = FragmentQrBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initButtonClickListener();
        storeInfoCheck();
        if(UserInfo.getRestaurantId() != null) {
            createQrCodesByUserInfo();
        }

        return view;
    }


    private void initButtonClickListener(){
        // 버튼 기능 추가
        binding.viewErrorLoadStore.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("qrFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            //binding.refreshImageButton.setOnClickListener(onClickListener);
        }
        else{
            Log.e("qrFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.qrFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void createQrCodesByUserInfo(){
        table_count = UserInfo.getTableCount();
//        String takeout_explain = "기다리지 말고\nQR 찍고 포장 주문하세요";
//        String waiting_explain = "줄 서지 말고\nQR 찍고 대기 등록하세요";
//        String table_explain = "테이블에서\nQR 찍고 주문하세요";
        String storeName = UserInfo.getRestaurantName();
//        qrList.add(new QrData(takeout_explain, storeName, CreateTakeoutQR()));
//        qrList.add(new QrData(waiting_explain, storeName, CreateWaitingQR()));
//        for(int tableNum = 1; tableNum<=table_count; tableNum++){
//            qrList.add(new QrData(table_explain, storeName, CreateTableQR(tableNum)));
//        }
        for(int i = 0; i<table_count+2;i++){
            switch(i){
                case 0:
                    qrBitmapList.add(convert2Bitmap(i,CreateTakeoutQR(),storeName));
                    break;
                case 1:
                    qrBitmapList.add(convert2Bitmap(i,CreateWaitingQR(),storeName));
                    break;
                default:
                    qrBitmapList.add(convert2Bitmap(i,CreateTableQR(i-2),storeName));
                    break;
            }
        }

//        RecyclerView recyclerView = binding.rvQrcode;
//        QrAdapter qrAdapter = new QrAdapter(qrList, getActivity());
//        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity(),LinearLayoutManager.HORIZONTAL, false)) ;
//        recyclerView.setAdapter(qrAdapter);
    }

    // 해당하는 QR 레이아웃의 view를 Bitmap으로 변환하여 반환
    @SuppressLint("SetTextI18n")
    private Bitmap convert2Bitmap(int listNum, Bitmap qrImage, String storeName){
        Bitmap captureView;
        ConstraintLayout qrContainer;
        switch (listNum){
            case 0:
                ViewQrTakeoutBinding qrTakeoutBinding;
                qrTakeoutBinding = ViewQrTakeoutBinding.inflate(getLayoutInflater());
                qrTakeoutBinding.ivQrcoderv.setImageBitmap(qrImage);
                qrTakeoutBinding.tvQrStoreName.setText(storeName);
                qrContainer = getView().findViewById(R.id.view_qr_takeout_inner);
                captureView = qrContainer.getDrawingCache();
                break;
            case 1:
                ViewQrWaitingBinding qrWaitingBinding;
                qrWaitingBinding = ViewQrWaitingBinding.inflate(getLayoutInflater());
                qrWaitingBinding.ivQrcoderv.setImageBitmap(qrImage);
                qrWaitingBinding.tvQrStoreName.setText(storeName);
                qrContainer = getView().findViewById(R.id.view_qr_waiting_inner);
                captureView = qrContainer.getDrawingCache();
                break;
            default:
                ViewQrTableBinding qrTableBinding;
                qrTableBinding = ViewQrTableBinding.inflate(getLayoutInflater());
                qrTableBinding.tvTableNum.setText(listNum-1 + "번 테이블");
                qrTableBinding.ivQrcoderv.setImageBitmap(qrImage);
                qrTableBinding.tvQrStoreName.setText(storeName);
                qrContainer = getView().findViewById(R.id.view_qr_table_inner);
                captureView = qrContainer.getDrawingCache();
                break;
        }
        return captureView;
    }

    private Bitmap CreateTakeoutQR(){
        url = "http://www.ordering.ml/"+ UserInfo.getRestaurantId() +"/takeout";
        try{
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE,250,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Log.e("takeout qr ","성공");
            return bitmap;
        }catch (Exception e){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            Log.e("takeout qr ",e.toString());
            Log.e("url = ",url);
            return bitmap;
        }

//        final Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);;
//        new Handler().postDelayed(new Runnable(){
//            @Override
//            public void run(){
//                url = "http://www.ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/takeout";
//                try{
//                    BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE,250,250);
//                    BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
//                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                        @Override
//                        public void run() {
//                            bitmap = barcodeEncoder.createBitmap(bitMatrix);
//                            Log.e("takeout qr ","성공");
//                        }
//                    });
//                }catch (Exception e){
//                    bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
//                    Log.e("takeout qr ",e.toString());
//                    Log.e("url = ",url);
//                }
//            }
//        },200);
//        return bitmap;
    }

    private Bitmap CreateWaitingQR(){
        url = "http://www.ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/waiting";
        try{
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE,250,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            return bitmap;
        }catch (Exception e){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            return bitmap;
        }
    }

    private Bitmap CreateTableQR(int i){
        url = "http://ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/table" + i;
        try{
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE,250,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            return bitmap;
        }catch (Exception e){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            return bitmap;
        }
    }
}