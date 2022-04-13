package com.example.orderingmanager.view.QRFragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.FragmentQrWaitingBinding;

public class QrWaitingFragment extends Fragment {

    private View view;

    //viewbinding
    private FragmentQrWaitingBinding binding;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_qr_waiting, container, false);
        binding = FragmentQrWaitingBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initView();
        initButtonListener();

        return view;
    }

    private void initView(){
        String title = "웨이팅QR";
        String secondary = "웨이팅용 QR코드입니다";
        binding.itemQrCardview.ivQrcodeCardview.setImageBitmap(QrList.getQrBitmap(1));
        binding.itemQrCardview.tvCardviewTitle.setText(title);
        binding.itemQrCardview.tvCardviewSecondary.setText(secondary);
    }
    private void initButtonListener(){
        binding.itemQrCardview.btnPreview.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(),QrPreviewActivity.class);
                intent.putExtra("cardViewType",1);
                startActivity(intent);
            }
        });
    }
}