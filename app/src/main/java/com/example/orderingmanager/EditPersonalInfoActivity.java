package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.Toast;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.request.MemberIdDto;
import com.example.orderingmanager.databinding.ActivityEditPersonalInfoBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;

import lombok.SneakyThrows;

public class EditPersonalInfoActivity extends BasicActivity {

    private ActivityEditPersonalInfoBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initButtonClickListener();

    }

    private void initButtonClickListener(){
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        binding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccount();
            }
        });

        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });
    }

    /* 로그아웃 */
    public void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        finish();
    }

    /* 회원탈퇴 */
    public void deleteAccount() {
        try {
            MemberIdDto memberIdDto = new MemberIdDto(UserInfo.getOwnerId());

            URL url = new URL("http://www.ordering.ml/api/owner/"+ UserInfo.getOwnerId().toString());
            HttpApi httpApi = new HttpApi(url, "DELETE");

            new Thread() {
                @SneakyThrows
                public void run() {
                    String json = httpApi.requestToServer(memberIdDto);
                    ObjectMapper mapper = new ObjectMapper();
                    ResultDto<Boolean> result = mapper.readValue(json, new TypeReference<ResultDto<Boolean>>() {});


                    if(result.getData() != null){
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showToast(EditPersonalInfoActivity.this,"회원탈퇴 되었습니다.");
                                startActivity(new Intent(EditPersonalInfoActivity.this,LoginActivity.class));
                                finish();
                            }
                        });
                    }
                    else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                showToast(EditPersonalInfoActivity.this,"서버 연결에 문제가 발생했습니다.");
                            }
                        });
                    }
                }
            }.start();

        } catch (Exception e) {
            showToast(EditPersonalInfoActivity.this,"서버 연결에 문제가 발생했습니다.");
        }
    }
}