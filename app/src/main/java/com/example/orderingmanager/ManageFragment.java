package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.orderingmanager.Dto.FoodCategory;
import com.example.orderingmanager.Dto.RestaurantType;
import com.example.orderingmanager.databinding.FragmentManageBinding;

public class ManageFragment extends Fragment {

    private View view;
    private FragmentManageBinding binding;

    Bundle extra;

    Boolean storeInitInfo;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initClickListener();
        initView();
        storeInfoCheck();

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

    private void initClickListener() {
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

        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 개인정보 수정 버튼이 할 일
            }
        });

        binding.btnStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditStoreInfoActivity.class));
            }
        });
    }

    public void storeInfoCheck() {
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if (!storeInitInfo) {
            Log.e("manageFrag", storeInitInfo.toString());
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        } else {
            Log.e("manageFrag", storeInitInfo.toString());
            binding.errorNotFound.setVisibility(View.GONE);
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
            switch (view.getId()) {
                case R.id.refreshImageButton:
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };


}