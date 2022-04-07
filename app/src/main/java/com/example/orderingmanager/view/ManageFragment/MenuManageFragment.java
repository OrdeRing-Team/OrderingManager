package com.example.orderingmanager.view.ManageFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.view.BasicActivity;
import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.HttpApi;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentMenuManageBinding;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;


public class MenuManageFragment extends Fragment {

    private View view;

    //viewbinding
    private FragmentMenuManageBinding binding;

    ArrayList<ManageData> menuList = new ArrayList<>();

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu_manage, container, false);
        binding = FragmentMenuManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        menuAdd();
        getMenuFromServer();
        return view;
    }

    public void getMenuFromServer(){
        try {
            URL url = new URL("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/foods");
            HttpApi httpApi = new HttpApi(url, "POST");

            new Thread() {
                @SneakyThrows
                public void run() {
                    String json = httpApi.requestToServer();
                    ObjectMapper mapper = new ObjectMapper();
                    ResultDto<List<FoodDto>> result = mapper.readValue(json, new TypeReference<ResultDto<List<FoodDto>>>() {});
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            result.getData().forEach(foodDto ->{
                                    menuList.add(new ManageData(foodDto.getFoodName(), Integer.toString(foodDto.getPrice()), foodDto.getMenuIntro()));
                                Log.e("data = ", foodDto.getFoodName());

                            });
                            RecyclerView recyclerView = binding.rvMenu;
                            ManageAdapter manageAdapter = new ManageAdapter(menuList, getActivity());
                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity())) ;
                            recyclerView.setAdapter(manageAdapter);
                        }
                    });

                }
            }.start();

        } catch (Exception e) {
            BasicActivity.showToast(getActivity(),"서버 요청에 실패하였습니다.");
            Log.e("e = " , e.getMessage());
        }
    }

    public void menuAdd() {
        binding.btnMenuAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MenuAddActivity.class));
            }
        });
    }


    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }




}