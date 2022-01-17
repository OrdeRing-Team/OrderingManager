package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends BasicActivity {

    BottomNavigationView bottomNavigationView; //바텀네비뷰



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        // 로그인 상태가 아닐 때 StartActivity로 이동
        // 회원 정보 입력 안된 상태
        if(user == null){
            startActivity(new Intent(MainActivity.this, StartActivity.class));
            Toast.makeText(MainActivity.this, "로그인 ㅎ ㅐ ~~~~~~~~~", Toast.LENGTH_SHORT).show();
            FinishWithAnim();
        }

        setContentView(R.layout.activity_main);

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
}