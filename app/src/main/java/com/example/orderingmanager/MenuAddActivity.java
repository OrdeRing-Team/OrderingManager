package com.example.orderingmanager;

import android.content.Intent;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.databinding.ActivityMenuItemBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.net.URL;
import java.util.HashMap;
import java.util.Map;

import lombok.SneakyThrows;

public class MenuAddActivity extends BasicActivity {
    EditText edtName, edtPrice;
    Button btnSubmit;
    String name;
    String price;
    String image;
    String menuIntro;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView ivMenu;
    private ActivityMenuItemBinding binding;

    //뒤로가기 버튼
    ImageButton btnBack;

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
                startActivity(new Intent(getApplicationContext(), StoreManageActivity.class));
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
                    try {
                        FoodDto foodDto = new FoodDto(name, Integer.parseInt(price), false, menuIntro);

                        URL url = new URL("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/food");
                        HttpApi httpApi = new HttpApi(url, "POST");

                        new Thread() {
                            @SneakyThrows
                            public void run() {
                                String json = httpApi.requestToServer(foodDto);
                                ObjectMapper mapper = new ObjectMapper();
                                ResultDto<Long> result = mapper.readValue(json, new TypeReference<ResultDto<Long>>() {});
                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        if(result.getData() != null) {
                                            UserInfo.setFoodId(result.getData());
                                            Log.e("foodId ",result.getData().toString());
                                        }
                                    }
                                });

                            }
                        }.start();

                    } catch (Exception e) {
                        Toast.makeText(MenuAddActivity.this, "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                        Log.e("e = " , e.getMessage());
                    }
                }
            }
        });

//        binding.btnSubmit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                name = edtName.getText().toString();
//                price = edtPrice.getText().toString();
//                if (name.length() > 0 && price.length() > 0) {
//
//                    try {
//                        FoodDto foodDto = new FoodDto(name, Integer.valueOf(price));
//
//                        URL url = new URL("http://www.ordering.ml/api/owner/" + UserInfo.getOwnerId().toString()+ "/restaurant");
//                        HttpApi httpApi = new HttpApi(url, "POST");
//
//                        new Thread() {
//                            @SneakyThrows
//                            public void run() {
//                                String json = httpApi.requestToServer(foodDto);
//                                ObjectMapper mapper = new ObjectMapper();
//                                ResultDto<Long> result = mapper.readValue(json, new TypeReference<ResultDto<Long>>() {});
//                                new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if(result.getData() != null) {
//                                            //UserInfo.initRestaurantInfo(UserInfo.getOwnerId(), restaurantInfoDto);
//                                            MenuInfo.setMenuInfo(result.getData());
//                                            Log.e("restaurantId ",result.getData().toString());
//                                            createQRCodes();
//                                        }
//                                    }
//                                });
//
//                            }
//                        }.start();
//
//                    } catch (Exception e) {
//                        showToast(this,"서버 요청에 실패하였습니다.");
//                        Log.e("e = " , e.getMessage());
//                    }
//
//                } else {
//                    showToast(MenuAddActivity.this, "빈 칸을 모두 입력해주세요.");
//                }
//            }
//        });


    }


    //이미지 업로드 함수
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == GET_GALLERY_IMAGE && resultCode == RESULT_OK && data != null && data.getData() != null) {

            Uri selectedImageUri = data.getData();
            ivMenu.setImageURI(selectedImageUri);

            //DB에 넣기 위한 image변수에 URI를 String으로 변경하여 넣기.
            image = selectedImageUri.toString();
        }

    }



    private void setDB(Map<String, Object> manageInfo) {
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        ;

        // userInfo에 manageInfo 문서 추가
        db.collection("users")
                .document(user.getUid()).collection("메뉴관리").document().set(manageInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("manageInfo DB 생성", "완료");
                        startActivity(new Intent(MenuAddActivity.this, StoreManageActivity.class));
                        FinishWithAnim();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("manageInfo DB 생성", "실패");
                    }
                });
    }
}