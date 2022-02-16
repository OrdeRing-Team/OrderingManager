package com.example.orderingmanager;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.orderingmanager.databinding.ActivityInfoBinding;

public class InfoActivity extends BasicActivity {

    //viewbinding
    private ActivityInfoBinding binding;

    private static final int SEARCH_ADDRESS_ACTIVITY = 10000;
    private EditText et_address;

    RadioGroup radioGroup;
    TextView tablenumtext;
    Button btnStartApp;
    Button btn_map;
    Button btn_search;

    EditText inputStoreName;
    EditText inputUserName;
    EditText tablenum;

    TextView psAccord;

    boolean[] codeStatus;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        et_address = (EditText) findViewById(R.id.et_address);
        //Button btn_search = (Button) findViewById(R.id.btn_location);




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

        findViewById(R.id.btn_code1).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code2).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code3).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code4).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code5).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code6).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code7).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code8).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code9).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code10).setOnClickListener(onClickListener);
        findViewById(R.id.btn_code11).setOnClickListener(onClickListener);

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
//

        btnStartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEmpty();
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

    // 입력창이 비었거나, 비밀번호가 일치하지 않을 때 알림을 띄우는 함수 (다 차 있고 비밀번호 입력을 안 하면 오류 발생,,,)
    private void isEmpty() {
        String storeName = inputStoreName.getText().toString();
        String userName = inputUserName.getText().toString();
        //String kategorie = inputKategorie.getText().toString();
        //String nicName = inputNicname.getText().toString();
        String tableNum = tablenum.getText().toString();
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
        if ((radio_button_only.isChecked() == false) && (radio_button_both.isChecked() == false)) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        } else if (radio_button_both.isChecked() == true && tableNum.matches("")) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        /*else if(psBeforeCheck.isEmpty() == true || psAfterCheck.isEmpty() == true) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }*/
        else {
            Toast.makeText(InfoActivity.this, "오더링 START", Toast.LENGTH_SHORT).show();
            Log.d("isn'tEmpty", "입력칸 모두 채워짐.");
            //디비에 데이터 저장하기. (차후에 추가할 부분)
        }
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

    //주소 결과값 가져오는 함수
    public void onActivityResult(int requestCode, int resultCode, Intent intent)
    {
        super.onActivityResult(requestCode, resultCode, intent);
        switch (requestCode) {
            case SEARCH_ADDRESS_ACTIVITY:
                if (resultCode == RESULT_OK) {
                    String data = intent.getExtras().getString("data");
                    if (data != null) {
                        et_address.setText(data);
                    }
                }
                break;
        }
    }
}