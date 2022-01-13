package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.google.android.material.textfield.TextInputEditText;
import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

import java.io.IOException;
import com.bumptech.glide.Glide;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Callback;
import okhttp3.FormBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.ResponseBody;
import okhttp3.internal.tls.OkHostnameVerifier;
import retrofit2.Call;
import retrofit2.Response;
import retrofit2.Retrofit;

public class SignupActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        String PhoneNum = ((TextInputEditText)findViewById(R.id.editTextPhoneSignup)).getText().toString();
        (findViewById(R.id.btn_sendSMS)).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                //int authNum = sendSMS("82",PhoneNum);
            }
        });

    }

// twilio는 따로 서버 구축하지 않는 이상 안되나봄...ㅠㅠ 파베 전화번호 인증으로 진행해야 할듯..
/*
    // Find your Account Sid and Token at twilio.com/user/account
    public static final String ACCOUNT_SID = "AC1f66b388bd759d969e8970546bceabbb";
    public static final String AUTH_TOKEN = "22519da7f71689053e9a7fd165f1b010";
    // SMS 전송

    private int sendSMS(String country, String phoneNum) {
        int authNum = randomRange(100000, 999999);
        String body = "<#>OrdeRing 본인인증 코드 [" + authNum + "] - 경상국립대 CS 오더링팀";
        String from = "+19377779817";
        String to = "+"+ country + phoneNum;

        String base64EncodedCredentials = "Basic " + Base64.encodeToString(
                (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(), Base64.NO_WRAP
        );

        Map<String, String> smsData = new HashMap<>();
        smsData.put("From", from);
        smsData.put("To", to);
        smsData.put("Body", body);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://api.twilio.com/2010-04-01/")
                .build();
        TwilioApi api = retrofit.create(TwilioApi.class);

        api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, smsData).enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                if (response.isSuccessful()) {
                    Log.d("TAG", "onResponse->success");
                }
                else Log.d("TAG", "onResponse->failure");
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.d("TAG", "onFailure");
            }
        });

        return authNum;
    }*/
    /*public static int sendSMS(String country, String phoneNum) {
        int authNum = randomRange(100000, 999999);
        String postURL = "https://api.twilio.com/2010-04-01/Accounts/"+ACCOUNT_SID+"/Messages";
        String body = "<#>OrdeRing 본인인증 코드 [" + authNum + "] - 경상국립대 CS 오더링팀";
        String from = "+19377779817";
        String to = "+"+ country + phoneNum;

        try {
            String base64EncodedCredentials = "Basic "
                    + Base64.encodeToString(
                    (ACCOUNT_SID + ":" + AUTH_TOKEN).getBytes(),
                    Base64.NO_WRAP);

            RequestBody formBody = new FormBody.Builder()
                    .add("From", from)  // number we get from Twilio
                    .add("To", to) //TODO  targetPhoneNumber
                    .add("Body", body)
                    .build();

            OkHttpClient client = new OkHttpClient();
            Request request = new Request.Builder()
                    .header("Authorization ", base64EncodedCredentials)
                    .url(postURL)
                    .post(formBody)
                    .build();

            client.newCall(request).enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                    e.printStackTrace();
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    //print errors if code is not 200
                }
            });

        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return authNum;
    }
    public static int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }*/
    /*public static int sendSMS (String country, String phoneNum) {

        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);

        // 휴대폰 인증번호 생성
        int authNum = randomRange(100000, 999999);


        // 전송대상 휴대폰 번호
        String sendTarget = "+"+ country + phoneNum;

        // 전송 메세지
        String authMsg = "<#>OrdeRing 본인인증 코드 [" + authNum + "] - 경상국립대 CS 오더링팀" ;


        Message message = Message.creator(
                // to
                new PhoneNumber(sendTarget),
                // from
                new PhoneNumber("+19377779817"),
                // message
                authMsg).create();

        return authNum;

    }

    // 인증번호 범위 지정
    public static int randomRange(int n1, int n2) {
        return (int) (Math.random() * (n2 - n1 + 1)) + n1;
    }*/
}
