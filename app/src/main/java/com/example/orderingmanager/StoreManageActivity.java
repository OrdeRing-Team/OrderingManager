package com.example.orderingmanager;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import de.hdodenhof.circleimageview.CircleImageView;

public class StoreManageActivity extends AppCompatActivity {

    RatingBar ratingBar;
    TextView tvScore;

    String image;
    private final int GET_GALLERY_IMAGE = 200;
    private final int GET_GALLERY_IMAGE2 = 300;
    private ImageView ivSignatureMenu;
    private CircleImageView ivStoreIcon;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_store_manage);

        //백버튼 이벤트
        ImageButton btnBack = findViewById(R.id.btn_backToManageFrag);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //별점 세팅
        tvScore = findViewById(R.id.tv_score);
        tvScore.setText("0점");
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tvScore.setText(v + "점");
            }
        });

        //툴바 타이틀 설정
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("매장명");


        //뷰페이저 세팅
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.vp_manage);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this);
        viewPager2.setAdapter(adapter);

        new TabLayoutMediator(tabLayout, viewPager2,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        if (position == 0) {
                            tab.setText("메뉴관리");
                        }
                        else {
                            tab.setText("리뷰관리");
                        }
                    }
                }).attach();


        //대표메뉴 사진 업로드
        ivSignatureMenu = findViewById(R.id.iv_sigmenu);
        ivSignatureMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        //매장 아이콘 업로드
        ivStoreIcon = findViewById(R.id.iv_storeIcon);
        ivStoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE2);
            }
        });

    }





    //대표이미지 업로드 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            ivSignatureMenu.setImageURI(selectedImageUri);

            //DB에 넣기 위한 image변수에 URI를 String으로 변경하여 넣기.
            image = selectedImageUri.toString();
        }

        else if (requestCode == GET_GALLERY_IMAGE2 && resultCode == RESULT_OK && data != null && data.getData() != null) {
            Uri selectedImageUri = data.getData();
            ivStoreIcon.setImageURI(selectedImageUri);
        }

    }

}