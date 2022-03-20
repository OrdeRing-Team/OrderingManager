package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.request.MemberIdDto;
import com.example.orderingmanager.databinding.FragmentQrBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.firebase.auth.FirebaseAuth;

import java.net.URL;

import lombok.SneakyThrows;

public class QrFragment extends Fragment {
    private View view;

    //viewbinding
    private FragmentQrBinding binding;

    Bundle extra;

    Boolean storeInitInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            /* 이곳에 받아올 데이터를 추가하십셩 */
        }

        binding = FragmentQrBinding.inflate(inflater, container, false);

        view = binding.getRoot();



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
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("qrFragment",storeInitInfo.toString());
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        }
        else{
            Log.e("qrFragment",storeInitInfo.toString());
            binding.errorNotFound.setVisibility(View.GONE);
            binding.qrFragment.setVisibility(View.VISIBLE);
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
        try {
            MemberIdDto memberIdDto = new MemberIdDto(UserInfo.getOwnerId());

            URL url = new URL("http://www.ordering.ml/api/owner");
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
                                MainActivity.showToast(getActivity(),"회원탈퇴 되었습니다.");
                                startActivity(new Intent(getActivity(),LoginActivity.class));
                                getActivity().finish();
                            }
                        });
                    }
                    else{
                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                            @Override
                            public void run() {
                                MainActivity.showToast(getActivity(),"서버 연결에 문제가 발생했습니다.");
                            }
                        });
                    }
                }
            }.start();

        } catch (Exception e) {
            MainActivity.showToast(getActivity(),"서버 연결에 문제가 발생했습니다.");
        }
    }

}