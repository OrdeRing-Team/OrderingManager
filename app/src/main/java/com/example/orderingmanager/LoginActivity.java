package com.example.orderingmanager;

import static com.example.orderingmanager.Utillity.showToast;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingmanager.databinding.ActivityLoginBinding;
import com.example.orderingmanager.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

// 로그인 액티비티에서는 화면전환 효과를 적용하지 않기 이해 AppCompatActivity 상속
public class LoginActivity extends AppCompatActivity {

    //viewbinding
    private ActivityLoginBinding binding;

    // 파이어베이스 인증
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();

        initButtonClickListener();


    }

    private void initButtonClickListener(){
        binding.textViewSignup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
                startActivity(intent);
            }
        });

        binding.btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String Email = binding.editTextEmailLogin.getText().toString();
                String Password = binding.editTextPasswordLogin.getText().toString();

                // 로그인 조건 처리
                if (Email.length() > 0 && Password.length() > 0) {
                    mAuth.signInWithEmailAndPassword(Email, Password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        if(mAuth.getCurrentUser() != null){
                                            startActivity(new Intent(LoginActivity.this, MainActivity.class));
                                            showToast(LoginActivity.this,"로그인이 정상적으로 되었습니다.");
                                            finish();
                                        }
                                    } else {
                                        showToast(LoginActivity.this,"이메일과 비밀번호를 다시 확인해 주세요.");
                                    }
                                    // ...
                                }
                            });

                } else {
                    showToast(LoginActivity.this,"이메일과 비밀번호를 입력해주세요.");


                }
            }
        });
    }
}
