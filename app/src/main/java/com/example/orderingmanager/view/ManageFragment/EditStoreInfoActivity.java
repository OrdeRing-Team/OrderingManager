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
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.PasswordChangeDto;
import com.example.orderingmanager.Dto.request.RestaurantDataDto;
import com.example.orderingmanager.Dto.request.RestaurantDataWithLocationDto;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.Dto.response.RestaurantInfoDto;
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
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditStoreInfoActivity extends BasicActivity {

    private ActivityEditStoreInfoBinding binding;

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    public static final int EDIT_NONE = 11111;
    public static final int EDIT_ONLY = 22222;
    public static final int EDIT_BOTH = 33333;

    int tableNum;
    double longitude = 0, latitude = 0;

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

                        showLongToast(EditStoreInfoActivity.this, "????????? ?????? 100??? ????????? ????????? ??? ????????????.");
                        // ?????? ????????? ??????
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
        // ???????????? ??????
        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // ?????? ??????
        binding.btnSaveStoreInfo.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                checkEmpty();
            }
        });

        // ????????? ?????? ?????? ?????????
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

        // ????????????/?????? ????????? ?????? ?????? ?????????
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
        // ??? ?????? ????????? ?????? ????????? ??????

        // ?????????/?????????
        binding.viewActivityEditStoreInfo.inputStoreName.setText(UserInfo.getRestaurantName());

        // ????????????
        binding.viewActivityEditStoreInfo.inputUserName.setText(UserInfo.getOwnerName());

        // ??????
        // ?????? ????????? split??? ?????? ", " (?????? 1?????? ??????) ??? ???????????? ???????????? ????????? ?????????.
        // ????????? ?????? ?????? ????????? "06544, ?????? ????????? ???????????? 270 (?????????, ?????????????????????)" ??????????????? "?????????" ?????? ", "??? ???????????? ????????? ????????? ??????
        // "52828, ?????? ????????? ???????????? 501 (?????????)" ??? ?????? ", "??? ???????????? ?????? ????????? ??????.
        // ????????? ?????? ????????? ????????? ?????? ", " ??? ???????????? split ???????????? ????????? ?????????
        // ??? ????????????????????? ????????? ?????? ", "??? ????????? ???????????? ????????? address.length()??? 4??? ????????????
        // ?????????????????? ????????? ?????? ????????? address.length()??? 3??? ????????????.
        // ?????? ?????? ????????? ???????????? setText?????????.
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

        // ????????????(??????/??????)
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

        setRestaurantLatlng();

    }

    private void setRestaurantLatlng(){
        try {

            new Thread() {
                @SneakyThrows
                public void run() {
                    Log.e("asdasd","asdasd");
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+ UserInfo.getRestaurantId() +"/info/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<RestaurantInfoDto>> call = service.getStoreNoticeAndCoordinate(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<RestaurantInfoDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<RestaurantInfoDto>> call, Response<ResultDto<RestaurantInfoDto>> response) {

                            ResultDto<RestaurantInfoDto> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            if(result.getData().getLatitude() != 0 && result.getData().getLongitude() != 0){
                                                latitude = result.getData().getLatitude();
                                                longitude = result.getData().getLongitude();

                                                Log.e("@@@@@@@Latitude ", Double.toString(latitude));
                                                Log.e("@@@@@@@Longitude ", Double.toString(longitude));
                                            }
                                        }
                                    });
                                }
                            }else{
                                Log.e("response########","failed");
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<RestaurantInfoDto>> call, Throwable t) {
                            showLongToast(EditStoreInfoActivity.this, "???????????? ????????? ??????????????????");
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            showLongToast(EditStoreInfoActivity.this, "???????????? ????????? ??????????????????");
            Log.e("e = ", e.getMessage());
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

        // "????????? ??????" ????????????????????? tablenum = 0 ?????? ??????
        if(radio_button_only.isChecked()){
            tableNum = 0;
        }

        if ((!radio_button_only.isChecked()) && (!radio_button_both.isChecked())) {
            Toast.makeText(EditStoreInfoActivity.this, "???????????? ?????? ???????????????.", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }else if (!radio_button_only.isChecked() && (tableNum == 0)) {
            Toast.makeText(EditStoreInfoActivity.this, "???????????? ?????? 1??? ??????????????? ?????????.", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }
        else if(foodCategory == FoodCategory.NONE){
            Toast.makeText(EditStoreInfoActivity.this, "?????? ??????????????? 1??? ?????? ????????? ?????????.", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }
        else if(binding.viewActivityEditStoreInfo.etAddress.getText().toString().equals("") || binding.viewActivityEditStoreInfo.etAddressDetail.getText().toString().equals("")){
            Toast.makeText(EditStoreInfoActivity.this, "????????? ?????? ????????? ?????????", Toast.LENGTH_SHORT).show();
            hideKeybord();
        }
        else {

            try {
                RestaurantDataWithLocationDto restaurantDataWithLocationDto = new RestaurantDataWithLocationDto(storeName, ownerName, address, tableNum, foodCategory, restaurantType, 0, 0, latitude, longitude);

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        Log.e("asdasd","asdasd");
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Boolean>> call = service.modifyStore(UserInfo.getRestaurantId(), restaurantDataWithLocationDto);

                        call.enqueue(new Callback<ResultDto<Boolean>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                ResultDto<Boolean> result;
                                result = response.body();
                                if (response.isSuccessful()) {
                                    if (result.getData()) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                if(restaurantType == RestaurantType.ONLY_TO_GO ||
                                                        UserInfo.getTableCount() == Integer.parseInt(binding.viewActivityEditStoreInfo.tablenum.getText().toString())){
                                                    // ????????? ?????????????????? ????????? ?????? ????????? ????????? QR????????? ?????? ???????????? ?????????.
                                                    UserInfo.modifyRestaurantInfo(UserInfo.getOwnerId(), restaurantDataWithLocationDto);
                                                    initQrList();
                                                    showToast(EditStoreInfoActivity.this, "??????????????? ?????????????????????.");
                                                    FinishWithAnim();
                                                }
                                                else{
                                                    // ????????? ????????? ?????? ?????????????????? QR????????? ?????? ????????????.(QR?????? ?????????????????? ????????????)
                                                    UserInfo.modifyRestaurantInfo(UserInfo.getOwnerId(), restaurantDataWithLocationDto);
                                                    createQRCodes();
                                                }
                                            }
                                        });
                                    }
                                }else{
                                    Log.e("response########","failed");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                showLongToast(EditStoreInfoActivity.this, "???????????? ????????? ??????????????????");
                                Log.e("e = ", t.getMessage());
                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                showLongToast(EditStoreInfoActivity.this, "???????????? ????????? ??????????????????");
                Log.e("e = ", e.getMessage());
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
                        longitude = data.getExtras().getDouble("longitude");
                        latitude = data.getExtras().getDouble("latitude");
                        Log.e("??????: ",addressExtra);
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
                            showToast(this,"????????? ???????????? ??? ????????? ??????????????????.");
                        }
                    }
                    break;
            }
        }
    }

    public void initQrList(){
        // MainActivity??? ???????????? QrList??? ???????????? ??? UserInfo??? ????????? tableCount??? ????????????
        // QrList??? ??????Qr,?????????Qr,?????????Qr Bitmap??? ????????????.
        // static ???????????? ???????????? ????????? ?????? ???????????? ??????????????? ???????????? ?????????
        // ???????????? QrList??? ????????? ??? ??????

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
            Log.e("takeout qr ", "??????");
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