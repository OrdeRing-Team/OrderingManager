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
import com.example.orderingmanager.databinding.ActivityEditStoreInfoBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

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

        initClickListener();
        setData();
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
                        break;
                    case R.id.radio_button_both:
                        restaurantType = RestaurantType.FOR_HERE_TO_GO;
                        binding.viewActivityEditStoreInfo.tablenumtext.setVisibility(View.VISIBLE);
                        binding.viewActivityEditStoreInfo.tablenum.setVisibility(View.VISIBLE);
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
        if(address.length == 4) {
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
        }else if (!radio_button_only.isChecked() && (tableNum == 0)) {
            Toast.makeText(EditStoreInfoActivity.this, "테이블의 수는 1개 이상이어야 합니다.", Toast.LENGTH_SHORT).show();
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
                                    if(restaurantType == RestaurantType.ONLY_TO_GO ||
                                            UserInfo.getTableCount() == Integer.parseInt(binding.viewActivityEditStoreInfo.tablenum.getText().toString())){
                                        // 포장이 선택되었거나 테이블 수가 이전과 같다면 QR코드를 새로 생성하지 않는다.
                                        UserInfo.initRestaurantInfo(restaurantDto);
                                        showToast(EditStoreInfoActivity.this, "매장정보가 저장되었습니다.");
                                        FinishWithAnim();
                                    }
                                    else{
                                        // 반면에 테이블 수가 변경되었다면 QR코드를 새로 생성한다.(QR코드 생성화면으로 넘어간다)
                                        UserInfo.initRestaurantInfo(restaurantDto);
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

}