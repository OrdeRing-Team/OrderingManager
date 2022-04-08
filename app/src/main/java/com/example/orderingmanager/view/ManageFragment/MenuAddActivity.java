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
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;

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
import java.util.HashMap;
import java.util.Map;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MenuAddActivity extends BasicActivity {
    EditText edtName, edtPrice;
    Button btnSubmit;
    String name;
    String price;
    String menuIntro;
    File imageFile;

    private final int GET_GALLERY_IMAGE = 200;
    private ImageView ivMenu;
    private ActivityMenuItemBinding binding;

    RequestBody fileBody;


    Map<String, Object> manageInfo = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);
        binding = ActivityMenuItemBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        

        //뒤로가기 버튼 클릭 이벤트
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                BackWithAnim(); // 뒤로가기 슬라이드 (finish(); 포함되어 있음.)
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
                name = binding.edtName.getText().toString();
                price = binding.edtPrice.getText().toString();
                menuIntro = binding.edtIntro.getText().toString();
                if (name.length() == 0 || price.length() == 0) {
                    Toast.makeText(MenuAddActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                }

                else {
                    FoodDto foodDto = new FoodDto(name, Integer.parseInt(price), false, menuIntro);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/food/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();


                    /* Uri 타입의 파일경로를 가지는 RequestBody 객체 생성 -> 이미지를 선택하지 않는 경우에 대한 예외처리 추가 */
                    //imageView가 null이 아니면 바로 fileBody에 할당
                    if (ivMenu != null) {
                        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile);
                    }
                    //imageView가 null이면 imageView에 splash(디폴트메뉴이미지)를 넣고 bitmap에 넣어서 fileBody에 할당
                    else {
                        binding.ivMenu.setImageResource(R.drawable.splash);
                        BitmapDrawable drawable2 = (BitmapDrawable) binding.ivMenu.getDrawable();
                        Bitmap bitmap2 = drawable2.getBitmap();
                        imageFile = convertBitmapToFile(bitmap2, UserInfo.getOwnerName() + System.currentTimeMillis() + ".png");
                        fileBody = RequestBody.create(MediaType.parse("image/png"), imageFile);
                    }

                    // RequestBody로 Multipart.Part 객체 생성
                    MultipartBody.Part image = MultipartBody.Part.createFormData("image", String.valueOf(System.currentTimeMillis()), fileBody);

                    RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Long>> call = retrofitService.addFood(UserInfo.getRestaurantId(), foodDto, image);

                    call.enqueue(new Callback<ResultDto<Long>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Long>> call, retrofit2.Response<ResultDto<Long>> response) {
                            ResultDto<Long> result = response.body();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    if(result.getData() != null) {
                                        startActivity(new Intent(MenuAddActivity.this, StoreManageActivity.class));
                                        finish();
                                    }
                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Long>> call, Throwable t) {
                            Toast.makeText(MenuAddActivity.this, "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                            Log.e("e = " , t.getMessage());
                        }
                    });
                }
            }
        });
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
            Log.e("image", "imageFile is " + bitmap);
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