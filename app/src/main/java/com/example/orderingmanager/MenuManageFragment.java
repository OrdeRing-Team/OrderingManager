package com.example.orderingmanager;

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

import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.ResultDto;
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

        //리사이클러뷰 임시 데이터
//        ArrayList<ManageData> arrayList = new ArrayList<>();
//        arrayList.add(new ManageData("후라이드 치킨", "18000"));
//        arrayList.add(new ManageData("양념 치킨", "20000"));
//        arrayList.add(new ManageData("간장 치킨", "20000"));
//        arrayList.add(new ManageData("포테이토 피자", "15000"));
//        arrayList.add(new ManageData("불고기 피자", "15000"));
//        arrayList.add(new ManageData("후라이드 치킨 반 + 양념 치킨 반", "20000"));
//        arrayList.add(new ManageData("양념 치킨 반 + 간장 치킨 반", "22000"));
//        arrayList.add(new ManageData("양념 치킨 + 포테이토 피자", "32000"));
//        arrayList.add(new ManageData("후라이드 치킨 + 포테이토 피자", "30000"));
//        arrayList.add(new ManageData("간장 치킨 + 불고기 피자", "32000"));

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
                                    menuList.add(new ManageData(foodDto.getFoodName(), Integer.toString(foodDto.getPrice())));
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

}