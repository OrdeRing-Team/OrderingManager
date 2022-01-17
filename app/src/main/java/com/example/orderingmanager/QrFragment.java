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
import com.google.firebase.firestore.FirebaseFirestore;

public class QrFragment extends Fragment {
    private View view;

    //viewbinding
    private FragmentQrBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_qr, container, false);

        binding = FragmentQrBinding.inflate(getLayoutInflater());

        // user 는 현재 로그인 된 사용자 정보 가져오실 때 사용하심 됩니다!!
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();


        // firestore 는 DB 접근할 때 사용하심 됩니당!!
        FirebaseFirestore firestore = FirebaseFirestore.getInstance();
        /*  각 사용자의 DB 문서명은 사용자의 uid로 구성되기 때문에 DB에 접근하고자 할 때는 user.getUid() 로 접근하시면 됩니다.
            파이어베이스 문서 참고 : https://firebase.google.com/docs/firestore/quickstart?authuser=0#java */


        binding.tvEmail.setText(user.getEmail());
        binding.tvPhoneNum.setText(user.getPhoneNumber());

        view.findViewById(R.id.btn_logout).setOnClickListener(onClickListener);
        view.findViewById(R.id.btn_deleteAccount).setOnClickListener(onClickListener);


        return view;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
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

    /* 로그아웃 함수입니다. */
    public void logout() {
        FirebaseAuth.getInstance().signOut();

        startActivity(new Intent(getActivity(), StartActivity.class));
        Toast.makeText(getActivity(), "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();
        getActivity().finish();
    }

    /* 회원탈퇴 함수입니다. */
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