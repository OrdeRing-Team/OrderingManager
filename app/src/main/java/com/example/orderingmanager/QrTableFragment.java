package com.example.orderingmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.databinding.FragmentQrTableBinding;


public class QrTableFragment extends Fragment {

    private View view;

    //viewbinding
    private FragmentQrTableBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_table, container, false);
        binding = FragmentQrTableBinding.inflate(inflater, container, false);
        view = binding.getRoot();


        return view;
    }
}