package com.example.orderingmanager.Dialog;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.example.orderingmanager.databinding.CustomDialogBinding;

public class CustomDialog extends Dialog {
    private CustomDialogBinding binding;
    private View.OnClickListener positiveButton;
    private View.OnClickListener negativeButton;

    String title;
    String contents;
    String positiveButtonText;
    String negativeButtonText;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        getWindow().requestFeature(Window.FEATURE_NO_TITLE);
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        binding = CustomDialogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        TextView titleText = binding.dialogTitleTextView;
        TextView messages = binding.dialogMessagesTextView;
        Button positive_btn = binding.positiveBtn;
        Button negative_btn = binding.negativeBtn;

        titleText.setText(title);
        messages.setText(contents);
        positive_btn.setText(positiveButtonText);
        negative_btn.setText(negativeButtonText);

        positive_btn.setOnClickListener(positiveButton);
        negative_btn.setOnClickListener(negativeButton);
    }

    public CustomDialog(@NonNull Context context,
                                 String title,
                                 String contents,
                                 String positiveButtonText,
                                 String negativeButtonText,
                                 View.OnClickListener positiveButton,
                        View.OnClickListener negativeButton){
        super(context);

        this.title = title;
        this.contents = contents;
        this.positiveButtonText = positiveButtonText;
        this.negativeButtonText = negativeButtonText;
        this.positiveButton = positiveButton;
        this.negativeButton = negativeButton;
    }
}
