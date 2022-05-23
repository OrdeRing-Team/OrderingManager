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
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityMenuItemBinding;
import com.example.orderingmanager.view.BasicActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuEditActivity extends BasicActivity {

    private final int GET_GALLERY_IMAGE = 200;
    private ActivityMenuItemBinding binding;
    File imageFile;

    ImageView ivMenu;
    Long foodId;

    RequestBody fileBody;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        binding = ActivityMenuItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setData();

        //뒤로가기 버튼 클릭 이벤트
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MenuEditActivity.this, StoreManageActivity.class));
                BackWithAnim();
            }
        });

        //메뉴사진 업로드
        binding.ivMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });


        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String menuName = binding.edtName.getText().toString();
                String menuPrice = binding.edtPrice.getText().toString();
                String menuIntro = binding.edtIntro.getText().toString();
                if (menuName.length() == 0 || menuPrice.length() == 0) {
                    Toast.makeText(MenuEditActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                }

                else {
                    FoodDto foodDto = new FoodDto(menuName, Integer.parseInt(menuPrice), menuIntro);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/food/" + foodId + "/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();


                    /* Uri 타입의 파일경로를 가지는 RequestBody 객체 생성 */
                    Call<ResultDto<Boolean>> call;

                    //파일로 변환 과정을 거쳐서 File형으로 선언된 변수에 담긴다.
                    //이미지를 바꿨다면 file은 null이 아니고 안 바꿨으면 null이다.
                    if (imageFile == null) {
                        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                        call = retrofitService.putFood(UserInfo.getRestaurantId(), foodId, foodDto);
                    }

                    else {
                        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile);
                        MultipartBody.Part image = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), fileBody);
                        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                        call = retrofitService.putFood(UserInfo.getRestaurantId(), foodId, foodDto, image);
                    }

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, retrofit2.Response<ResultDto<Boolean>> response) {
                            ResultDto<Boolean> result = response.body();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Toast.makeText(MenuEditActivity.this, "메뉴 수정이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                                    startActivity(new Intent(MenuEditActivity.this, StoreManageActivity.class));
                                    finish();
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(getApplicationContext(), "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            Log.e("e = " , t.getMessage());
                        }
                    });
                }
            }
        });
    }


    // 기존 데이터 세팅
    public void setData() {
        String name = getIntent().getStringExtra("menuName");
        String price = getIntent().getStringExtra("menuPrice");
        String intro = getIntent().getStringExtra("menuIntro");
        String imageUrl = getIntent().getStringExtra("menuImage");
        foodId = getIntent().getLongExtra("menuId", 0);
        Log.e("Selected item's foodId :", foodId.toString());

        binding.edtName.setText(name);
        binding.edtPrice.setText(price);
        binding.edtIntro.setText(intro);

        if (imageUrl != null) {
            Glide.with(this).load(imageUrl).into(binding.ivMenu);
        }
        else {
            binding.ivMenu.setImageURI(null);
        }


    }

    //이미지 업로드 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            ivMenu = findViewById(R.id.iv_menu);
            Uri selectedImageUri = data.getData();
            ivMenu.setImageURI(selectedImageUri);

            BitmapDrawable drawable = (BitmapDrawable) ivMenu.getDrawable();
            Bitmap bitmap = drawable.getBitmap();

            imageFile = convertBitmapToFile(bitmap, UserInfo.getOwnerName() + System.currentTimeMillis() + ".png");
            Log.e("image", "imageFile is " + imageFile);
        }
    }

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
            Log.e("Menu Image","FileNotFoundException : " + e.getMessage());
        } catch (IOException e) {
            Log.e("Menu Image","IOException : " + e.getMessage());
        }
        return null;
    }



}
