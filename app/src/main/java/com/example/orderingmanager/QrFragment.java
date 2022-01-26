package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

import com.example.orderingmanager.databinding.FragmentQrBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.HashMap;
import java.util.Map;

public class QrFragment extends Fragment {
    private View view;

    //viewbinding
    private FragmentQrBinding binding;

    FirebaseUser user;
    FirebaseFirestore db;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        // user 는 현재 로그인 된 사용자 정보 가져오실 때 사용하심 됩니다!!
        user = FirebaseAuth.getInstance().getCurrentUser();

        // firestore 는 DB 접근할 때 사용하심 됩니당!!
        db = FirebaseFirestore.getInstance();
        /*  각 사용자의 DB 문서명은 사용자의 uid로 구성되기 때문에 해당 유저의 DB에 접근하고자 할 때는 user.getUid() 로 접근하시면 됩니다.
            파이어베이스 문서 참고 : https://firebase.google.com/docs/firestore/quickstart?authuser=0#java */

        initSetUserInfo();
        initButtonClickListener();

        return view;
    }

    private void initSetUserInfo(){
        DocumentReference docRef = db.collection("users").document(user.getUid());
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    Log.d("docRef", "DocumentSnapshot data: " + document.getData());
                    if (document.exists()) {
                        Log.e("document.getData().get(Email)", document.getData().get("이메일").toString());
                        if(document.getData().get("이메일") != null){
                            Log.e("dddddd",document.getData().get("이메일").toString());
                            Toast.makeText(getActivity(),document.getData().get("이메일").toString(),Toast.LENGTH_SHORT).show();
                            binding.tvEmail.setText(document.getData().get("이메일").toString());
                        }
                        if(document.getData().get("휴대폰번호") != null){
                            binding.tvPhoneNum.setText(document.getData().get("휴대폰번호").toString());
                        }
                    } else {
                        Log.d("docRef", "No such document");
                    }
                } else {
                    Log.d("docRef", "get failed with ", task.getException());
                }
            }
        });
    }

    private void initButtonClickListener(){
        binding.btnLogout.setOnClickListener(onClickListener);
        binding.btnDeleteAccount.setOnClickListener(onClickListener);


    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.btn_logout:
                    logout();
                    break;
                case R.id.btn_deleteAccount:
                    deleteAccount();
                    break;
            }
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



    /* 로그아웃 */
    public void logout() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getActivity(), LoginActivity.class));
        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    /* 회원탈퇴 */
    public void deleteAccount() {
        FirebaseAuth.getInstance().getCurrentUser().delete();
        AuthUI.getInstance()
                .delete(getActivity())
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Toast.makeText(getActivity(), "회원탈퇴가 정상적으로 처리되었습니다.", Toast.LENGTH_LONG).show();
                        getActivity().finish();
                        startActivity(new Intent(getActivity(), StartActivity.class));
                    }
                });
    }
}