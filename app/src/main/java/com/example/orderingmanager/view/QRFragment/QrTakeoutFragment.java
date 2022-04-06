package com.example.orderingmanager.view.QRFragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.FragmentQrTakeoutBinding;

public class QrTakeoutFragment extends Fragment {

    private View view;

    //viewbinding
    private FragmentQrTakeoutBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_takeout, container, false);
        binding = FragmentQrTakeoutBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        return view;
    }
}