package com.example.orderingmanager.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.RadioGroup;

import androidx.annotation.NonNull;

import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.CustomDialogOrderCancelBinding;

public class CustomDialogOrderCancel extends Dialog {
    private CustomDialogOrderCancelBinding binding;
    private View.OnClickListener positiveButton;
    private View.OnClickListener negativeButton;

    String reason = "";
    ProgressBar progressBar;
    int absolutePosition;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomDialogOrderCancelBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Button positive_btn = binding.btnCancelOrder;
        Button negative_btn = binding.btnClose;

        positive_btn.setOnClickListener(positiveButton);
        negative_btn.setOnClickListener(negativeButton);

        progressBar = findViewById(R.id.progressbar_order_cancel);
        initButtonListener();
    }

    public CustomDialogOrderCancel(@NonNull Context context,
                                   View.OnClickListener positiveButton,
                                   View.OnClickListener negativeButton,
                                   int absolutePosition) {
        super(context);

        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
        this.absolutePosition = absolutePosition;
    }

    public void initButtonListener() {
        binding.rgCancelReason.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch (checkedId) {
                    case R.id.reason_order_end:
                        reason = "주문마감";
                        break;
                    case R.id.reason_exhaustion:
                        reason = "재료소진";
                        break;
                    case R.id.reason_etc:
                        reason = "기타";
                        break;
                }
            }
        });
    }

    public String getReason() {
        return reason;
    }

    public int getAbsolutePosition(){
        return absolutePosition;
    }

    public void showProgress(){
        progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public void hideProgress(){
        progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }
}
