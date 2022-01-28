package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;

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

public class MainActivity extends BasicActivity {

    private ActivityMainBinding binding;

    BottomNavigationView bottomNavigationView; //바텀네비뷰
    UserInfo userInfo;

    FirebaseUser user;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        user = FirebaseAuth.getInstance().getCurrentUser();
        db = FirebaseFirestore.getInstance();
        // 로그인 상태가 아닐 때 StartActivity로 이동
        // 회원 정보 입력 안된 상태
        if(user == null){
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            FinishWithAnim();
        }

        binding = ActivityMainBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        binding.progressBar.setVisibility(View.VISIBLE);

        // 유저 DB Init
        getUserDB();

        bottomNavigationView = findViewById(R.id.bottomNavi);

        //처음화면
        getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new QrFragment()).commit(); //FrameLayout에 QrFragment.xml띄우기

        //바텀 네비게이션뷰 안의 아이템 설정
        bottomNavigationView.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem menuitem) {
                switch (menuitem.getItemId()) {
                    //item 클릭 시 id값을 가져와 FrameLayout에 fragment.xml 띄우기
                    case R.id.item_qr:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new QrFragment()).commit();
                        break;
                    case R.id.item_manage:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new ManageFragment()).commit();
                        break;
                    case R.id.item_order:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new OrderFragment()).commit();
                        break;
                    case R.id.item_finish:
                        getSupportFragmentManager().beginTransaction().replace(R.id.main_frame, new FinishFragment()).commit();
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
                            showToast(MainActivity.this, "매장정보 입력 안됨!!");
                            // TODO 다이얼로그로 매장정보 입력 경고창 표시
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
}