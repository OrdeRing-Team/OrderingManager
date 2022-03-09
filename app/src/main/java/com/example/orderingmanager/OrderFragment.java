package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orderingmanager.databinding.FragmentOrderBinding;
import com.example.orderingmanager.databinding.FragmentQrBinding;

public class OrderFragment extends Fragment {
    private View view;
    private FragmentOrderBinding binding;

    Bundle extra;

    Boolean storeInitInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            // 매장정보 입력 여부
            storeInitInfo = extra.getBoolean("StoreInitInfo");

            /* 이곳에 받아올 데이터를 추가하십셩 */
        }

        storeInfoCheck();

        return view;
    }


    public void storeInfoCheck(){
        if(!storeInitInfo){
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        }
        else{
            binding.orderFragment.setVisibility(View.VISIBLE);
        }
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
                case R.id.refreshImageButton:
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };
}