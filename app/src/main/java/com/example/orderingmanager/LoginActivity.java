package com.example.orderingmanager;

import static com.example.orderingmanager.Utillity.showToast;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingmanager.databinding.ActivityLoginBinding;
import com.example.orderingmanager.databinding.ActivitySignupBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class LoginActivity extends BasicActivity {

    //viewbinding
    private ActivityLoginBinding binding;

    // 파이어베이스 인증
    private FirebaseAuth mAuth;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityLoginBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null){
            startActivity(new Intent(LoginActivity.this, MainActivity.class));
            finish();
        }
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
                String MemberId = binding.etMemberId.getText().toString();
                String Password = binding.editTextPasswordLogin.getText().toString();

                // 로그인 조건 처리
                if (MemberId.length() > 0 && Password.length() > 0) {
                    mAuth.signInWithEmailAndPassword(MemberId, Password)
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
                                        showLoginErrorPopup();
                                    }
                                    // ...
                                }
                            });

                } else {
                    showToast(LoginActivity.this,"아이디와 비밀번호를 입력해주세요.");


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
