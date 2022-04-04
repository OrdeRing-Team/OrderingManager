package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.databinding.FragmentManageBinding;

public class ManageFragment extends Fragment {

    private View view;
    private FragmentManageBinding binding;

    Boolean storeInitInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        // 새로고침
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();

        initButtonClickListener();

        storeInfoCheck();

        if(UserInfo.getRestaurantId() != null) {
            initView();
        }
        return view;
    }

    private void initView() {
        // 점주용은 닉네임 없애기로 했어! 필요 없을것 같아서 ㅎ ㅎ ㅎ.......
        // 그래서 별명 자리에 매장명 크게 보이는게 좋을것 같아서 수정했숨당!
        binding.tvNikname.setText(UserInfo.getRestaurantName());

        // 매장명
        binding.tvStoreName.setText(UserInfo.getRestaurantName());

        // 사업자명
        binding.tvName.setText(UserInfo.getOwnerName());

        // 매장주소
        binding.tvAddress.setText(UserInfo.getAddress());

        // 매장종류
        RestaurantType restaurantType = UserInfo.getRestaurantType();
        switch (restaurantType) {
            case ONLY_TO_GO:
                binding.tvMealMethod.setText("포장");
                break;
            case FOR_HERE_TO_GO:
                binding.tvMealMethod.setText("매장식사, 포장");
                break;
        }
        // 카테고리
        FoodCategory foodCategory = UserInfo.getFoodCategory();
        switch (foodCategory) {
            case PIZZA: binding.tvCategory.setText("피자");break;
            case BUNSIK: binding.tvCategory.setText("분식");break;
            case CHICKEN: binding.tvCategory.setText("치킨");break;
            case KOREAN_FOOD: binding.tvCategory.setText("한식");break;
            case CAFE_DESSERT: binding.tvCategory.setText("카페/디저트");break;
            case PORK_CUTLET_ROW_FISH_SUSHI: binding.tvCategory.setText("돈가스/회/초밥");break;
            case FAST_FOOD: binding.tvCategory.setText("패스트푸드");break;
            case JJIM_TANG: binding.tvCategory.setText("찜/탕");break;
            case CHINESE_FOOD: binding.tvCategory.setText("중국집");break;
            case JOKBAL_BOSSAM: binding.tvCategory.setText("족발/보쌈");break;
            case ASIAN_FOOD_WESTERN_FOOD: binding.tvCategory.setText("아시안/양식");break;
        }
    }

    private void initButtonClickListener() {
        binding.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), StoreManageActivity.class));
                //getActivity().finish();
            }
        });

        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditPersonalInfoActivity.class));
            }
        });

        binding.btnStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditStoreInfoActivity.class);
                startActivity(intent);
                // 저장 후 화면을 갱신하기 위해 startActivityForResult 로 호출
                // 이 호출함수는 나중에 돌아오면 MainActivity 의 onActivityResult 함수 에서 받는다.
                //startActivityForResult(intent,MainActivity.MANAGEFRAGMENT);
            }
        });

        binding.viewErrorLoadStore.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }

    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("ManageFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            binding.manageFragment.setVisibility(View.GONE);
        }
        else{
            Log.e("ManageFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.manageFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 정보 수정이 이루어지고 fragment 로 다시 돌아왔을때는 onResume 이 호출된다
        // 뷰를 새로 다시 세팅해준다.

        if(UserInfo.getRestaurantId() != null) {
            initView();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }



}