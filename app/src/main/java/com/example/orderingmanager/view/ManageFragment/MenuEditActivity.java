package com.example.orderingmanager.view.ManageFragment;

import static com.example.orderingmanager.Util.Utility.showToast;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingmanager.R;

public class MenuEditActivity extends AppCompatActivity {
    EditText edtName, edtPrice;
    Button btnSubmit;
    String name;
    String price;
    private final int GET_GALLERY_IMAGE = 200;
    private ImageView ivMenu;

    //뒤로가기 버튼
    ImageButton btnBack;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_item);

        edtName = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);


        name = getIntent().getStringExtra("name");
        price = getIntent().getStringExtra("price");

        edtName.setText(name);
        edtPrice.setText(String.valueOf(price));


        //뒤로가기 버튼 클릭 이벤트
        btnBack = findViewById(R.id.btn_back);
        btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                if (isFinishing()) {
                    // 이 화면은 오른쪽에서 왼쪽으로 슬라이딩 하면서 사라집니다.
                    overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_none);
                }
            }
        });

        //메뉴사진 업로드
        ivMenu = findViewById(R.id.iv_menu);
        ivMenu.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {

                Intent intent = new Intent(Intent.ACTION_PICK);
                intent. setDataAndType(android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI, "image/*");
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
                    Intent intent = new Intent(getApplicationContext(), StoreManageActivity.class);
                    intent.putExtra("edit", true);
                    intent.putExtra("name", name);
                    intent.putExtra("price", price);
                    intent.putExtra("position", getIntent().getIntExtra("position", -1));
                    startActivity(intent);
                    finish();
                }
                else {
                    showToast(MenuEditActivity.this,"빈 칸을 모두 입력해주세요.");
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

        }

    }
}
