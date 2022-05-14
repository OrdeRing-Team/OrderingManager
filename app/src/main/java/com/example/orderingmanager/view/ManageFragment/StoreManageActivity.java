package com.example.orderingmanager.view.ManageFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.viewpager2.widget.ViewPager2;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityStoreManageBinding;
import com.example.orderingmanager.view.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreManageActivity extends AppCompatActivity {
    private ActivityStoreManageBinding binding;
    RatingBar ratingBar;
    TextView tvScore;

    private final int GET_GALLERY_IMAGE = 200;
    private final int GET_GALLERY_IMAGE2 = 300;

    File imageFile_icon;
    File imageFile_sig;
    RequestBody fileBody;

    String storeIconInUserInfo;
    String storeSigInUserInfo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoreManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        menuAdd();

        // 업로드된 아이콘 및 대표메뉴 이미지 가져오기
        getRestaurantInfo();



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
        toolbar.setTitle(UserInfo.getRestaurantName());


        //뷰페이저 세팅
        TabLayout tabLayout = findViewById(R.id.tab_layout);
        ViewPager2 viewPager2 = findViewById(R.id.vp_manage);
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 0,2);
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


        // 대표메뉴 사진 업로드
        binding.ivSigmenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        // 매장 아이콘 업로드
       binding.ivStoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE2);
            }
        });

        // 매장명 설정
        binding.tvStoreName.setText(UserInfo.getRestaurantName());
        int storeNameLength = UserInfo.getRestaurantName().length();
        // 매장명의 길이가 길 경우 textsize 조절
        if(storeNameLength > 11 && storeNameLength < 14) binding.tvStoreName.setTextSize(16.f);
        else if(storeNameLength > 13) binding.tvStoreName.setTextSize(14.f);
    }


    // 이미지 업로드 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // 대표 이미지일 경우
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            binding.ivSigmenu.setImageURI(selectedImageUri);

            BitmapDrawable drawable = (BitmapDrawable) binding.ivSigmenu.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            imageFile_sig = convertBitmapToFile(bitmap, UserInfo.getOwnerName() + System.currentTimeMillis() + ".png");
            Log.e("image", "imageFile is " + bitmap);

            // 서버에 대표메뉴 업로드
            putStoreSigMenu();


        }

        // 매장 아이콘일 경우
        else if (requestCode == GET_GALLERY_IMAGE2 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            binding.ivStoreIcon.setImageURI(selectedImageUri);

            BitmapDrawable drawable = (BitmapDrawable) binding.ivStoreIcon.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Log.e("bitmap", String.valueOf(bitmap));

            imageFile_icon = convertBitmapToFile(bitmap, UserInfo.getOwnerName() + System.currentTimeMillis() + ".png");
            Log.e("image", "imageFile is " + imageFile_icon);

            // 서버에 아이콘 업로드
            putStoreIcon();


        }

    }

    // bitmap -> file
    private File convertBitmapToFile(Bitmap bitmap, String fileName) {

        File storage = getCacheDir();
        File tempFile = new File(storage, fileName);

        try {
            tempFile.createNewFile();

            FileOutputStream out = new FileOutputStream(tempFile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 0, out);

            out.close();
            return tempFile;
        } catch (FileNotFoundException e) {
            Log.e("Image","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("Image","IOException : " + e.getMessage());
        }
        return null;
    }

    // 서버에 매장 아이콘 저장하는 함수
    private void putStoreIcon() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/profile_image/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile_icon);
        // RequestBody로 Multipart.Part 객체 생성
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), fileBody);
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<Boolean>> call = retrofitService.putStoreIcon(UserInfo.getRestaurantId(), image);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, retrofit2.Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("StoreIcon", "is uploaded.");
                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(StoreManageActivity.this, "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });
    }


    // 서버에 매장 대표 메뉴 이미지 저장하는 함수
    private void putStoreSigMenu() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/background_image/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile_sig);
        // RequestBody로 Multipart.Part 객체 생성
        MultipartBody.Part image = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), fileBody);
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<Boolean>> call = retrofitService.putStoreSigMenu(UserInfo.getRestaurantId(), image);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, retrofit2.Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("StoreSigMenu", "is uploaded.");

                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(StoreManageActivity.this, "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }


    // floating button = menu add button
    public void menuAdd() {
        binding.btnMenuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(StoreManageActivity.this, MenuAddActivity.class));
                finish();
            }
        });
    }

    //getRestaurantInfo
    private void getRestaurantInfo() {

            SignInDto signInDto = new SignInDto(UserInfo.getUserId(), UserInfo.getUserPW());

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/owner/signin/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<OwnerSignInResultDto>> call = service.ownerSignIn(signInDto);

                    call.enqueue(new Callback<ResultDto<OwnerSignInResultDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<OwnerSignInResultDto>> call, Response<ResultDto<OwnerSignInResultDto>> response) {
                            ResultDto<OwnerSignInResultDto> result = response.body();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    // 서버에 업로드된 이미지Url을 변수에 저장
                                    storeIconInUserInfo = String.valueOf(result.getData().getProfileImageUrl());
                                    storeSigInUserInfo = String.valueOf(result.getData().getBackgroundImageUrl());
                                    Log.e("getStoreIcon : ", storeIconInUserInfo + "getStoreSig : " + storeSigInUserInfo);
                                    UserInfo.setRestrauntIcon(result.getData());
                                    UserInfo.setRestaurantSigMenu(result.getData());

                                    // 서버에서 아이콘 이미지 값이 Null일 때
                                    if (UserInfo.getStoreIcon() == null) {
                                        Glide.with(StoreManageActivity.this).load(R.drawable.icon).into(binding.ivStoreIcon);
                                    }
                                    else {
                                        Glide.with(StoreManageActivity.this).load(UserInfo.getStoreIcon()).into(binding.ivStoreIcon);
                                    }

                                    // 서버에서 대표메뉴 이미지 값이 Null일 때
                                    if (UserInfo.getSigMenuImg() == null) {
                                        Glide.with(StoreManageActivity.this).load(R.drawable.splash).into(binding.ivSigmenu);
                                    }
                                    else {
                                        Glide.with(StoreManageActivity.this).load(UserInfo.getSigMenuImg()).into(binding.ivSigmenu);
                                    }
                                }


                            });

                        }

                        @Override
                        public void onFailure(Call<ResultDto<OwnerSignInResultDto>> call, Throwable t) {
                            Log.e("e = ", t.getMessage());
                        }
                    });
    }


}