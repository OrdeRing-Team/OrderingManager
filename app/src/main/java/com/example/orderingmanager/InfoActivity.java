package com.example.orderingmanager;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantDto;
import com.example.orderingmanager.Dto.request.RestaurantType;
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

    boolean[] codeStatus;

    RestaurantType restaurantType = RestaurantType.NONE;
    FoodCategory foodCategory = FoodCategory.NONE;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 매장 식사가 가능할 때만 테이블 수 입력 받기위해 테이블 수 입력창은 가려놓는다.
        binding.viewActivityInfo.tablenumtext.setVisibility(View.GONE);
        binding.viewActivityInfo.tablenum.setVisibility(View.GONE);

        // 카테고리 선택여부 초기화
//        int categotyNum = binding.viewActivityInfo.constraintLayoutInScrollView.getChildCount();
//        codeStatus = new boolean[categotyNum];
//        for (int i = 0; i < categotyNum; i++) {
//            codeStatus[i] = false;
//        }

//        binding.btnCode1.setOnClickListener(onClickListener);
//        binding.btnCode2.setOnClickListener(onClickListener);
//        binding.btnCode3.setOnClickListener(onClickListener);
//        binding.btnCode4.setOnClickListener(onClickListener);
//        binding.btnCode5.setOnClickListener(onClickListener);
//        binding.btnCode6.setOnClickListener(onClickListener);
//        binding.btnCode7.setOnClickListener(onClickListener);
//        binding.btnCode8.setOnClickListener(onClickListener);
//        binding.btnCode9.setOnClickListener(onClickListener);
//        binding.btnCode10.setOnClickListener(onClickListener);
//        binding.btnCode11.setOnClickListener(onClickListener);

        // 라디오 버튼 클릭 이벤트
        binding.viewActivityInfo.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_onlydeliver:
                        restaurantType = RestaurantType.ONLY_TO_GO;
                        binding.viewActivityInfo.tablenumtext.setVisibility(View.GONE);
                        binding.viewActivityInfo.tablenum.setVisibility(View.GONE);
                        break;
                    case R.id.radio_button_both:
                        restaurantType = RestaurantType.FOR_HERE_TO_GO;
                        binding.viewActivityInfo.tablenumtext.setVisibility(View.VISIBLE);
                        binding.viewActivityInfo.tablenum.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });
//

        binding.startApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startApp.setEnabled(false);
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

        //주소 검색 버튼
        binding.viewActivityInfo.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(InfoActivity.this, WebViewActivity.class);
                startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
            }
        });
    }

    // 입력창이 비었거나, 비밀번호가 일치하지 않을 때 알림을 띄우는 함수
    private void checkEmpty() {
        String storeName = binding.viewActivityInfo.inputStoreName.getText().toString();
        String ownerName = binding.viewActivityInfo.inputUserName.getText().toString();
        String address = binding.viewActivityInfo.etAddressNumber.getText().toString() + ", "
                + binding.viewActivityInfo.etAddress.getText().toString() + ", "
                + binding.viewActivityInfo.etAddressDetail.getText().toString();
        //String kategorie = inputKategorie.getText().toString();
        //String nicName = inputNicname.getText().toString();

        if(!binding.viewActivityInfo.tablenum.getText().toString().equals("")){
            tableNum = Integer.parseInt(binding.viewActivityInfo.tablenum.getText().toString());
        }
        RadioButton radio_button_only = findViewById(R.id.radio_button_onlydeliver);
        RadioButton radio_button_both = findViewById(R.id.radio_button_both);

        // "포장만 가능" 체크되어있으면 tablenum = 0 으로 고정
        if(radio_button_only.isChecked()){
            tableNum = 0;
        }

        if ((!radio_button_only.isChecked()) && (!radio_button_both.isChecked())) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            binding.startApp.setEnabled(true);
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        } else if (!radio_button_only.isChecked() && (tableNum == 0)) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            binding.startApp.setEnabled(true);
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        /** 카테고리 개선 후 수정 **/
//        else if(foodCategory == FoodCategory.NONE){
//            Toast.makeText(EditStoreInfoActivity.this, "음식 카테고리를 1개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();// 키보드 내리기
//            InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
//            imm.hideSoftInputFromWindow(this.getCurrentFocus().getWindowToken(), 0);
//            binding.startApp.setEnabled(true);
//
//        }
        else {
            try {
                RestaurantDto restaurantDto = new RestaurantDto(UserInfo.getOwnerId(),storeName,ownerName,address,tableNum, foodCategory, restaurantType);

                URL url = new URL("http://www.ordering.ml/api/restaurant");
                HttpApi httpApi = new HttpApi(url, "POST");

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String json = httpApi.requestToServer(restaurantDto);
                        ObjectMapper mapper = new ObjectMapper();
                        ResultDto<Long> result = mapper.readValue(json, new TypeReference<ResultDto<Long>>() {});
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(result.getData() != null) {
                                    UserInfo.initRestaurantInfo(restaurantDto);
                                    createQRCodes();
                                }
                                else{
                                    showToast(InfoActivity.this,"asdasdas");
                                }
                            }
                        });

                    }
                }.start();

            } catch (Exception e) {
                showToast(this,"서버 요청에 실패하였습니다.");
                Log.e("e = " , e.getMessage());
            }
        }
    }

    private void createQRCodes(){
        startActivity(new Intent(InfoActivity.this, CreateQR.class));
        FinishWithAnim();
    }


//    View.OnClickListener onClickListener = new View.OnClickListener() {
//        @Override
//        public void onClick(View v) {
//
//            switch (v.getId()) {
//                case R.id.btn_code1:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode1.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.KOREAN_FOOD;
//                    }
//                    else{
//                        binding.btnCode1.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code2:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode2.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.BUNSIK;
//                    }
//                    else{
//                        binding.btnCode2.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code3:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode3.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.CAFE_DESSERT;
//                    }
//                    else{
//                        binding.btnCode3.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code4:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode4.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.PORK_CUTLET_ROW_FISH_SUSHI;
//                    }
//                    else{
//                        binding.btnCode4.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code5:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode5.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.CHICKEN;
//                    }
//                    else{
//                        binding.btnCode5.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code6:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode6.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.PIZZA;
//                    }
//                    else{
//                        binding.btnCode6.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code7:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode7.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.ASIAN_FOOD_WESTERN_FOOD;
//                    }
//                    else{
//                        binding.btnCode7.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code8:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode8.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.CHINESE_FOOD;
//                    }
//                    else{
//                        binding.btnCode8.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code9:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode9.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.JOKBAL_BOSSAM;
//                    }
//                    else{
//                        binding.btnCode9.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code10:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode10.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.JJIM_TANG;
//                    }
//                    else{
//                        binding.btnCode10.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//                case R.id.btn_code11:
//                    if (foodCategory == FoodCategory.NONE) {
//                        binding.btnCode11.setBackgroundColor(Color.parseColor("#E1695E"));
//                        foodCategory = FoodCategory.FAST_FOOD;
//                    }
//                    else{
//                        binding.btnCode11.setBackgroundColor(Color.parseColor("#FFFFFF"));
//                        foodCategory = FoodCategory.NONE;
//                    }
//                    break;
//            }
//        }
//    };

    //주소 결과값 가져오는 함수
    @SuppressLint("SetTextI18n")
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        String[] address = data.split(", ");
                        binding.viewActivityInfo.etAddressNumber.setText(address[0]);;
                        if(address.length == 3) {
                            binding.viewActivityInfo.etAddress.setText(address[1] +", " + address[2]);
                        }else{
                            binding.viewActivityInfo.etAddress.setText(address[1]);
                        }
                        binding.viewActivityInfo.etAddressDetail.setText("");
                    }
                    else{
                        showToast(this,"주소를 불러오는 중 오류가 발생했습니다.");
                    }
                }
                break;
        }
    }
}