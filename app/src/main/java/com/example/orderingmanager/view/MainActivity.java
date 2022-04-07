package com.example.orderingmanager.view;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.Dialog.CustomDialogStoreCheck;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityMainBinding;
import com.example.orderingmanager.view.FinishFragment.FinishFragment;
import com.example.orderingmanager.view.ManageFragment.ManageFragment;
import com.example.orderingmanager.view.OrderFragment.OrderFragment;
import com.example.orderingmanager.view.QRFragment.QrFragment;
import com.example.orderingmanager.view.QRFragment.QrList;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;

public class MainActivity extends BasicActivity {

    private static final String TAG = "MainActivity_TAG";

    public static final int QRFRAGMENT = 1111;
    public static final int MANAGEFRAGMENT = 2222;
    public static final int ORDERFRAGMENT = 3333;
    public static final int FINISHFRAGMENT = 4444;

    private ActivityMainBinding binding;
    private CustomDialogStoreCheck dialog;

    BottomNavigationView bottomNavigationView; //바텀네비뷰

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해시키 받아오는 함수 호출
        Log.e("getKeyHash", ""+getKeyHash(MainActivity.this));
        bundle = new Bundle();

        // 유저 DB Init
        getUserDB();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.VISIBLE);
        bottomNavigationView = findViewById(R.id.bottomNavi);

        initFragView();
    }

    private void initFragView(){
        // DB에서 데이터를 받아오는 시간이 있기 때문에
        // 아래의 핸들러로 코드 실행 시간을 지연 시키지 않으면
        // StoreInitinfo가 false로 처리되는 현상이 발생함.
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                //처음화면
                if(bundle != null){
                    Fragment qrFragment = new QrFragment();
                    qrFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame, qrFragment).commit(); //FrameLayout에 QrFragment.xml띄우기
                } else{
                    getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new QrFragment()).commit(); //FrameLayout에 QrFragment.xml띄우기
                }
            }
        },1000);


        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {
                    //item 클릭 시 id값을 가져와 FrameLayout에 fragment.xml 띄우기
                    case R.id.item_qr:
                        Fragment qrFragment = new QrFragment();
                        qrFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, qrFragment).commit();
                        break;
                    case R.id.item_manage:
                        Fragment manageFragment = new ManageFragment();
                        manageFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, manageFragment).commit();
                        break;
                    case R.id.item_order:
                        Fragment orderFragment = new OrderFragment();
                        orderFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, orderFragment).commit();
                        break;
                    case R.id.item_finish:
                        Fragment finishFragment = new FinishFragment();
                        finishFragment.setArguments(bundle);
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, finishFragment).commit();
                        break;
                }
                return true;
            }
        });
    }

    private void getUserDB(){
        //Intent intent = getIntent();
        if(UserInfo.getRestaurantId() == null){
            showCustomDialog();
        }
        else{
            Log.e(TAG,"매장정보입력 : true");
            initQrList();
        }

    }

    private void showCustomDialog(){
        dialog = new CustomDialogStoreCheck(
                this,
                "등록된 매장이 없습니다.",
                "이 서비스를 이용하기 위해서는 매장 등록이 필요합니다.\n지금 매장을 등록하시겠습니까?",
                "매장 등록하기","닫기",
                positiveButton,negativeButton);

        dialog.show();
    }

    private final View.OnClickListener positiveButton = view -> {
        dialog.dismiss();
        startActivity(new Intent(MainActivity.this, InfoActivity.class));
        FinishWithAnim();
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };

    private void initQrList(){
        // MainActivity가 실행되면 QrList를 초기화한 뒤 UserInfo에 입력된 tableCount를 가져오고
        // QrList에 포장Qr,웨이팅Qr,테이블Qr Bitmap을 저장한다.
        // static 클래스에 저장되기 때문에 앱이 실행종료 되기전까지 리스트는 유효함
        // 어디서는 QrList를 불러올 수 있음

        int tableCount = UserInfo.getTableCount();
        ArrayList<Bitmap> qrArrayList = new ArrayList<>();
        qrArrayList.add(CreateTakeoutQR());
        qrArrayList.add(CreateTakeoutQR());

        if(tableCount != 0) {
            for (int i = 1; i < tableCount + 1; i++) {
                qrArrayList.add(CreateTableQR(i));
            }
        }

        QrList qrList = new QrList(qrArrayList);
    }

    //해시 키 값 구하기
    public static String getKeyHash(final Context context) {
        PackageManager pm = context.getPackageManager();
        try {
            PackageInfo packageInfo = pm.getPackageInfo(context.getPackageName(), PackageManager.GET_SIGNATURES);
            if (packageInfo == null)
                return null;

            for (Signature signature : packageInfo.signatures) {
                try {
                    MessageDigest md = MessageDigest.getInstance("SHA");
                    md.update(signature.toByteArray());
                    return android.util.Base64.encodeToString(md.digest(), android.util.Base64.NO_WRAP);
                } catch (NoSuchAlgorithmException e) {
                    e.printStackTrace();
                }
            }
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return null;
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

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        super.onActivityResult(requestCode, resultCode, intent);

//        if (resultCode == RESULT_OK) {
//            Log.e(TAG,"resultokkkkkkkkkkkkkkk으아ㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏㅏ");
//            switch (requestCode) {
//                case QRFRAGMENT:
//                    Fragment qrFragment = new QrFragment();
//                    qrFragment.setArguments(bundle);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, qrFragment).commit();
//                    break;
//                case MANAGEFRAGMENT:
//                    Fragment manageFragment = new ManageFragment();
//                    manageFragment.setArguments(bundle);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, manageFragment).commit();
//                    break;
//                case ORDERFRAGMENT:
//                    Fragment orderFragment = new OrderFragment();
//                    orderFragment.setArguments(bundle);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, orderFragment).commit();
//                    break;
//                case FINISHFRAGMENT:
//                    Fragment finishFragment = new FinishFragment();
//                    finishFragment.setArguments(bundle);
//                    getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, finishFragment).commit();
//                    break;
//            }
//        }
    }
}