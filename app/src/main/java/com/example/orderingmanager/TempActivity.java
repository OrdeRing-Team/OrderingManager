package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;

import com.example.orderingmanager.databinding.ActivityTempBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class TempActivity extends BasicActivity {

    Intent intent;

    //viewbinding
    private ActivityTempBinding binding;

    // 파이어베이스 인증
    private FirebaseAuth mAuth;

    private static final String TAG = "TempActivity_TAG";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityTempBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        intent = getIntent();
        String phoneNum = intent.getStringExtra("phoneNum");
        binding.etPhoneNum.setText(phoneNum);

        mAuth = FirebaseAuth.getInstance();

        binding.button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(phoneNum);
            }
        });
    }


    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
    }


    /* 이메일 계정 생성 */
    private void createAccount(String phoneNum) {
        String memberId = binding.etMemberId.getText().toString();
        String Password = binding.editTextPassword.getText().toString();

        // 이메일 계정 생성 시작
        if (memberId.length() > 0 && Password.length() > 0) {
            mAuth.createUserWithEmailAndPassword(memberId, Password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG, "계정 생성 성공");
                                FirebaseUser user = mAuth.getCurrentUser();
                                updateUI(user, phoneNum);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                updateUI(null, phoneNum);
                            }
                        }
                    });
        }
    }
    private void updateUI(FirebaseUser user, String phoneNum) {

        updateDB(user, phoneNum);

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
        FinishWithAnim();
    }

    private void updateDB(FirebaseUser user, String phoneNum) {

        FirebaseFirestore db = FirebaseFirestore.getInstance();

        //UserInfo userinfo = new UserInfo(name, birthyear, birthmonth, birthday,followerlist,followinglist,contents);

        Map<String, Object> userInfo = new HashMap<>();
        userInfo.put("이메일", user.getEmail());
        userInfo.put("전화번호", phoneNum);

        // 새로운 사용자 DB 생성
        db.collection("users")
                .add(userInfo)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d(TAG, user.getPhoneNumber() + "의 DB 생성 완료  ::  " + documentReference.getId());
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Log.w(TAG, "의 DB 생성 실패", e);
                    }
                });

    }


}
