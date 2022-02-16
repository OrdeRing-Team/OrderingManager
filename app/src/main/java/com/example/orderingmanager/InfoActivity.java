package com.example.orderingmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.orderingmanager.databinding.ActivityInfoBinding;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

/**
 * Food Code List
 * code1 : 한식
 * code2 : 분식
 * code3 : 카페/디저트
 * code4 : 돈까스/회/초밥
 * code5 : 치킨
 * code6 : 피자
 * code7 : 아시안/양식
 * code8 : 중국집
 * code9 : 족발/보쌈
 * code10: 찜/탕
 * code11: 패스트푸드
 */

public class InfoActivity extends BasicActivity {

    //viewbinding
    private ActivityInfoBinding binding;

    RadioGroup radioGroup;
    TextView tablenumtext;
    Button btnStartApp;

    EditText inputStoreName;
    EditText inputUserName;
    EditText tablenum;

    TextView psAccord;

    Map<String, Object> storeInfo = new HashMap<>();

    boolean[] codeStatus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //setContentView(R.layout.activity_info);

        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        radioGroup = findViewById(R.id.radio_group);
        tablenumtext = findViewById(R.id.tablenumtext);
        tablenum = findViewById(R.id.tablenum);
        inputStoreName = findViewById(R.id.input_storeName);
        inputUserName = findViewById(R.id.input_userName);
        btnStartApp = findViewById(R.id.startApp);

        psAccord = findViewById(R.id.tv_psAccord);


        // 매장 식사가 가능할 때만 테이블 수 입력 받기위해 테이블 수 입력창은 가려놓는다.
        tablenumtext.setVisibility(View.GONE);
        tablenum.setVisibility(View.GONE);

        // 카테고리 선택여부 초기화
        int categotyNum = binding.constraintLayoutInScrollView.getChildCount();
        codeStatus = new boolean[categotyNum];
        for (int i = 0; i < categotyNum; i++) {
            codeStatus[i] = false;
        }

        binding.btnCode1.setOnClickListener(onClickListener);
        binding.btnCode2.setOnClickListener(onClickListener);
        binding.btnCode3.setOnClickListener(onClickListener);
        binding.btnCode4.setOnClickListener(onClickListener);
        binding.btnCode5.setOnClickListener(onClickListener);
        binding.btnCode6.setOnClickListener(onClickListener);
        binding.btnCode7.setOnClickListener(onClickListener);
        binding.btnCode8.setOnClickListener(onClickListener);
        binding.btnCode9.setOnClickListener(onClickListener);
        binding.btnCode10.setOnClickListener(onClickListener);
        binding.btnCode11.setOnClickListener(onClickListener);

        // 라디오 버튼 클릭 이벤트
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_onlydeliver:
                        tablenumtext.setVisibility(View.GONE);
                        tablenum.setVisibility(View.GONE);
                        break;
                    case R.id.radio_button_both:
                        tablenumtext.setVisibility(View.VISIBLE);
                        tablenum.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });


        btnStartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStartApp.setEnabled(false);
                isEmpty();
            }
        });

    }

    // 입력창이 비었거나, 비밀번호가 일치하지 않을 때 알림을 띄우는 함수
    private void isEmpty() {
        String storeName = inputStoreName.getText().toString();
        String userName = inputUserName.getText().toString();
        //String kategorie = inputKategorie.getText().toString();
        //String nicName = inputNicname.getText().toString();
        int tableNum = 0;
        if(!tablenum.getText().toString().equals("")){
            tableNum = Integer.parseInt(tablenum.getText().toString());
        }
        RadioButton radio_button_only = findViewById(R.id.radio_button_onlydeliver);
        RadioButton radio_button_both = findViewById(R.id.radio_button_both);


        /*if(storeName.matches("") || userName.matches("") || kategorie.matches("") || nicName.matches("")) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if(!psBeforeCheck.isEmpty() || !psAfterCheck.isEmpty()) {
            if (psBeforeCheck.equals(psAfterCheck) == false) {
                Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                Log.d("isEmpty", "입력칸을 모두 채워라.");
            }
        }*/
        if ((!radio_button_only.isChecked()) && (!radio_button_both.isChecked())) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            btnStartApp.setEnabled(true);
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        } else if (radio_button_both.isChecked() && (tableNum == 0)) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            btnStartApp.setEnabled(true);
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        /*else if(psBeforeCheck.isEmpty() == true || psAfterCheck.isEmpty() == true) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }*/
        else if(!codeStatus[0] && !codeStatus[1] && !codeStatus[2] && !codeStatus[3] && !codeStatus[4] && !codeStatus[5] && !codeStatus[6] &&
                !codeStatus[7] && !codeStatus[8] && !codeStatus[9] && !codeStatus[10]){
            Toast.makeText(InfoActivity.this, "음식 카테고리를 1개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();// 키보드 내리기
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            btnStartApp.setEnabled(true);

        }
        else {
            Toast.makeText(InfoActivity.this, "오더링 START", Toast.LENGTH_SHORT).show();
            Log.d("isn'tEmpty", "입력칸 모두 채워짐.");

            String storeCustom;
            if(radio_button_only.isChecked()){
                storeCustom = "포장";
            }
            else {
                storeCustom = "공통";
            }
            storeInfo.put("매장명", storeName);
            storeInfo.put("사업자명", userName);
            storeInfo.put("매장종류", storeCustom);
            if(storeCustom.equals("공통")) {
                storeInfo.put("테이블 수", tableNum);
            }
            storeInfo.put("카테고리", Arrays.toString(codeStatus));

            setDB(storeInfo);

            //디비에 데이터 저장하기. (차후에 추가할 부분)
        }
    }

    private void setDB(Map<String, Object> storeInfo){
        FirebaseFirestore db = FirebaseFirestore.getInstance();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();;

        // userInfo의 매장정보 입력여부 true로 변경
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef
                .update("매장정보", true)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("매장정보입력여부", "업데이트 성공");
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("매장정보입력여부", "업데이트 실패");
                    }
                });

        // userInfo에 storeInfo 문서 추가
        db.collection("users")
                .document(user.getUid()).collection("매장정보").document().set(storeInfo)
                .addOnSuccessListener(new OnSuccessListener<Void>() {
                    @Override
                    public void onSuccess(Void aVoid) {
                        Log.e("storeInfo DB 생성", "완료");
                        startActivity(new Intent(InfoActivity.this, MainActivity.class));
                        FinishWithAnim();
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.e("storeInfo DB 생성", "실패");
                    }
                });
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_code1:
                    if (!codeStatus[0]) {
                        binding.btnCode1.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[0] = true;
                    } else {
                        binding.btnCode1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[0] = false;
                    }
                    break;
                case R.id.btn_code2:
                    if (!codeStatus[1]) {
                        binding.btnCode2.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[1] = true;
                    } else {
                        binding.btnCode2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[1] = false;
                    }
                    break;
                case R.id.btn_code3:
                    if (!codeStatus[2]) {
                        binding.btnCode3.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[2] = true;
                    } else {
                        binding.btnCode3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[2] = false;
                    }
                    break;
                case R.id.btn_code4:
                    if (!codeStatus[3]) {
                        binding.btnCode4.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[3] = true;
                    } else {
                        binding.btnCode4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[3] = false;
                    }
                    break;
                case R.id.btn_code5:
                    if (!codeStatus[4]) {
                        binding.btnCode5.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[4] = true;
                    } else {
                        binding.btnCode5.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[4] = false;
                    }
                    break;
                case R.id.btn_code6:
                    if (!codeStatus[5]) {
                        binding.btnCode6.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[5] = true;
                    } else {
                        binding.btnCode6.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[5] = false;
                    }
                    break;
                case R.id.btn_code7:
                    if (!codeStatus[6]) {
                        binding.btnCode7.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[6] = true;
                    } else {
                        binding.btnCode7.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[6] = false;
                    }
                    break;
                case R.id.btn_code8:
                    if (!codeStatus[7]) {
                        binding.btnCode8.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[7] = true;
                    } else {
                        binding.btnCode8.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[7] = false;
                    }
                    break;
                case R.id.btn_code9:
                    if (!codeStatus[8]) {
                        binding.btnCode9.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[8] = true;
                    } else {
                        binding.btnCode9.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[8] = false;
                    }
                    break;
                case R.id.btn_code10:
                    if (!codeStatus[9]) {
                        binding.btnCode10.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[9] = true;
                    } else {
                        binding.btnCode10.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[9] = false;
                    }
                    break;
                case R.id.btn_code11:
                    if (!codeStatus[10]) {
                        binding.btnCode11.setBackgroundColor(Color.parseColor("#E1695E"));
                        codeStatus[10] = true;
                    } else {
                        binding.btnCode11.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        codeStatus[10] = false;
                    }
                    break;
            }
        }
    };
}