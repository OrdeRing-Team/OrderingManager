package com.example.orderingmanager.view.OrderFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentOrderBinding;
import com.example.orderingmanager.view.MainActivity;
import com.example.orderingmanager.view.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

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

            //뷰페이저 세팅
            ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), 2,2);
            binding.orderViewPager.setAdapter(adapter);

            new TabLayoutMediator(binding.tabLayoutOrder, binding.orderViewPager,
                    new TabLayoutMediator.TabConfigurationStrategy() {
                        @Override
                        public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                            switch (position){
                                case 0:
                                    tab.setText("주문현황");
                                    break;
                                default:
                                    tab.setText("웨이팅현황");
                                    break;
                            }
                        }
                    }).attach();
        }

        initButtonClickListener();
        storeInfoCheck();

        return view;
    }


    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("OrderFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            binding.orderFragment.setVisibility(View.GONE);
        }
        else{
            Log.e("OrderFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.orderFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void initButtonClickListener(){
        binding.viewErrorLoadStore.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });
    }
}