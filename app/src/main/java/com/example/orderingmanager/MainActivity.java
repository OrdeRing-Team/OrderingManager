package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.android.material.bottomnavigation.BottomNavigationView;

public class MainActivity extends BasicActivity {

    BottomNavigationView bottomNavigationView; //바텀네비뷰

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_guide);
        (findViewById(R.id.button_right_in)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GuidesActivity.class);
                startActivity(intent);
            }
        });
        ImageView eggGif = (ImageView)findViewById(R.id.gif_egg);
        Glide.with(this).load(R.raw.egg).into(eggGif);

        //임시 주석 처리 - 로그인기능 완성 시 주석제거할 예정
        /*setContentView(R.layout.activity_main);

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
        });*/

    }
}