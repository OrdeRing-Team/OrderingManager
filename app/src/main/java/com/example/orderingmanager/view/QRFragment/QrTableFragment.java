package com.example.orderingmanager.view.QRFragment;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentQrTableBinding;
import com.example.orderingmanager.view.MainActivity;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;


public class QrTableFragment extends Fragment {

    private View view;

    //viewbinding
    private FragmentQrTableBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_table, container, false);
        binding = FragmentQrTableBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initView();

        return view;
    }

    private void initView(){

        // 리사이클러뷰가 아래 네비바에 의해 마지막이 잘리는 경우가 발생
        // 네비바의 높이를 계산해서 그 높이만큼 마진을 준다.
        // 이렇게 하면 리사이클러뷰가 높이만큼 올라와서 마지막 끝부분까지 보이게 됨
        RelativeLayout.LayoutParams mLayoutParams = (RelativeLayout.LayoutParams) binding.rvQrTable.getLayoutParams();
        mLayoutParams.bottomMargin = MainActivity.getBottomNaviHeight();

        initQrList();

        RecyclerView recyclerView = binding.rvQrTable;
        QrCardViewAdapter qrCardViewAdapter = new QrCardViewAdapter(QrList.getQrArrayList(), getActivity());
        recyclerView.setLayoutManager(new GridLayoutManager(getActivity(), 2)) ;
        recyclerView.setAdapter(qrCardViewAdapter);
    }

    @Override
    public void onResume() {
        super.onResume();

        initView();
    }

    public void initQrList(){
        // MainActivity가 실행되면 QrList를 초기화한 뒤 UserInfo에 입력된 tableCount를 가져오고
        // QrList에 포장Qr,웨이팅Qr,테이블Qr Bitmap을 저장한다.
        // static 클래스에 저장되기 때문에 앱이 실행종료 되기전까지 리스트는 유효함
        // 어디서는 QrList를 불러올 수 있음

        int tableCount = UserInfo.getTableCount();
        ArrayList<Bitmap> qrArrayList = new ArrayList<>();
        qrArrayList.add(CreateTakeoutQR());
        qrArrayList.add(CreateWaitingQR());

        if(tableCount != 0) {
            for (int i = 1; i < tableCount + 1; i++) {
                qrArrayList.add(CreateTableQR(i));
            }
        }

        QrList qrList = new QrList(qrArrayList);
    }
    private Bitmap CreateTakeoutQR() {
        String url;
        url = "http://www.ordering.ml/" + UserInfo.getRestaurantId() + "/takeout";
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Log.e("takeout qr ", "성공");
            return bitmap;
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            Log.e("takeout qr ", e.toString());
            Log.e("url = ", url);
            return bitmap;
        }
    }
    private Bitmap CreateWaitingQR(){
        String url;
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
        String url;
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