package com.example.orderingmanager.view.ManageFragment;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Color;
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

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityStoreManageBinding;
import com.example.orderingmanager.view.ViewPagerAdapter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
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

    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityStoreManageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // floating menu add btn setting
        menuAdd();

        // ProgressDialog 생성
        creatProgress();

        // 업로드된 아이콘 및 대표메뉴 이미지 가져오기
        getRestaurantInfo();

        Log.e("매장 아이디", String.valueOf(UserInfo.getRestaurantId()));


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
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 0,2);
        binding.vpManage.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.vpManage,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        switch (position){
                            case 0:
                                tab.setText("메뉴관리");
                                break;
                            default:
                                tab.setText("리뷰관리");
                                break;
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

        progressDialog.show();
        progressDialog.setCancelable(false);  // 화면 밖 터치 시 종료되지 않게 설정

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
                        deleteProgress();
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

        progressDialog.show();
        progressDialog.setCancelable(false);  // 화면 밖 터치 시 종료되지 않게 설정

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
                        deleteProgress();

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
                                    storeIconInUserInfo = result.getData().getProfileImageUrl();
                                    storeSigInUserInfo = result.getData().getBackgroundImageUrl();
                                    Log.e("getStoreIcon", storeIconInUserInfo + "getStoreSig : " + storeSigInUserInfo);
                                    UserInfo.setRestrauntIcon(result.getData());
                                    UserInfo.setRestaurantSigMenu(result.getData());

                                    // 서버에서 아이콘 이미지 값이 Null일 때
                                    if (storeIconInUserInfo == null) {
                                        Log.e("storeIconInServer", "is null" + storeIconInUserInfo);
                                        Glide.with(StoreManageActivity.this).load(R.drawable.icon).into(binding.ivStoreIcon);
                                    }
                                    else {
                                        Log.e("storeIconInServer", "is not null" + storeIconInUserInfo);
                                        Glide.with(StoreManageActivity.this).load(storeIconInUserInfo).into(binding.ivStoreIcon);
                                    }

                                    // 서버에서 대표메뉴 이미지 값이 Null일 때
                                    if (storeSigInUserInfo == null) {
                                        Log.e("storeSigInServer", "is null" + storeSigInUserInfo);
                                        Glide.with(StoreManageActivity.this).load(R.drawable.splash).into(binding.ivSigmenu);
                                    }
                                    else {
                                        Log.e("storeSigInServer", "is not null" + storeSigInUserInfo);
                                        Glide.with(StoreManageActivity.this).load(storeSigInUserInfo).into(binding.ivSigmenu);
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

    // ProgressDialog 생성
    public void creatProgress() {
        progressDialog = new ProgressDialog(this, R.style.SpinKitViewUpload);
        progressDialog.setMessage("Image Uploading ...");
        Sprite anim = new ThreeBounce();
        anim.setColor(Color.rgb(227, 85, 85));
        progressDialog.setIndeterminateDrawable(anim);


        // 취소 버튼
//        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "취소",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.e("취소버튼", "클릭됨.");
//                    }
//                }
//        );
    }

    // ProgressDialog 없애기
    public void deleteProgress() {
        // 지연 처리
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                progressDialog.dismiss();
//                Toast.makeText(StoreManageActivity.this, "업로드되었습니다.", Toast.LENGTH_SHORT).show();
//            }
//        },3000);

        progressDialog.dismiss();
        Toast.makeText(StoreManageActivity.this, "업로드되었습니다.", Toast.LENGTH_SHORT).show();
    }

}