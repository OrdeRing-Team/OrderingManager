package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.databinding.FragmentManageBinding;

import java.util.ArrayList;

public class ManageFragment extends Fragment {

    private View view;
    private FragmentManageBinding binding;

    Bundle extra;

    Boolean storeInitInfo;

    //리사이클러뷰 관련 선언
    private Button btnMenuManage;
    private ArrayList<ManageData> arrayList;
    private ManageAdapter manageAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Button btnAdd;
    private Button btnAddMenu;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            // 매장정보 입력 여부
            storeInitInfo = extra.getBoolean("StoreInitInfo");

            /* 이곳에 받아올 데이터를 추가하십셩 */

            btnMenuManage = view.findViewById(R.id.btn_menu_manage);
            btnMenuManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), MenuManageActivity.class));
                    getActivity().finish();
                }
            });
        }

        storeInfoCheck();

        return view;
    }


    public void storeInfoCheck(){
        if(!storeInitInfo){
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.manageFragment.setVisibility(View.GONE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        }

        else{
            //메뉴입력화면 보이게 하기.
            binding.manageFragment.setVisibility(View.VISIBLE);
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