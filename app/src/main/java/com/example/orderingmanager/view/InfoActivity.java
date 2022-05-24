package com.example.orderingmanager.view;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Vibrator;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantInfoDto;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.HttpApi;
import com.example.orderingmanager.KakaoMap.WebViewActivity;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
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
    InputMethodManager imm;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 매장 식사가 가능할 때만 테이블 수 입력 받기위해 테이블 수 입력창은 가려놓는다.
        binding.viewActivityInfo.tablenumtext.setVisibility(View.GONE);
        binding.viewActivityInfo.tablenum.setVisibility(View.GONE);

        initButtonClickListener();
        initTextChangedListener();
    }

    private void initTextChangedListener(){
        binding.viewActivityInfo.tablenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    int input = Integer.parseInt(binding.viewActivityInfo.tablenum.getText().toString());
                    if (input > 100) {
                        binding.viewActivityInfo.tablenum.setText("100");
                        input = 100;

                        showLongToast(InfoActivity.this, "테이블 수는 100개 까지만 입력할 수 있습니다.");
                        // 짧은 진동을 울림
                        Vibrator vibrator = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                        vibrator.vibrate(300);
                    }
                } catch (NumberFormatException e){
                    Log.e("NumberFormatException",e.getMessage());
                }
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });
    }
    private void initButtonClickListener(){
        // 매장식사/포장 라디오 버튼 클릭 이벤트
        binding.viewActivityInfo.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_onlydeliver:
                        restaurantType = RestaurantType.ONLY_TO_GO;
                        binding.viewActivityInfo.tablenumtext.setVisibility(View.GONE);
                        binding.viewActivityInfo.tablenum.setVisibility(View.GONE);
                        hideKeybord();
                        break;
                    case R.id.radio_button_both:
                        restaurantType = RestaurantType.FOR_HERE_TO_GO;
                        binding.viewActivityInfo.tablenumtext.setVisibility(View.VISIBLE);
                        binding.viewActivityInfo.tablenum.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });

        // 매장식사/포장 라디오 버튼 클릭 이벤트
        binding.viewActivityInfo.radioGroupCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.rbtn_korean_food:
                        foodCategory = FoodCategory.KOREAN_FOOD;
                        hideKeybord();
                        break;
                    case R.id.rbtn_bunsik:
                        foodCategory = FoodCategory.BUNSIK;
                        hideKeybord();
                        break;
                    case R.id.rbtn_cafe_dessert:
                        foodCategory = FoodCategory.CAFE_DESSERT;
                        hideKeybord();
                        break;
                    case R.id.rbtn_japanese_food:
                        foodCategory = FoodCategory.JOKBAL_BOSSAM;
                        hideKeybord();
                        break;
                    case R.id.rbtn_chicken:
                        foodCategory = FoodCategory.CHICKEN;
                        hideKeybord();
                        break;
                    case R.id.rbtn_pizza:
                        foodCategory = FoodCategory.PIZZA;
                        hideKeybord();
                        break;
                    case R.id.rbtn_asian:
                        foodCategory = FoodCategory.ASIAN_FOOD_WESTERN_FOOD;
                        hideKeybord();
                        break;
                    case R.id.rbtn_chinese_food:
                        foodCategory = FoodCategory.CHINESE_FOOD;
                        hideKeybord();
                        break;
                    case R.id.rbtn_jokbal_bossam:
                        foodCategory = FoodCategory.JOKBAL_BOSSAM;
                        hideKeybord();
                        break;
                    case R.id.rbtn_jjim:
                        foodCategory = FoodCategory.JJIM_TANG;
                        hideKeybord();
                        break;
                    case R.id.rbtn_fast_food:
                        foodCategory = FoodCategory.FAST_FOOD;
                        hideKeybord();
                        break;

                }
            }
        });
        binding.startApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                binding.startApp.setEnabled(false);
                checkEmpty();
            }
        });

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
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요", Toast.LENGTH_SHORT).show();
            binding.startApp.setEnabled(true);
            hideKeybord();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        } else if (!radio_button_only.isChecked() && (tableNum == 0)) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요", Toast.LENGTH_SHORT).show();
            binding.startApp.setEnabled(true);
            hideKeybord();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if(foodCategory == FoodCategory.NONE){
            Toast.makeText(InfoActivity.this, "음식 카테고리를 1개 이상 선택해 주세요", Toast.LENGTH_SHORT).show();
            hideKeybord();
            binding.startApp.setEnabled(true);
        }
        else if(binding.viewActivityInfo.etAddress.getText().toString().equals("") || binding.viewActivityInfo.etAddressDetail.getText().toString().equals("")){
            Toast.makeText(InfoActivity.this, "주소를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
            hideKeybord();
            binding.startApp.setEnabled(true);
        }
        else {
            try {
                Log.e("ads",binding.viewActivityInfo.etAddress.getText().toString());
                RestaurantInfoDto restaurantInfoDto = new RestaurantInfoDto(storeName, ownerName, address, tableNum, foodCategory, restaurantType, null, null);

                URL url = new URL("http://www.ordering.ml/api/owner/" + String.valueOf(UserInfo.getOwnerId()) + "/restaurant");
                HttpApi httpApi = new HttpApi(url, "POST");

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String json = httpApi.requestToServer(restaurantInfoDto);
                        ObjectMapper mapper = new ObjectMapper();
                        ResultDto<Long> result = mapper.readValue(json, new TypeReference<ResultDto<Long>>() {});
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(result.getData() != null) {
                                    UserInfo.initRestaurantInfo(UserInfo.getOwnerId(), restaurantInfoDto);
                                    UserInfo.setRestaurantId(result.getData());
                                    Log.e("restaurantId ",result.getData().toString());
                                    createQRCodes();
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
        hideKeybord();
        startActivity(new Intent(InfoActivity.this, CreateQR.class));
        FinishWithAnim();
    }

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

    private void hideKeybord(){
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}