package com.example.orderingmanager;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.os.Bundle;
import android.util.Base64;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import static com.example.orderingmanager.Utillity.showToast;

import com.example.orderingmanager.databinding.ActivityMainBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

import org.jetbrains.annotations.Nullable;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MainActivity extends BasicActivity {

    private ActivityMainBinding binding;
    private CustomDialog dialog;

    BottomNavigationView bottomNavigationView; //바텀네비뷰
    UserInfo userInfo;

    FirebaseUser user;
    FirebaseFirestore db;

    Bundle bundle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // 해시키 받아오는 함수 호출
        Log.e("getKeyHash", ""+getKeyHash(MainActivity.this));

        bundle = new Bundle();

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        // 로그인 상태가 아닐 때 StartActivity로 이동
        // 회원 정보 입력 안된 상태
        if(user == null){
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            FinishWithAnim();
        }

        // 유저 DB Init
        getUserDB();

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.VISIBLE);

        // 유저 DB Init
        getUserDB();


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
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    if (document.exists()) {
                        String PhoneNum = document.getData().get("휴대폰번호").toString();
                        String Email = document.getData().get("이메일").toString();
                        String Nickname = document.getData().get("닉네임").toString();
                        boolean StoreInitInfo = Boolean.parseBoolean(document.getData().get("매장정보").toString());
                        userInfo = new UserInfo(PhoneNum, Email, Nickname, StoreInitInfo);
                        Log.e("userInfo.setEmail", Email);
                        Log.e("userInfo.getEmail :: ", userInfo.getEmail());
                        Log.e("userInfo.setPhoneNum", PhoneNum);
                        Log.e("userInfo.getPhoneNum :: ", userInfo.getPhoneNum());
                        Log.e("userInfo.setNickname", Nickname);
                        Log.e("userInfo.getNickname :: ", userInfo.getNickname());
                        Log.e("userInfo.setStoreInfo", Boolean.toString(StoreInitInfo));
                        Log.e("userInfo.getStoreInfo :: ", Boolean.toString(userInfo.getStoreInitInfo()));
                        if(!userInfo.getStoreInitInfo()){
                            showCustomDialog();
                            bundle.putBoolean("StoreInitInfo",false);
                        }
                        else{
                            bundle.putBoolean("StoreInitInfo",true);
                        }
                    } else {
                        Log.d("docRef", "No such document");
                    }
                } else {
                    Log.d("docRef", "get failed with ", task.getException());
                }
            }
        });
    }

    private void showCustomDialog(){
        dialog = new CustomDialog(
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
}