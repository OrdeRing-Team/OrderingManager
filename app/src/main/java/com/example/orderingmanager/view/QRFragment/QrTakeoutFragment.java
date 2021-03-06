package com.example.orderingmanager.view.QRFragment;

import android.content.Intent;
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

        initView();
        initButtonListener();

        return view;
    }

    private void initView(){
        binding.itemQrCardview.ivQrcodeCardview.setImageBitmap(QrList.getQrBitmap(0));
    }

    private void initButtonListener(){
        binding.itemQrCardview.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),QrPreviewActivity.class);
                intent.putExtra("cardViewType",0);
                startActivity(intent);
            }
        });
    }
}