package com.example.orderingmanager.view.login_register;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityLoginBinding;
import com.example.orderingmanager.view.BasicActivity;
import com.example.orderingmanager.view.MainActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends BasicActivity {

    //viewbinding
    private ActivityLoginBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UserInfo 초기화
        UserInfo.init();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initButtonClickListener();

    }

    private void initButtonClickListener(){
        binding.textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(intent);
                finish();
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String memberId = binding.etMemberId.getText().toString();
                String password = binding.editTextPasswordLogin.getText().toString();

                // 로그인 조건 처리
                if (memberId.length() > 0 && password.length() > 0) {
                    try {
                        SignInDto signInDto = new SignInDto(memberId, password);

//                        URL url = new URL("http://www.ordering.ml/api/owner/signin");
//                        HttpApi httpApi = new HttpApi(url, "POST");

                        new Thread() {
                            @SneakyThrows
                            public void run() {
                                // login

                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://www.ordering.ml/api/owner/signin/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<ResultDto<OwnerSignInResultDto>> call = service.ownerSignIn(signInDto);

                                call.enqueue(new Callback<ResultDto<OwnerSignInResultDto>>() {
                                    @Override
                                    public void onResponse(Call<ResultDto<OwnerSignInResultDto>> call, Response<ResultDto<OwnerSignInResultDto>> response) {

                                        ResultDto<OwnerSignInResultDto> result = response.body();
                                        if (response.isSuccessful()) {
                                            if(result.getData() != null){
                                                // 아이디 비밀번호 일치할 때
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        UserInfo.setUserInfo(result.getData());
                                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                                        if(result.getData().getRestaurantId() == null){
                                                            // 매장정보입력이 안되어 있을때
                                                            Log.e("getOwnerId : ",result.getData().getOwnerId().toString());
                                                            Log.e("getRestaurantId : ",result.getData().getRestaurantId() != null ? result.getData().getRestaurantId().toString() : "null");

                                                            //intent.putExtra("restaurantId", false);
                                                        }
                                                        else{
                                                            // 매장정보입력이 완료된 상태
                                                            UserInfo.setRestaurantInfo(result.getData());
                                                        }
                                                        UserInfo.setUserId(binding.etMemberId.getText().toString());
                                                        startActivity(intent);
                                                        finish();
                                                    }
                                                });
                                            }
                                            else{
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @Override
                                                    public void run() {
                                                        Log.e("로그인 실패 ! ", "아이디 혹은 비밀번호 일치하지 않음");
                                                        showLoginErrorPopup();
                                                    }
                                                });
                                            }
                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultDto<OwnerSignInResultDto>> call, Throwable t) {
                                        Toast.makeText(LoginActivity.this,"로그인 도중 일시적인 오류가 발생하였습니다.",Toast.LENGTH_LONG).show();
                                        Log.e("e = " , t.getMessage());
                                    }
                                });


//                                String json = httpApi.requestToServer(signInDto);
//                                ObjectMapper mapper = new ObjectMapper();
//                                ResultDto<OwnerSignInResultDto> result = mapper.readValue(json, new TypeReference<ResultDto<OwnerSignInResultDto>>() {});
//
//
//                                if(result.getData() != null){
//                                    // 아이디 비밀번호 일치할 때
//                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            UserInfo.setUserInfo(result.getData());
//                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//
//
//                                            if(result.getData().getRestaurantId() == null){
//                                                // 매장정보입력이 안되어 있을때
//                                                Log.e("getOwnerId : ",result.getData().getOwnerId().toString());
//                                                Log.e("getRestaurantId : ",result.getData().getRestaurantId() != null ? result.getData().getRestaurantId().toString() : "null");
//
//                                                //intent.putExtra("restaurantId", false);
//                                            }
//                                            else{
//                                                // 매장정보입력이 완료된 상태
//                                                UserInfo.setRestaurantInfo(result.getData());
//                                            }
//                                            UserInfo.setUserId(binding.etMemberId.getText().toString());
//                                            startActivity(intent);
//                                            finish();
//                                        }
//                                    });
//                                }
//                                else{
//                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
//                                        @Override
//                                        public void run() {
//                                            Log.e("로그인 실패 ! ", "아이디 혹은 비밀번호 일치하지 않음");
//                                            showLoginErrorPopup();
//                                        }
//                                    });
//                                }
                            }
                        }.start();

                    } catch (Exception e) {
                        Toast.makeText(LoginActivity.this,"로그인 도중 일시적인 오류가 발생하였습니다.",Toast.LENGTH_LONG).show();
                        Log.e("e = " , e.getMessage());
                    }
                } else {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 모두 입력해주세요.",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void showLoginErrorPopup(){
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("로그인 실패").setMessage("아이디와 비밀번호를 다시 확인해 주세요.");
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener(){
            @Override
            public void onClick(DialogInterface dialog, int id)
            {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }
}
