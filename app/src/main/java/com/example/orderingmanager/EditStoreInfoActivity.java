package com.example.orderingmanager;

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
    }

    private void setData(){
        // 각 입력 항목에 기존 정보로 세팅

        // 매장명/상호명
        binding.viewActivityEditStoreInfo.inputStoreName.setText(UserInfo.getRestaurantName());

        // 사업자명
        binding.viewActivityEditStoreInfo.inputUserName.setText(UserInfo.getOwnerName());

        // 주소
        String[] address = getAddressArr();
        binding.viewActivityEditStoreInfo.etAddressNumber.setText(address[0]);
        binding.viewActivityEditStoreInfo.etAddress.setText(address[1]);
        binding.viewActivityEditStoreInfo.etAddressDetail.setText(address[2]);

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
                                    UserInfo.initRestaurantInfo(restaurantDto);
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
            }
        }
    }
}