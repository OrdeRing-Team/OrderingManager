package com.example.orderingmanager.view.login_register;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityLoginBinding;
import com.example.orderingmanager.view.BasicActivity;
import com.example.orderingmanager.view.MainActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class LoginActivity extends BasicActivity {

    //viewbinding
    private ActivityLoginBinding binding;

    ResultDto<OwnerSignInResultDto> result;

    String memberId, password;
    SharedPreferences loginSP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // UserInfo 초기화
        UserInfo.init();

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // 자동로그인
        if (getLocalData()) {
            binding.etMemberId.setText(memberId);
            binding.etPassword.setText(password);
            binding.cbAutologin.setChecked(true);
            login();
        }

        initButtonClickListener();
    }

    private void initButtonClickListener() {
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
                memberId = binding.etMemberId.getText().toString();
                password = binding.etPassword.getText().toString();
                // 로그인 조건 처리
                if (memberId.length() > 0 && password.length() > 0) {
                    login();
                } else {
                    Toast.makeText(LoginActivity.this, "아이디와 비밀번호를 모두 입력해주세요.", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void login() {
        try {
            showProgress();
            FirebaseMessaging.getInstance().getToken()
                    .addOnCompleteListener(new OnCompleteListener<String>() {
                        @Override
                        public void onComplete(@NonNull Task<String> task) {
                            if (!task.isSuccessful()) {
                                Log.e("토큰 조회", "Fetching FCM registration token failed", task.getException());
                                return;
                            }

                            String token = task.getResult();

                            SignInDto signInDto = new SignInDto(memberId, password, token);

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

                                            if (response.isSuccessful()) {
                                                result = response.body();
                                                if (result.getData() != null) {
                                                    // 아이디 비밀번호 일치할 때
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            UserInfo.setUserInfo(result.getData());
                                                            Intent intent = new Intent(LoginActivity.this, MainActivity.class);

                                                            if (result.getData().getRestaurantId() == null) {
                                                                // 매장정보입력이 안되어 있을때
                                                                Log.e("getUserId : ", result.getData().getOwnerId().toString());
                                                                Log.e("getRestaurantId : ", result.getData().getRestaurantId() != null ? result.getData().getRestaurantId().toString() : "null");

                                                                //intent.putExtra("restaurantId", false);
                                                            } else {
                                                                // 매장정보입력이 완료된 상태
                                                                UserInfo.setRestaurantInfo(result.getData());

                                                            }
                                                            // 자동로그인 여부
                                                            if (binding.cbAutologin.isChecked()) {
                                                                // 자동로그인 체크되어있으면
                                                                // SharedPreferences 에 로그인 정보를 저장
                                                                loginSP = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);

                                                                SharedPreferences.Editor autoLoginEdit = loginSP.edit();
                                                                autoLoginEdit.putString("signInId", memberId);
                                                                autoLoginEdit.putString("password", password);
                                                                autoLoginEdit.commit();
                                                            }
                                                            UserInfo.setUserId(memberId);
                                                            UserInfo.setUserPW(password);
                                                            startActivity(intent);
                                                            finish();
                                                        }
                                                    });
                                                } else {
                                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                        @Override
                                                        public void run() {
                                                            Log.e("로그인 실패 ! ", "아이디 혹은 비밀번호 일치하지 않음");
                                                            hideProgress();
                                                            showLoginErrorPopup();
                                                        }
                                                    });
                                                }
                                            }else{
                                                Log.e("로그인", "is not successful");
                                                Log.e("로그인 실패 ! ", "아이디 혹은 비밀번호 일치하지 않음");
                                                hideProgress();
                                            }
                                        }

                                        @Override
                                        public void onFailure(Call<ResultDto<OwnerSignInResultDto>> call, Throwable t) {
                                            Toast.makeText(LoginActivity.this, "로그인 도중 일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                            hideProgress();
                                            Log.e("e = ", t.getMessage());
                                        }
                                    });
                                }
                            }.start();
                            String msg = getString(R.string.msg_token_fmt, token);
                            Log.e("token Log", msg);
                        }
                    });
        } catch (Exception e) {
            Toast.makeText(LoginActivity.this, "로그인 도중 일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = ", e.getMessage());
        }

    }

    private void showLoginErrorPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("로그인 실패").setMessage("아이디와 비밀번호를 다시 확인해 주세요.");
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private boolean getLocalData() {
        loginSP = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        memberId = loginSP.getString("signInId", null);
        password = loginSP.getString("password", null);

        return (memberId != null && password != null);
    }

    private void showProgress() {
        binding.progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress() {
        binding.progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
