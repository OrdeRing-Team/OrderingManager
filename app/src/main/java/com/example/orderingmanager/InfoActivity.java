package com.example.orderingmanager;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.textfield.TextInputEditText;

public class InfoActivity extends AppCompatActivity {

    RadioGroup radioGroup;
    TextView tablenumtext;
    Button btnStartApp;

    EditText inputStoreName;
    EditText inputUserName;
    EditText inputKategorie;
    EditText inputNicname;
    EditText tablenum;
    TextInputEditText psFirst;
    TextInputEditText psSecond;

    TextView psTrue;
    TextView psFalse;

    String psBeforeCheck;
    String psAfterCheck;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info);

        radioGroup = findViewById(R.id.radio_group);
        tablenumtext = findViewById(R.id.tablenumtext);
        tablenum = findViewById(R.id.tablenum);
        inputStoreName = findViewById(R.id.input_storeName);
        inputUserName = findViewById(R.id.input_userName);
        inputKategorie = findViewById(R.id.input_Kategorie);
        inputNicname = findViewById(R.id.input_nicname);
        btnStartApp = findViewById(R.id.startApp);

        psFirst = findViewById(R.id.textInputEditText_ps1);
        psSecond = findViewById(R.id.textInputEditText_ps2);

        psTrue = findViewById(R.id.psTrue);
        psFalse = findViewById(R.id.psFalse);


        // 매장 식사가 가능할 때만 테이블 수 입력 받기위해 테이블 수 입력창은 가려놓는다.
        tablenumtext.setVisibility(View.GONE);
        tablenum.setVisibility(View.GONE);

        // 비밀번호 일치, 불일치를 알리기 위해 알림 메시지는 가려놓는다.
        psTrue.setVisibility(View.GONE);
        psFalse.setVisibility(View.GONE);


        // 라디오 버튼 클릭 이벤트
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId){
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

        // 비밀번호 일치, 불일치 확인하기
        psFirst.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                psBeforeCheck = psFirst.getText().toString();
                Log.d("firstPS", psBeforeCheck);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        psSecond.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                psAfterCheck = psSecond.getText().toString();
                Log.d("secondPS", psAfterCheck);

                if( psBeforeCheck.equals(psAfterCheck) ) {
                    psTrue.setVisibility(View.VISIBLE);
                    psFalse.setVisibility(View.GONE);
                }
                else {
                    psTrue.setVisibility(View.GONE);
                    psFalse.setVisibility(View.VISIBLE);

                }
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

        btnStartApp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                isEmpty();
            }
        });

    }


    // 입력창이 비었거나, 비밀번호가 일치하지 않을 때 알림을 띄우는 함수 (다 차 있고 비밀번호 입력을 안 하면 오류 발생,,,)
    private void isEmpty() {
        String storeName = inputStoreName.getText().toString();
        String userName = inputUserName.getText().toString();
        String kategorie = inputKategorie.getText().toString();
        String nicName = inputNicname.getText().toString();
        String tableNum = tablenum.getText().toString();
        RadioButton radio_button_only = findViewById(R.id.radio_button_onlydeliver);
        RadioButton radio_button_both = findViewById(R.id.radio_button_both);

        if(storeName.matches("") || userName.matches("") || kategorie.matches("") || nicName.matches("")) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if(psBeforeCheck.equals(psAfterCheck) == false) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if((radio_button_only.isChecked() == false) && (radio_button_both.isChecked() == false)) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if(radio_button_both.isChecked() == true && tableNum.matches("")) {
                Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else if(psBeforeCheck.isEmpty() == true || psAfterCheck.isEmpty() == true) {
            Toast.makeText(InfoActivity.this, "입력칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
            Log.d("isEmpty", "입력칸을 모두 채워라.");
        }
        else {
            Toast.makeText(InfoActivity.this, "오더링 START", Toast.LENGTH_SHORT).show();
            Log.d("isn'tEmpty", "입력칸 모두 채워짐.");
            //디비에 데이터 저장하기. (차후에 추가할 부분)
        }
    }
}