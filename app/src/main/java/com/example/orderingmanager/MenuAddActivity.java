package com.example.orderingmanager;

import static com.example.orderingmanager.Utillity.showToast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class MenuAddActivity extends BasicActivity {
    EditText edtName, edtPrice;
    Button btnSubmit;
    String name;
    String price;
    String image;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView ivMenu;

    //뒤로가기 버튼
    ImageButton btnBack;

    Map<String, Object> manageInfo = new HashMap<>();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);

        edtName = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);


        //뒤로가기 버튼 클릭 이벤트
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuManageActivity.class));
                BackWithAnim(); // 뒤로가기 슬라이드 (finish(); 포함되어 있음.)
            }
        });

        //메뉴사진 업로드
        ivMenu = findViewById(R.id.iv_menu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent.setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
                startActivityForResult(intent, GET_GALLERY_IMAGE);
            }
        });


        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtName.getText().toString();
                price = edtPrice.getText().toString();
                if (name.length() > 0 && price.length() > 0) {

                    //DB에 메뉴 추가
                    manageInfo.put("메뉴명", name);
                    manageInfo.put("메뉴가격", price);
                    manageInfo.put("메뉴사진", image);

                    setDB(manageInfo);

//                    //MenuManageActivity의 리사이클러뷰에 메뉴 추가
//                    Intent intent = new Intent(getApplicationContext(), MenuManageActivity.class);
//                    intent.putExtra("new", true);
//                    intent.putExtra("name", name);
//                    intent.putExtra("price", price);
//                    startActivity(intent);
//                    finish();

                    /* MenuManageActivity의 리사이클러뷰에 DB에 저장된 메뉴 출력 */

                } else {
                    showToast(MenuAddActivity.this, "빈 칸을 모두 입력해주세요.");
                }
            }
        });


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
                        startActivity(new Intent(MenuAddActivity.this, MenuManageActivity.class));
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