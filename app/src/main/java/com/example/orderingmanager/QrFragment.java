package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.databinding.FragmentQrBinding;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

public class QrFragment extends Fragment {
    private View view;

    //viewbinding
    private FragmentQrBinding binding;

    Bundle extra;

    Boolean storeInitInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentQrBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            // 매장정보 입력 여부
            storeInitInfo = extra.getBoolean("StoreInitInfo");

            /* 이곳에 받아올 데이터를 추가하십셩 */
        }

        initButtonClickListener();
        storeInfoCheck();

        return view;
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
                case R.id.refreshImageButton:
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };

    public void storeInfoCheck(){
        if(!storeInitInfo){
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        }
    }

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