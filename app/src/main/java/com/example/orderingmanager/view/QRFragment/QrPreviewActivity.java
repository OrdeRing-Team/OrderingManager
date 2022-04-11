package com.example.orderingmanager.view.QRFragment;

import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingmanager.databinding.ActivityQrPreviewBinding;

public class QrPreviewActivity extends AppCompatActivity {

    //viewbinding
    private ActivityQrPreviewBinding binding;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        Bitmap bitmap = QrFragment.getQrPreviewList(0);
        binding.ivQrPreview.setImageBitmap(bitmap);

    }

}
