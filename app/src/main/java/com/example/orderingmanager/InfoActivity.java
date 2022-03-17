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

import com.example.orderingmanager.Dto.FoodCategory;
import com.example.orderingmanager.Dto.HttpApi;
import com.example.orderingmanager.Dto.RestaurantDto;
import com.example.orderingmanager.Dto.RestaurantType;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.databinding.ActivityInfoBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

import lombok.SneakyThrows;

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

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;

    int tableNum = 0;
    String storeCustom;
    RadioGroup radioGroup;
    TextView tablenumtext;
    Button btnStartApp;
    Button btn_map;
    Button btn_search;

    EditText inputStoreName;
    EditText inputOwnerName;
    EditText tablenum;
    EditText et_address;

    TextView psAccord;

    boolean[] codeStatus;

    RestaurantType restaurantType = RestaurantType.NONE;
    FoodCategory foodCategory = FoodCategory.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        //Button btn_search = (Button) findViewById(R.id.btn_location);

        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        radioGroup = findViewById(R.id.radio_group);
        tablenumtext = findViewById(R.id.tablenumtext);
        tablenum = findViewById(R.id.tablenum);
        inputStoreName = findViewById(R.id.input_storeName);
        inputOwnerName = findViewById(R.id.input_userName);
        btnStartApp = findViewById(R.id.startApp);
        et_address = (EditText) findViewById(R.id.et_address);
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
                        restaurantType = RestaurantType.ONLY_TO_GO;
                        tablenumtext.setVisibility(View.GONE);
                        tablenum.setVisibility(View.GONE);
                        break;
                    case R.id.radio_button_both:
                        restaurantType = RestaurantType.FOR_HERE_TO_GO;
                        tablenumtext.setVisibility(View.VISIBLE);
                        tablenum.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
//

        btnStartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                btnStartApp.setEnabled(false);
                checkEmpty();
                createQRCodes();
            }
        });

        //카카오맵
//        btn_map = findViewById(R.id.btn_map);
//        btn_map.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                Intent intent = new Intent(InfoActivity.this, MapActivity.class);
//                startActivity(intent);
//            }
//        });

        //주소 api로 넘어가는 버튼 이벤
        btn_search = findViewById(R.id.btn_location);
        if (btn_search != null) {
            btn_search.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v)
                {
                    Intent i = new Intent(InfoActivity.this, WebViewActivity.class);
                    startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
                }
            });
        }

    }

    // 입력창이 비었거나, 비밀번호가 일치하지 않을 때 알림을 띄우는 함수
    private void checkEmpty() {
        String storeName = inputStoreName.getText().toString();
        String ownerName = inputOwnerName.getText().toString();
        String address = et_address.getText().toString();
        String addressDetail = binding.etAddressDetail.getText().toString();
        //String kategorie = inputKategorie.getText().toString();
        //String nicName = inputNicname.getText().toString();

        if(!tablenum.getText().toString().equals("")){
            tableNum = Integer.parseInt(tablenum.getText().toString());
        }
        RadioButton radio_button_only = findViewById(R.id.radio_button_onlydeliver);
        RadioButton radio_button_both = findViewById(R.id.radio_button_both);

        if ((!radio_button_only.isChecked()) && (!radio_button_both.isChecked())) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            btnStartApp.setEnabled(true);
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        } else if (radio_button_both.isChecked() && (tableNum == 0)) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            btnStartApp.setEnabled(true);
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if(foodCategory == FoodCategory.NONE){
            Toast.makeText(InfoActivity.this, "음식 카테고리를 1개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();// 키보드 내리기
            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
            btnStartApp.setEnabled(true);

        }
        else {
            Toast.makeText(InfoActivity.this, "오더링 START", Toast.LENGTH_SHORT).show();
            Log.d("isn'tEmpty", "입력칸 모두 채워짐.");


            try {
                RestaurantDto restaurantDto = new RestaurantDto(UserInfo.getOwnerId(),storeName,ownerName,address,tableNum, foodCategory, restaurantType);

                URL url = new URL("http://www.ordering.ml/api/restaurant");
                HttpApi httpApi = new HttpApi(url, "POST");

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        // login
                        String json = httpApi.requestToServer(restaurantDto);
                        ObjectMapper mapper = new ObjectMapper();
                        ResultDto<Long> result = mapper.readValue(json, new TypeReference<ResultDto<Long>>() {});
                        UserInfo.setRestaurantInfo(restaurantDto);

                        createQRCodes();
                    }
                }.start();

            } catch (Exception e) {
                showToast(this,"서버 요청에 실패하였습니다.");
                Log.e("e = " , e.getMessage());
            }


            /** storeInfo.put("카테고리", Arrays.toString(codeStatus)); */

            //디비에 데이터 저장하기. (차후에 추가할 부분)
        }
    }

    private void createQRCodes(){
        startActivity(new Intent(InfoActivity.this, CreateQR.class));
        FinishWithAnim();
    }


    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {

            switch (v.getId()) {
                case R.id.btn_code1:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode1.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.KOREAN_FOOD;
                    }
                    else{
                        binding.btnCode1.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code2:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode2.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.BUNSIK;
                    }
                    else{
                        binding.btnCode2.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code3:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode3.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.CAFE_DESSERT;
                    }
                    else{
                        binding.btnCode3.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code4:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode4.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.PORK_CUTLET_ROW_FISH_SUSHI;
                    }
                    else{
                        binding.btnCode4.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code5:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode5.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.CHICKEN;
                    }
                    else{
                        binding.btnCode5.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code6:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode6.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.PIZZA;
                    }
                    else{
                        binding.btnCode6.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code7:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode7.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.ASIAN_FOOD_WESTERN_FOOD;
                    }
                    else{
                        binding.btnCode7.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code8:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode8.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.CHINESE_FOOD;
                    }
                    else{
                        binding.btnCode8.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code9:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode9.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.JOKBAL_BOSSAM;
                    }
                    else{
                        binding.btnCode9.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code10:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode10.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.JJIM_TANG;
                    }
                    else{
                        binding.btnCode10.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
                case R.id.btn_code11:
                    if (foodCategory == FoodCategory.NONE) {
                        binding.btnCode11.setBackgroundColor(Color.parseColor("#E1695E"));
                        foodCategory = FoodCategory.FAST_FOOD;
                    }
                    else{
                        binding.btnCode11.setBackgroundColor(Color.parseColor("#FFFFFF"));
                        foodCategory = FoodCategory.NONE;
                    }
                    break;
            }
        }
    };

    //주소 결과값 가져오는 함수
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        String[] address = data.split(", ");
                        binding.etAddressNumber.setText(address[0]);;
                        binding.etAddress.setText(address[1]);;
                    }
                    else{
                        showToast(this,"주소를 불러오는 중 오류가 발생했습니다.");
                    }
                }
                break;
        }
    }
}