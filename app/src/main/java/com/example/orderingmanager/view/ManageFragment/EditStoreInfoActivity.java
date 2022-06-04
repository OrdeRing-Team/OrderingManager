package com.example.orderingmanager.view.ManageFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import com.example.orderingmanager.Dto.request.RestaurantDataDto;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.HttpApi;
import com.example.orderingmanager.KakaoMap.WebViewActivity;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityEditStoreInfoBinding;
import com.example.orderingmanager.view.BasicActivity;
import com.example.orderingmanager.view.CreateQR;
import com.example.orderingmanager.view.MainActivity;
import com.example.orderingmanager.view.QRFragment.QrList;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.net.URL;
import java.util.ArrayList;

import lombok.SneakyThrows;

public class EditStoreInfoActivity extends BasicActivity {

    private ActivityEditStoreInfoBinding binding;

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    public static final int EDIT_NONE = 11111;
    public static final int EDIT_ONLY = 22222;
    public static final int EDIT_BOTH = 33333;

    int tableNum;

    RestaurantType restaurantType = UserInfo.getRestaurantType();
    FoodCategory foodCategory = UserInfo.getFoodCategory();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityEditStoreInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initView();
        initClickListener();
        initTextChangedListener();
        setData();
    }

    private void initView() {
        binding.viewActivityEditStoreInfo.tvTime.setVisibility(View.GONE);
        binding.viewActivityEditStoreInfo.llTimeSet.setVisibility(View.GONE);
    }

    private void initTextChangedListener(){
        binding.viewActivityEditStoreInfo.tablenum.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                try{
                    int input = Integer.parseInt(binding.viewActivityEditStoreInfo.tablenum.getText().toString());
                    if (input > 100) {
                        binding.viewActivityEditStoreInfo.tablenum.setText("100");
                        input = 100;

                        showLongToast(EditStoreInfoActivity.this, "테이블 수는 100개 까지만 입력할 수 있습니다.");
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

    private void initClickListener(){
        // 뒤로가기 버튼
        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 저장 버튼
        binding.btnSaveStoreInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkEmpty();
            }
        });

        // 라디오 버튼 클릭 이벤트
        binding.viewActivityEditStoreInfo.radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.radio_button_onlydeliver:
                        restaurantType = RestaurantType.ONLY_TO_GO;
                        binding.viewActivityEditStoreInfo.tablenumtext.setVisibility(View.GONE);
                        binding.viewActivityEditStoreInfo.tablenum.setVisibility(View.GONE);
                        hideKeybord();
                        break;
                    case R.id.radio_button_both:
                        restaurantType = RestaurantType.FOR_HERE_TO_GO;
                        binding.viewActivityEditStoreInfo.tablenumtext.setVisibility(View.VISIBLE);
                        binding.viewActivityEditStoreInfo.tablenum.setVisibility(View.VISIBLE);
                        break;

                }
            }
        });

        // 매장식사/포장 라디오 버튼 클릭 이벤트
        binding.viewActivityEditStoreInfo.radioGroupCategory.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
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
                        foodCategory = FoodCategory.PORK_CUTLET_ROW_FISH_SUSHI;
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
        binding.viewActivityEditStoreInfo.btnLocation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)
            {
                Intent i = new Intent(EditStoreInfoActivity.this, WebViewActivity.class);
                startActivityForResult(i, SEARCH_ADDRESS_ACTIVITY);
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setData(){
        // 각 입력 항목에 기존 정보로 세팅

        // 매장명/상호명
        binding.viewActivityEditStoreInfo.inputStoreName.setText(UserInfo.getRestaurantName());

        // 사업자명
        binding.viewActivityEditStoreInfo.inputUserName.setText(UserInfo.getOwnerName());

        // 주소
        // 주소 배열은 split을 통해 ", " (공백 1문자 포함) 을 기준으로 구분하여 배열에 담는다.
        // 도로명 주소 검색 결과에 "06544, 서울 서초구 신반포로 270 (반포동, 반포자이아파트)" 이런식으로 "반포동" 뒤에 ", "가 포함되어 나오는 주소가 있고
        // "52828, 경남 진주시 진주대로 501 (가좌동)" 과 같이 ", "가 포함되지 않는 주소도 있다.
        // 따라서 처음 배열에 담을때 먼저 ", " 을 기준으로 split 한다음에 배열에 담으면
        // 위 반포자이아파트 주소와 같이 ", "가 결과에 포함되는 주소면 address.length()는 4가 될것이고
        // 경상국립대의 주소와 같은 경우는 address.length()는 3이 될것이다.
        // 위와 같은 경우를 나누어서 setText해준다.
        String[] address = getAddressArr();
        binding.viewActivityEditStoreInfo.etAddressNumber.setText(address[0]);
        Log.e("address[0] = ", address[0]);
        if(address.length >= 4) {
            binding.viewActivityEditStoreInfo.etAddress.setText(address[1] +", " + address[2]);
            binding.viewActivityEditStoreInfo.etAddressDetail.setText(address[3]);
            Log.e("address[1] = ", address[1]);
            Log.e("address[2] = ", address[2]);
            Log.e("address[3] = ", address[3]);
        }else{
            binding.viewActivityEditStoreInfo.etAddress.setText(address[1]);
            Log.e("address[1] = ", address[1]);
            binding.viewActivityEditStoreInfo.etAddressDetail.setText(address[2]);
            Log.e("address[2] = ", address[2]);
        }

        // 매장종류(포장/식사)
        if(UserInfo.getRestaurantType() == RestaurantType.ONLY_TO_GO){
            restaurantType = RestaurantType.ONLY_TO_GO;
            binding.viewActivityEditStoreInfo.radioButtonOnlydeliver.setChecked(true);
        }
        else{
            restaurantType = RestaurantType.FOR_HERE_TO_GO;
            binding.viewActivityEditStoreInfo.radioButtonBoth.setChecked(true);
            binding.viewActivityEditStoreInfo.tablenum.setText(Integer.toString(UserInfo.getTableCount()));
        }

        switch(UserInfo.getFoodCategory()){
            case KOREAN_FOOD:
                foodCategory = FoodCategory.KOREAN_FOOD;
                binding.viewActivityEditStoreInfo.rbtnKoreanFood.setChecked(true);
                hideKeybord();
                break;
            case BUNSIK:
                foodCategory = FoodCategory.BUNSIK;
                binding.viewActivityEditStoreInfo.rbtnBunsik.setChecked(true);
                hideKeybord();
                break;
            case CAFE_DESSERT:
                foodCategory = FoodCategory.CAFE_DESSERT;
                binding.viewActivityEditStoreInfo.rbtnCafeDessert.setChecked(true);
                hideKeybord();
                break;
            case PORK_CUTLET_ROW_FISH_SUSHI:
                foodCategory = FoodCategory.PORK_CUTLET_ROW_FISH_SUSHI;
                binding.viewActivityEditStoreInfo.rbtnJapaneseFood.setChecked(true);
                hideKeybord();
                break;
            case CHICKEN:
                foodCategory = FoodCategory.CHICKEN;
                binding.viewActivityEditStoreInfo.rbtnChicken.setChecked(true);
                hideKeybord();
                break;
            case PIZZA:
                foodCategory = FoodCategory.PIZZA;
                binding.viewActivityEditStoreInfo.rbtnPizza.setChecked(true);
                hideKeybord();
                break;
            case ASIAN_FOOD_WESTERN_FOOD:
                foodCategory = FoodCategory.ASIAN_FOOD_WESTERN_FOOD;
                binding.viewActivityEditStoreInfo.rbtnAsian.setChecked(true);
                hideKeybord();
                break;
            case CHINESE_FOOD:
                foodCategory = FoodCategory.CHINESE_FOOD;
                binding.viewActivityEditStoreInfo.rbtnChineseFood.setChecked(true);
                hideKeybord();
                break;
            case JOKBAL_BOSSAM:
                foodCategory = FoodCategory.JOKBAL_BOSSAM;
                binding.viewActivityEditStoreInfo.rbtnJokbalBossam.setChecked(true);
                hideKeybord();
                break;
            case JJIM_TANG:
                foodCategory = FoodCategory.JJIM_TANG;
                binding.viewActivityEditStoreInfo.rbtnJjim.setChecked(true);
                hideKeybord();
                break;
            case FAST_FOOD:
                foodCategory = FoodCategory.FAST_FOOD;
                binding.viewActivityEditStoreInfo.rbtnFastFood.setChecked(true);
                hideKeybord();
                break;
        }

    }

    private String[] getAddressArr(){
        return UserInfo.getAddress().split(", ");
    }

    private void checkEmpty() {
        String storeName = binding.viewActivityEditStoreInfo.inputStoreName.getText().toString();
        String ownerName = binding.viewActivityEditStoreInfo.inputUserName.getText().toString();
        String address = binding.viewActivityEditStoreInfo.etAddressNumber.getText().toString() + ", "
                + binding.viewActivityEditStoreInfo.etAddress.getText().toString() + ", "
                + binding.viewActivityEditStoreInfo.etAddressDetail.getText().toString();
        //String kategorie = inputKategorie.getText().toString();

        if(!binding.viewActivityEditStoreInfo.tablenum.getText().toString().equals("")){
            tableNum = Integer.parseInt(binding.viewActivityEditStoreInfo.tablenum.getText().toString());
        }
        RadioButton radio_button_only = binding.viewActivityEditStoreInfo.radioButtonOnlydeliver;
        RadioButton radio_button_both = binding.viewActivityEditStoreInfo.radioButtonBoth;

        // "포장만 가능" 체크되어있으면 tablenum = 0 으로 고정
        if(radio_button_only.isChecked()){
            tableNum = 0;
        }

        if ((!radio_button_only.isChecked()) && (!radio_button_both.isChecked())) {
            Toast.makeText(EditStoreInfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }else if (!radio_button_only.isChecked() && (tableNum == 0)) {
            Toast.makeText(EditStoreInfoActivity.this, "테이블의 수는 1개 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }
        else if(foodCategory == FoodCategory.NONE){
            Toast.makeText(EditStoreInfoActivity.this, "음식 카테고리를 1개 이상 선택해 주세요.", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }
        else if(binding.viewActivityEditStoreInfo.etAddress.getText().toString().equals("") || binding.viewActivityEditStoreInfo.etAddressDetail.getText().toString().equals("")){
            Toast.makeText(EditStoreInfoActivity.this, "주소를 모두 입력해 주세요", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }
        else {
            try {
                RestaurantDataDto restaurantDataDto = new RestaurantDataDto(storeName, ownerName, address, tableNum, foodCategory, restaurantType, 0, 0);

                URL url = new URL("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId().toString());
                HttpApi httpApi = new HttpApi(url, "PUT");

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String json = httpApi.requestToServer(restaurantDataDto);
                        ObjectMapper mapper = new ObjectMapper();
                        ResultDto<Boolean> result = mapper.readValue(json, new TypeReference<ResultDto<Boolean>>() {});
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                if(result.getData() != null) {
                                    if(restaurantType == RestaurantType.ONLY_TO_GO ||
                                            UserInfo.getTableCount() == Integer.parseInt(binding.viewActivityEditStoreInfo.tablenum.getText().toString())){
                                        // 포장이 선택되었거나 테이블 수가 이전과 같다면 QR코드를 새로 생성하지 않는다.
                                        UserInfo.modifyRestaurantInfo(UserInfo.getOwnerId(), restaurantDataDto);
                                        initQrList();
                                        showToast(EditStoreInfoActivity.this, "매장정보가 저장되었습니다.");
                                        FinishWithAnim();
                                    }
                                    else{
                                        // 반면에 테이블 수가 변경되었다면 QR코드를 새로 생성한다.(QR코드 생성화면으로 넘어간다)
                                        UserInfo.modifyRestaurantInfo(UserInfo.getOwnerId(), restaurantDataDto);
                                        createQRCodes();
                                    }
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
        Intent intent = new Intent(EditStoreInfoActivity.this, CreateQR.class);
        if(binding.viewActivityEditStoreInfo.radioButtonBoth.isChecked()){
            intent.putExtra("EditStoreInfo", EDIT_BOTH);
        }
        else{
            intent.putExtra("EditStoreInfo", EDIT_ONLY);
        }
        startActivity(intent);
        finish();
        //startActivityForResult(intent,999);
    }

    @SuppressLint("SetTextI18n")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            Log.e("resultcpode", "okkkkkkkkkk");
            switch (requestCode) {
                case 999:
                    Intent intent = new Intent(EditStoreInfoActivity.this, MainActivity.class);
                    setResult(RESULT_OK, intent);
                    FinishWithAnim();
                    break;

                case SEARCH_ADDRESS_ACTIVITY:
                    if (resultCode == RESULT_OK) {
                        String addressExtra = data.getExtras().getString("data");

                        Log.e("주소: ",addressExtra);
                        if (addressExtra != null) {
                            String[] address = addressExtra.split(", ");
                            binding.viewActivityEditStoreInfo.etAddressNumber.setText(address[0]);;
                            if(address.length == 3) {
                                binding.viewActivityEditStoreInfo.etAddress.setText(address[1] +", " + address[2]);
                            }else{
                                binding.viewActivityEditStoreInfo.etAddress.setText(address[1]);
                            }
                            binding.viewActivityEditStoreInfo.etAddressDetail.setText("");
                        }
                        else{
                            showToast(this,"주소를 불러오는 중 오류가 발생했습니다.");
                        }
                    }
                    break;
            }
        }
    }

    public void initQrList(){
        // MainActivity가 실행되면 QrList를 초기화한 뒤 UserInfo에 입력된 tableCount를 가져오고
        // QrList에 포장Qr,웨이팅Qr,테이블Qr Bitmap을 저장한다.
        // static 클래스에 저장되기 때문에 앱이 실행종료 되기전까지 리스트는 유효함
        // 어디서는 QrList를 불러올 수 있음

        int tableCount = UserInfo.getTableCount();
        ArrayList<Bitmap> qrArrayList = new ArrayList<>();
        qrArrayList.add(CreateTakeoutQR());
        qrArrayList.add(CreateWaitingQR());

        if(tableCount != 0) {
            for (int i = 1; i < tableCount + 1; i++) {
                qrArrayList.add(CreateTableQR(i));
            }
        }

        QrList qrList = new QrList(qrArrayList);
    }
    private Bitmap CreateTakeoutQR() {
        String url;
        url = "http://www.ordering.ml/" + UserInfo.getRestaurantId() + "/takeout";
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Log.e("takeout qr ", "성공");
            return bitmap;
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            Log.e("takeout qr ", e.toString());
            Log.e("url = ", url);
            return bitmap;
        }
    }
    private Bitmap CreateWaitingQR(){
        String url;
        url = "http://www.ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/waiting";
        try{
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE,250,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            return bitmap;
        }catch (Exception e){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            return bitmap;
        }
    }

    private Bitmap CreateTableQR(int i){
        String url;
        url = "http://www.ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/table" + i;
        try{
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE,250,250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            return bitmap;
        }catch (Exception e){
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            return bitmap;
        }
    }

    private void hideKeybord(){
        if(getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            inputMethodManager.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
        }
    }
}