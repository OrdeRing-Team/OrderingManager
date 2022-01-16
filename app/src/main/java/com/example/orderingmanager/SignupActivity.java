package com.example.orderingmanager;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.orderingmanager.databinding.ActivitySignupBinding;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.Timer;
import java.util.TimerTask;
import java.util.concurrent.TimeUnit;

public class SignupActivity extends BasicActivity {

    //viewbinding ->  findViewById를 쓰지 않고 뷰 컴포넌트를 접근할수 있게 도와주는 기능
    private ActivitySignupBinding binding;

    //코드 전송에 실패하면 재전송 코드를 위해 선언
    private PhoneAuthProvider.ForceResendingToken forceResendingToken;

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    private String mVerificationId; // 코드를 담을 변수

    private static final String TAG = "SIGNUP_TAG";

    private FirebaseAuth firebaseAuth;

    int minute, second;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySignupBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        binding.etPhoneSignup.setVisibility(View.VISIBLE);
        binding.btnSendSMS.setVisibility(View.VISIBLE);
        binding.etVerifyCode.setVisibility(View.GONE);
        binding.btnVerifyCode.setVisibility(View.GONE);
        binding.tvVerifyCode.setVisibility(View.GONE);
        binding.tvResend.setVisibility(View.GONE);
        binding.tvTimerLeft.setVisibility(View.GONE);
        binding.tvTimer.setVisibility(View.GONE);
        binding.tvTimerMin.setVisibility(View.GONE);
        binding.tvTimerSec.setVisibility(View.GONE);
        ButtonLock(binding.btnSendSMS);
        ButtonLock(binding.btnVerifyCode);
        firebaseAuth = FirebaseAuth.getInstance();


        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks(){
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential){

            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e){

            }

            @Override
            public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
                super.onCodeSent(s, forceResendingToken);
                binding.tvResend.setVisibility(View.VISIBLE);
                binding.tvVerifyCode.setVisibility(View.VISIBLE);
                binding.etVerifyCode.setVisibility(View.VISIBLE);
                binding.btnVerifyCode.setVisibility(View.VISIBLE);
                binding.btnSendSMS.setBackgroundColor(Color.parseColor("#5E5E5E"));
                binding.btnSendSMS.setEnabled(false);
                binding.etPhoneSignup.setEnabled(false);
                binding.etPhoneSignup.setTextColor(Color.parseColor("#9A9A9A"));

                binding.etVerifyCode.requestFocus();

                // progressBar 실행
                binding.progressBar.setVisibility(View.GONE);

                //키보드 보이게 하는 부분
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY);

                TimerStart();
            }
        };


        // ******** 매장정보입력 버튼 클릭 이벤트 (임시) *************
        binding.goInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), InfoActivity.class);
                startActivity(intent);
            }
        });

        /* 전화번호 입력란 글자수 리스너 입니다 */
        binding.etPhoneSignup.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int input = binding.etPhoneSignup.getText().toString().length();
                if(input > 10){
                    ButtonRelease(binding.btnSendSMS);
                }
                else ButtonLock(binding.btnSendSMS);
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        /* 인증번호 입력란 글자수 리스너 입니다 */
        binding.etVerifyCode.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }


            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                int input = binding.etVerifyCode.getText().toString().length();
                if(input > 5){
                    ButtonRelease(binding.btnVerifyCode);
                }
                else ButtonLock(binding.btnVerifyCode);
            }


            @Override
            public void afterTextChanged(Editable s) {

            }
        });


        binding.ibClose.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(),0);

                FinishWithAnim();
            }
        });


        binding.btnSendSMS.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                // 010부터 받은 전화번호를 +8210... 으로 변환해준다.
                String phoneNum = "+82" + binding.etPhoneSignup.getText().toString().trim().substring(1);
                Toast.makeText(SignupActivity.this, phoneNum, Toast.LENGTH_SHORT).show();

                // 키보드 내리기
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(binding.etPhoneSignup.getWindowToken(),0);

                // 문자 전송
                startPhoneNumberVerification(phoneNum);

                // progressBar 실행
                binding.progressBar.setVisibility(View.VISIBLE);
            }
        });


        binding.btnVerifyCode.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){

            }
        });
    }
    private void TimerStart(){
        String min = Integer.toString(2);
        String sec = Integer.toString(0);

        binding.tvTimerMin.setText(min);
        binding.tvTimerSec.setText(sec);
        binding.tvTimerMin.setVisibility(View.VISIBLE);
        binding.tvTimerSec.setVisibility(View.VISIBLE);
        binding.tvTimer.setVisibility(View.VISIBLE);
        binding.tvTimerLeft.setVisibility(View.VISIBLE);

        minute = Integer.parseInt(binding.tvTimerMin.getText().toString());
        second = Integer.parseInt(binding.tvTimerSec.getText().toString());

        Timer timer = new Timer();
        TimerTask timerTask = new TimerTask() {
            @Override
            public void run() {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // 반복실행할 구문

                        // 0초 이상이면
                        if(second != 0) {
                            //1초씩 감소
                            second--;

                            // 0분 이상이면
                        } else if(minute != 0) {
                            // 1분 = 60초
                            second = 60;
                            second--;
                            minute--;

                        }

                        // 분, 초가 10이하(한자리수) 라면
                        // 숫자 앞에 0을 붙인다 ( 8 -> 08 )
                        if(second <= 9){
                            binding.tvTimerSec.setText("0" + second);
                        } else {
                            binding.tvTimerSec.setText(Integer.toString(second));
                        }

                        if(minute <= 9){
                            binding.tvTimerMin.setText("0" + minute);
                        } else {
                            binding.tvTimerMin.setText(Integer.toString(minute));
                        }

                        // 분, 초가 다 0이라면 메세지를 출력한다.
                        if(minute == 0 && second == 0) {

                            timer.cancel();//타이머 종료

                            binding.tvTimerMin.setVisibility(View.GONE);
                            binding.tvTimer.setVisibility(View.GONE);
                            binding.tvTimerLeft.setVisibility(View.GONE);

                            binding.tvTimerSec.setText("(시간초과) 처음부터 다시 시작해주세요.");
                            binding.tvTimerSec.setTextColor(Color.parseColor("#FF0000"));
                            binding.etVerifyCode.setEnabled(false);
                            ButtonLock(binding.btnVerifyCode);
                        }
                    }
                });
            }
        };

        //타이머를 실행
        timer.schedule(timerTask, 0, 1000); //Timer 실행
    }
    /* 버튼 Lock거는 함수 */
    private void ButtonLock(Button button){
        if(button.equals(binding.btnSendSMS)){
            binding.btnSendSMS.setBackgroundColor(Color.parseColor("#5E5E5E"));
            binding.btnSendSMS.setEnabled(false);
        }
        else if(button.equals(binding.btnVerifyCode)){
            binding.btnVerifyCode.setBackgroundColor(Color.parseColor("#5E5E5E"));
            binding.btnVerifyCode.setEnabled(false);
        }
    }


    /* 버튼 Lock푸는 함수 */
    private void ButtonRelease(Button button){
        if(button.equals(binding.btnSendSMS)){
            binding.btnSendSMS.setBackgroundColor(Color.parseColor("#0D70E6"));
            binding.btnSendSMS.setEnabled(true);
        }
        else if(button.equals(binding.btnVerifyCode)){
            binding.btnVerifyCode.setBackgroundColor(Color.parseColor("#0D70E6"));
            binding.btnVerifyCode.setEnabled(true);
        }
    }

    private void startPhoneNumberVerification(String phoneNum){
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(firebaseAuth)
                .setPhoneNumber(phoneNum)
                .setTimeout(120L, TimeUnit.SECONDS)
                .setActivity(this)
                .setCallbacks(mCallbacks)
                .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }


}
