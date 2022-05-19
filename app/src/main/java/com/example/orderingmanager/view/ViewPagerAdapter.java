package com.example.orderingmanager.view;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import com.example.orderingmanager.view.ManageFragment.MenuManageFragment;
import com.example.orderingmanager.view.ManageFragment.ReviewManageFragment;
import com.example.orderingmanager.view.QRFragment.QrTableFragment;
import com.example.orderingmanager.view.QRFragment.QrTakeoutFragment;
import com.example.orderingmanager.view.QRFragment.QrWaitingFragment;

public class  ViewPagerAdapter extends FragmentStateAdapter {
    int num, count;
    public ViewPagerAdapter(@NonNull FragmentActivity fragmentActivity,int num, int count) {
        super(fragmentActivity);
        this.num = num;
        this.count = count;
    }

    @NonNull
    @Override
    public Fragment createFragment(int position) {
        if (num == 0) {
            switch (position) {
                case 0:
                    return new MenuManageFragment();

                default:
                    return new ReviewManageFragment();
            }
        }
        else{
            switch (position) {
                case 0:
                    return new QrTakeoutFragment();
                case 1:
                    return new QrWaitingFragment();
                default:
                    return new QrTableFragment();
            }
        }
    }

    @Override
    public int getItemCount() {
        return count;
    }

    
}
