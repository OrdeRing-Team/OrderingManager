package com.example.orderingmanager.view.ManageFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentMenuManageBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class MenuManageFragment extends Fragment {

    private View view;

    //viewbinding
    private FragmentMenuManageBinding binding;

    ArrayList<ManageData> menuList = new ArrayList<>();
    public Object position;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu_manage, container, false);
        binding = FragmentMenuManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        getMenuFromServer();
        return view;
    }

    public void getMenuFromServer(){
        // 매장 모든 음식 불러오기
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+UserInfo.getRestaurantId()+"/foods/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<FoodDto>>> call = service.getFood(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<List<FoodDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<FoodDto>>> call, Response<ResultDto<List<FoodDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<FoodDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            result.getData().forEach(foodDto ->{
                                                menuList.add(new ManageData(foodDto.getFoodId(), foodDto.getImageUrl(), foodDto.getFoodName(), Integer.toString(foodDto.getPrice()), foodDto.getMenuIntro()));
                                                Log.e("data = ", foodDto.getFoodName() + ", image url = " + foodDto.getImageUrl());
                                                Log.e("foodid = ", Long.toString(menuList.get(0).getFoodId()));
                                            });

                                            RecyclerView recyclerView = binding.rvMenu;
                                            ManageAdapter manageAdapter = new ManageAdapter(menuList, getActivity());
                                            recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                            recyclerView.setAdapter(manageAdapter);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<FoodDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }


//    public void menuAdd() {
//        binding.btnMenuAdd.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                startActivity(new Intent(getActivity(), MenuAddActivity.class));
//                getActivity().finish();
//            }
//        });
//    }

}