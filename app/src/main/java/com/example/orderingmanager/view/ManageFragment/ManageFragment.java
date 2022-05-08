package com.example.orderingmanager.view.ManageFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentManageBinding;
import com.example.orderingmanager.view.MainActivity;
import com.example.orderingmanager.view.QRFragment.QrList;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;

public class ManageFragment extends Fragment {

    private View view;
    private FragmentManageBinding binding;

    Boolean storeInitInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        // 새로고침
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();

        initButtonClickListener();

        storeInfoCheck();

        if(UserInfo.getRestaurantId() != null) {
            initView();
            getStoreIcon();
        }
        return view;
    }

    private void initView() {
        // 점주용은 닉네임 없애기로 했어! 필요 없을것 같아서 ㅎ ㅎ ㅎ.......
        // 그래서 별명 자리에 매장명 크게 보이는게 좋을것 같아서 수정했숨당!
        binding.tvNikname.setText(UserInfo.getRestaurantName());

        // 매장명
        binding.tvStoreName.setText(UserInfo.getRestaurantName());

        // 사업자명
        binding.tvName.setText(UserInfo.getOwnerName());

        // 매장주소
        binding.tvAddress.setText(UserInfo.getAddress());

        // 매장종류
        RestaurantType restaurantType = UserInfo.getRestaurantType();
        switch (restaurantType) {
            case ONLY_TO_GO:
                binding.tvMealMethod.setText("포장");
                break;
            case FOR_HERE_TO_GO:
                binding.tvMealMethod.setText("매장식사, 포장");
                break;
        }
        // 카테고리
        FoodCategory foodCategory = UserInfo.getFoodCategory();
        switch (foodCategory) {
            case PIZZA: binding.tvCategory.setText("피자");break;
            case BUNSIK: binding.tvCategory.setText("분식");break;
            case CHICKEN: binding.tvCategory.setText("치킨");break;
            case KOREAN_FOOD: binding.tvCategory.setText("한식");break;
            case CAFE_DESSERT: binding.tvCategory.setText("카페/디저트");break;
            case PORK_CUTLET_ROW_FISH_SUSHI: binding.tvCategory.setText("돈가스/회/초밥");break;
            case FAST_FOOD: binding.tvCategory.setText("패스트푸드");break;
            case JJIM_TANG: binding.tvCategory.setText("찜/탕");break;
            case CHINESE_FOOD: binding.tvCategory.setText("중국집");break;
            case JOKBAL_BOSSAM: binding.tvCategory.setText("족발/보쌈");break;
            case ASIAN_FOOD_WESTERN_FOOD: binding.tvCategory.setText("아시안/양식");break;
        }
    }

    private void initButtonClickListener() {
        binding.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), StoreManageActivity.class));
                //getActivity().finish();
            }
        });

        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditPersonalInfoActivity.class));
            }
        });

        binding.btnStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditStoreInfoActivity.class);
                startActivity(intent);
                // 저장 후 화면을 갱신하기 위해 startActivityForResult 로 호출
                // 이 호출함수는 나중에 돌아오면 MainActivity 의 onActivityResult 함수 에서 받는다.
                //startActivityForResult(intent,MainActivity.MANAGEFRAGMENT);
            }
        });

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
            Log.e("ManageFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            binding.manageFragment.setVisibility(View.GONE);
        }
        else{
            Log.e("ManageFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.manageFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 정보 수정이 이루어지고 fragment 로 다시 돌아왔을때는 onResume 이 호출된다
        // 뷰를 새로 다시 세팅해준다.

        if(UserInfo.getRestaurantId() != null) {
            initView();
            initQrList();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    // 매장 아이콘 불러오기 함수
    public void getStoreIcon(){
        Log.e("매장정보액티비티의 매장아이콘", String.valueOf(UserInfo.getStoreIcon()));
        String storeIconURL = String.valueOf(UserInfo.getStoreIcon());
        Glide.with(this).load(storeIconURL).into(binding.ivStoreIcon);
    }

}