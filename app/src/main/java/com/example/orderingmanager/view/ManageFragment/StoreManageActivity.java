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
import com.example.orderingmanager.Dto.response.ReviewPreviewDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityStoreManageBinding;
import com.example.orderingmanager.view.ViewPagerAdapter;
import com.github.ybq.android.spinkit.sprite.Sprite;
import com.github.ybq.android.spinkit.style.ThreeBounce;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.messaging.FirebaseMessaging;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;

import lombok.SneakyThrows;
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

    public static int totalStars, oneStar, twoStars, threeStars, fourStars, fiveStars;
    public static List<ReviewPreviewDto> reviewList;
    public static float reviewTotalRating;
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

        initData();

        // floating menu add btn setting
        menuAdd();

        // ProgressDialog ??????
        createProgress();

        // ???????????? ????????? ??? ???????????? ????????? ????????????
        getRestaurantInfo();

        Log.e("?????? ?????????", String.valueOf(UserInfo.getRestaurantId()));


        //????????? ?????????
        ImageButton btnBack = findViewById(R.id.btn_backToManageFrag);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        //?????? ??????
        tvScore = findViewById(R.id.tv_score);
        tvScore.setText("0???");
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tvScore.setText(v + "???");
            }
        });

        //?????? ????????? ??????
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(UserInfo.getRestaurantName());


        //???????????? ??????
        ViewPagerAdapter adapter = new ViewPagerAdapter(this, 0, 2);
        binding.vpManage.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayout, binding.vpManage,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        //tab.setText("Tab" + (position + 1));
                        switch (position) {
                            case 0:
                                tab.setText("????????????");
                                break;
                            default:
                                tab.setText("????????????");
                                break;
                        }
                    }
                }).attach();


        // ???????????? ?????? ?????????
        binding.ivSigmenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });

        // ?????? ????????? ?????????
        binding.ivStoreIcon.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE2);
            }
        });

        // ????????? ??????
        binding.tvStoreName.setText(UserInfo.getRestaurantName());
        int storeNameLength = UserInfo.getRestaurantName().length();
        // ???????????? ????????? ??? ?????? textsize ??????
        if (storeNameLength > 11 && storeNameLength < 14) binding.tvStoreName.setTextSize(16.f);
        else if (storeNameLength > 13) binding.tvStoreName.setTextSize(14.f);
    }

    private void initData(){
        totalStars = 0;
        oneStar = 0;
        twoStars = 0;
        threeStars = 0;
        fourStars = 0;
        fiveStars = 0;
        reviewList =  null;
        reviewTotalRating = 0;

        initReviewRecyclerView();
    }

    // ????????? ????????? ??????
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);

        // ?????? ???????????? ??????
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            binding.ivSigmenu.setImageURI(selectedImageUri);

            BitmapDrawable drawable = (BitmapDrawable) binding.ivSigmenu.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            imageFile_sig = convertBitmapToFile(bitmap, UserInfo.getOwnerName() + System.currentTimeMillis() + ".png");
            Log.e("image", "imageFile is " + bitmap);

            // ????????? ???????????? ?????????
            putStoreSigMenu();
        }

        // ?????? ???????????? ??????
        else if (requestCode == GET_GALLERY_IMAGE2 && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            binding.ivStoreIcon.setImageURI(selectedImageUri);

            BitmapDrawable drawable = (BitmapDrawable) binding.ivStoreIcon.getDrawable();
            Bitmap bitmap = drawable.getBitmap();
            Log.e("bitmap", String.valueOf(bitmap));

            imageFile_icon = convertBitmapToFile(bitmap, UserInfo.getOwnerName() + System.currentTimeMillis() + ".png");
            Log.e("image", "imageFile is " + imageFile_icon);

            // ????????? ????????? ?????????
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
            Log.e("Image", "FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("Image", "IOException : " + e.getMessage());
        }
        return null;
    }

    // ????????? ?????? ????????? ???????????? ??????
    private void putStoreIcon() {

        progressDialog.show();
        progressDialog.setCancelable(false);  // ?????? ??? ?????? ??? ???????????? ?????? ??????

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/profile_image/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody ?????? ??????
        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile_icon);
        // RequestBody??? Multipart.Part ?????? ??????
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
                Toast.makeText(StoreManageActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }


    // ????????? ?????? ?????? ?????? ????????? ???????????? ??????
    private void putStoreSigMenu() {

        progressDialog.show();
        progressDialog.setCancelable(false);  // ?????? ??? ?????? ??? ???????????? ?????? ??????

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/background_image/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody ?????? ??????
        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile_sig);
        // RequestBody??? Multipart.Part ?????? ??????
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
                Toast.makeText(StoreManageActivity.this, "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
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
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("?????? ??????", "Fetching FCM registration token failed", task.getException());
                            return;
                        }

                        String token = task.getResult();

                        SignInDto signInDto = new SignInDto(UserInfo.getUserId(), UserInfo.getUserPW(), token);

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
                                        // ????????? ???????????? ?????????Url??? ????????? ??????
                                        storeIconInUserInfo = result.getData().getProfileImageUrl();
                                        storeSigInUserInfo = result.getData().getBackgroundImageUrl();
                                        Log.e("getStoreIcon", storeIconInUserInfo + "getStoreSig : " + storeSigInUserInfo);
                                        UserInfo.setRestrauntIcon(result.getData());
                                        UserInfo.setRestaurantSigMenu(result.getData());

                                        // ???????????? ????????? ????????? ?????? Null??? ???
                                        if (storeIconInUserInfo == null) {
                                            Log.e("storeIconInServer", "is null" + storeIconInUserInfo);
                                            Glide.with(StoreManageActivity.this).load(R.drawable.icon).into(binding.ivStoreIcon);
                                        } else {
                                            Log.e("storeIconInServer", "is not null" + storeIconInUserInfo);
                                            Glide.with(StoreManageActivity.this).load(storeIconInUserInfo).into(binding.ivStoreIcon);
                                        }

                                        // ???????????? ???????????? ????????? ?????? Null??? ???
                                        if (storeSigInUserInfo == null) {
                                            Log.e("storeSigInServer", "is null" + storeSigInUserInfo);
                                            Glide.with(StoreManageActivity.this).load(R.drawable.splash).into(binding.ivSigmenu);
                                        } else {
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

                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.e("token Log", msg);
                    }
                });
    }

    // ProgressDialog ??????
    public void createProgress() {
        progressDialog = new ProgressDialog(this, R.style.SpinKitViewUpload);
        progressDialog.setMessage("Image Uploading ...");
        Sprite anim = new ThreeBounce();
        anim.setColor(Color.rgb(227, 85, 85));
        progressDialog.setIndeterminateDrawable(anim);


        // ?????? ??????
//        progressDialog.setButton(ProgressDialog.BUTTON_NEGATIVE, "??????",
//                new DialogInterface.OnClickListener() {
//                    @Override
//                    public void onClick(DialogInterface dialog, int which) {
//                        Log.e("????????????", "?????????.");
//                    }
//                }
//        );
    }

    // ProgressDialog ?????????
    public void deleteProgress() {
        // ?????? ??????
//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                progressDialog.dismiss();
//                Toast.makeText(StoreManageActivity.this, "????????????????????????.", Toast.LENGTH_SHORT).show();
//            }
//        },3000);

        progressDialog.dismiss();
        Toast.makeText(StoreManageActivity.this, "????????????????????????.", Toast.LENGTH_SHORT).show();
    }


    public void setRatings(float rating, String ratingString){
        binding.ratingBar.setRating(rating);
        binding.tvScore.setText(ratingString);
    }

    private void initReviewRecyclerView(){
        try{
            new Thread(){
                @SneakyThrows
                public void run(){
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/reviews/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<ReviewPreviewDto>>> call = service.getReviewList(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<List<ReviewPreviewDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<ReviewPreviewDto>>> call, Response<ResultDto<List<ReviewPreviewDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<ReviewPreviewDto>> result;
                                result = response.body();
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {

                                        for(ReviewPreviewDto i : result.getData()){
                                            reviewTotalRating += i.getRating();
                                            switch ((int)i.getRating()){
                                                case 1:
                                                    oneStar++;
                                                    break;
                                                case 2:
                                                    twoStars++;
                                                    break;
                                                case 3:
                                                    threeStars++;
                                                    break;
                                                case 4:
                                                    fourStars++;
                                                    break;
                                                case 5:
                                                    fiveStars++;
                                                    break;
                                                default:
                                                    break;
                                            }
                                        }
                                        totalStars = oneStar + twoStars + threeStars + fourStars + fiveStars;
                                        if(result.getData().size() != 0) {
                                            reviewTotalRating /= result.getData().size();
                                        }else{
                                            reviewTotalRating = 0;
                                        }
                                        reviewList = result.getData();

                                        setRatings(reviewTotalRating, Float.toString(reviewTotalRating));
                                    }
                                });
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<ReviewPreviewDto>>> call, Throwable t) {
                            Toast.makeText(StoreManageActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(StoreManageActivity.this, "???????????? ????????? ?????????????????????.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }
}