package com.example.orderingmanager;

import static com.example.orderingmanager.Utillity.showToast;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingmanager.Dto.request.OwnerSignUpDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.databinding.ActivitySignupBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;

import lombok.SneakyThrows;

public class SignupActivity extends AppCompatActivity {

    Intent intent;
    String phoneNum;

    Boolean isMemberIdWritten = false;
    Boolean isPasswordWritten = false;
    Boolean isPasswordCheckAccord = false;

    Animation complete;

    // 아이디 형식 패턴(영문 소문자, 숫자만)
    Pattern ps = Pattern.compile("^[a-zA-Z0-9_]*$");

    private static final String TAG = "SignupActivity_TAG";

    //viewbinding
    private ActivitySignupBinding binding;

    // 닉네임 리스트 생성
    private List<String> firstNick = new ArrayList<String>();
    private List<String> lastNick = new ArrayList<String>();

    // 파이어베이스 인증
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        complete = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);

        // 전화번호 받아온 뒤 표시
        intent = getIntent();
        phoneNum = intent.getStringExtra("phoneNum");
        binding.tvSignupPhoneNum.setText(phoneNum);

        ButtonLock(binding.btnSignup);

        initSignupButton();
        initTextChangedListener();


    }


    private void initTextChangedListener(){

        /* 아이디 입력란 변경 리스너 */
        binding.etMemberId.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                binding.ivIdComplete.setVisibility(View.GONE);

                checkAllWritten();
            }

            @Override
            public void afterTextChanged(Editable s) {

                String input = binding.etMemberId.getText().toString();

                if(input.length()> 4 && input.length() < 21 && ps.matcher(input).matches()){
                    isMemberIdWritten = true;
                } else {
                    isMemberIdWritten = false;
                    binding.ivError4.setVisibility(View.VISIBLE);
                    binding.tvIdError.setVisibility(View.VISIBLE);
                }
                if(isMemberIdWritten){

                    binding.ivError4.setVisibility(View.GONE);
                    binding.tvIdError.setVisibility(View.GONE);

                    // 통과 표시 출력
                    binding.ivIdComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivIdComplete.startAnimation(complete);
                }
                else{
                    binding.ivIdComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }
        });

        /* 비밀번호 입력란 변경 리스너 */
        binding.editTextPassword.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
                binding.ivError2.setVisibility(View.GONE);
                binding.tvPsLength.setVisibility(View.GONE);
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.editTextPassword.getText().toString();
                String passwordCheckText = binding.editTextPasswordCheck.getText().toString();
                int inputLength = binding.editTextPassword.getText().toString().length();
                if (inputLength > 5) {
                    isPasswordWritten = true;
                    binding.ivError2.setVisibility(View.GONE);
                    binding.tvPsLength.setVisibility(View.GONE);
                    binding.ivPsComplete.setVisibility(View.GONE);

                    // 비밀번호확인까지 입력한 뒤 비밀번호를 수정한 경우 조건문 추가
                    if (passwordCheckText.length() > 0) {
                        if(input.equals(passwordCheckText)){
                            isPasswordCheckAccord = true;
                            binding.ivError3.setVisibility(View.GONE);
                            binding.tvPsAccord.setVisibility(View.GONE);
                            binding.ivPsCheckComplete.setVisibility(View.VISIBLE);
                            binding.ivPsCheckComplete.startAnimation(complete);
                        } else {
                            isPasswordCheckAccord = false;
                            binding.ivError3.setVisibility(View.VISIBLE);
                            binding.tvPsAccord.setVisibility(View.VISIBLE);
                            binding.ivPsCheckComplete.setVisibility(View.GONE);
                        }
                    }
                } else {
                    if(passwordCheckText.length() > 0){
                        isPasswordCheckAccord = false;
                        binding.ivError3.setVisibility(View.VISIBLE);
                        binding.tvPsAccord.setVisibility(View.VISIBLE);
                        binding.ivPsCheckComplete.setVisibility(View.GONE);
                    }
                    isPasswordWritten = false;
                    binding.ivError2.setVisibility(View.VISIBLE);
                    binding.tvPsLength.setVisibility(View.VISIBLE);
                    binding.ivPsComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }

            @Override
            public void afterTextChanged(Editable s) {

                if(isPasswordWritten){
                    // 통과 표시 출력
                    binding.ivPsComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivPsComplete.startAnimation(complete);
                }
                else{
                    binding.ivPsComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }
        });

        /* 비밀번호확인 입력란 변경 리스너 */
        binding.editTextPasswordCheck.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                String input = binding.editTextPasswordCheck.getText().toString();
                if (input.equals(binding.editTextPassword.getText().toString())) {
                    if(input.length() > 0 && input.length()<6){
                        isPasswordCheckAccord = false;
                        binding.ivError3.setVisibility(View.VISIBLE);
                        binding.tvPsAccord.setVisibility(View.VISIBLE);
                        binding.ivPsCheckComplete.setVisibility(View.GONE);
                        return;
                    }
                    isPasswordCheckAccord = true;
                    binding.ivError3.setVisibility(View.GONE);
                    binding.tvPsAccord.setVisibility(View.GONE);
                    binding.ivPsCheckComplete.setVisibility(View.GONE);
                } else {
                    isPasswordCheckAccord = false;
                    binding.ivError3.setVisibility(View.VISIBLE);
                    binding.tvPsAccord.setVisibility(View.VISIBLE);
                    binding.ivPsCheckComplete.setVisibility(View.GONE);
                }

                checkAllWritten();

            }

            @Override
            public void afterTextChanged(Editable s) {

                if(isPasswordCheckAccord){
                    // 통과 표시 출력
                    binding.ivPsCheckComplete.setVisibility(View.VISIBLE);

                    // 통과 애니메이션 실행
                    binding.ivPsCheckComplete.startAnimation(complete);
                }
                else{
                    binding.ivPsCheckComplete.setVisibility(View.GONE);
                }

                checkAllWritten();
            }
        });
    }

    private void initSignupButton(){
        binding.btnSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount();
            }
        });
    }

    private void checkAllWritten(){
        if(isMemberIdWritten && isPasswordWritten && isPasswordCheckAccord) {
            ButtonRelease(binding.btnSignup);
        }
        else{
            ButtonLock(binding.btnSignup);
        }
    }
    /* 버튼 Lock거는 함수 */
    private void ButtonLock(Button button) {
        button.setBackgroundColor(Color.parseColor("#5E5E5E"));
        button.setEnabled(false);
    }

    /* 버튼 Lock푸는 함수 */
    private void ButtonRelease(Button button) {
        button.setBackgroundColor(Color.parseColor("#0D70E6"));
        button.setEnabled(true);
    }

    /* 이메일 계정 생성 */
    private void createAccount() {

        String memberId = binding.etMemberId.getText().toString();
        String password = binding.editTextPassword.getText().toString();
        String phoneNum = binding.tvSignupPhoneNum.getText().toString();


        // 이메일 계정 생성 시작
        if (memberId.length() > 4 && memberId.length() < 21 && password.length() > 5) {
            // twilio
            try {
                Log.e("아이디", memberId);
                Log.e("비밀번호", password);
                Log.e("전화번호", phoneNum);
                OwnerSignUpDto ownerSignUpDto = new OwnerSignUpDto(memberId, password, phoneNum);

                URL url = new URL("http://www.ordering.ml/api/owner/signup");
                HttpApi httpApi = new HttpApi(url, "POST");

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String json = httpApi.requestToServer(ownerSignUpDto);
                        ObjectMapper mapper = new ObjectMapper();
                        ResultDto<Boolean> result = mapper.readValue(json, new TypeReference<ResultDto<Boolean>>() {});
                        if (result.getData()) {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    updateDB(memberId, phoneNum);
                                    startActivity(new Intent(SignupActivity.this, LoginActivity.class));
                                    finish();
                                    Log.e(TAG, "회원가입 성공\n아이디:" + memberId + "\n전화번호:" + phoneNum + "\n비밀번호:" + password);
                                    showToast(SignupActivity.this, "회원가입을 완료하였습니다.");
                                }
                            });
                        } else {
                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
                                    Log.w(TAG, "회원가입 실패");
                                    showToast(SignupActivity.this, "이미 존재하는 아이디입니다.");
                                }
                            });
                        }
                    }
                }.start();

            } catch (Exception e) {
                Toast.makeText(this, "회원가입 도중 일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                Log.e("e = ", e.getMessage());
            }

        }

    }

//    private void updateUI(String Nickname, String Email, String Password) {
//
//        mAuth.signInWithEmailAndPassword(Email, Password)
//                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
//                    @Override
//                    public void onComplete(@NonNull Task<AuthResult> task) {
//
//                        if (task.isSuccessful()) {
//                            if(mAuth.getCurrentUser()!=null){
//                                FirebaseUser user = mAuth.getCurrentUser();
//                                updateDB(user, phoneNum, Nickname, Email);
//                                startActivity(new Intent(SignupActivity.this, MainActivity.class));
//                                showToast(SignupActivity.this,"회원가입을 완료하였습니다.");
//                                finish();
//                            }
//                        } else {
//                            Log.w(TAG, "signInWithEmail:failure", task.getException());
//                            showToast(SignupActivity.this,"회원가입에 실패하였습니다.");
//                        }
//                        // ...
//                    }
//                });
//    }

    private void updateDB(String memberId, String phoneNum) {
        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("아이디", memberId);
        userInfo.put("휴대폰번호", phoneNum);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        startActivity(new Intent(SignupActivity.this, StartActivity.class));
        finish();
    }

}