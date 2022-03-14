package com.example.orderingmanager;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.databinding.ActivityCreateQrBinding;
import com.example.orderingmanager.databinding.ActivityInfoBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CreateQR extends BasicActivity {

    Animation complete;

    private ActivityCreateQrBinding binding;

    private String text;

    MultiFormatWriter multiFormatWriter;

    // 임시 변수
    static int table_count = 20;

    static int countNumber = 0;
    static double PROGRESS_MAX = table_count + 2;

    double progress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        binding = ActivityCreateQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        complete = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);

        multiFormatWriter = new MultiFormatWriter();

        progress = 0;
        binding.progressBar.setProgress(0);

        CreateTakeoutQR();
        CreateWaitingQR();
        CreateTablesQR();

    }

    private void CreateTakeoutQR(){
        text = "https://www.ordering.com/uid/TakeoutQR";
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.qrcodeImage.setImageBitmap(bitmap);
            binding.progress1.setText("1");
            binding.progress1.setTypeface(null, Typeface.NORMAL);
            binding.progressText1.setTypeface(null, Typeface.NORMAL);;
            binding.progressSlash1.setTypeface(null, Typeface.NORMAL);;
            binding.progressEnd1.setTypeface(null, Typeface.NORMAL);;

            binding.ivComplete1.setVisibility(View.VISIBLE);
            binding.ivComplete1.startAnimation(complete);

            updateProgress();
        }catch (Exception e){}
    }

    private void CreateWaitingQR(){
        binding.progressTextLinearLayout2.setVisibility(View.VISIBLE);
        text = "도메인/uid/WaitingQR";
        try{
            BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE,200,200);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            binding.qrcodeImage.setImageBitmap(bitmap);
            binding.progress2.setText("1");
            binding.progress2.setTypeface(null, Typeface.NORMAL);
            binding.progressText2.setTypeface(null, Typeface.NORMAL);;
            binding.progressSlash2.setTypeface(null, Typeface.NORMAL);;
            binding.progressEnd2.setTypeface(null, Typeface.NORMAL);;

            binding.ivComplete2.setVisibility(View.VISIBLE);
            binding.ivComplete2.startAnimation(complete);

            updateProgress();
        }catch (Exception e){}
    }

    private void CreateTablesQR(){
        binding.progressTextLinearLayout3.setVisibility(View.VISIBLE);
        binding.progressEnd3.setText(Integer.toString(table_count));
        if(countNumber < table_count){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    countNumber += 1;
                    text = "https://ordering.com/sadasdasd/Table" + Integer.toString(countNumber);
                    try {
                        BitMatrix bitMatrix = multiFormatWriter.encode(text, BarcodeFormat.QR_CODE, 200, 200);
                        BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
                        Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
                        binding.qrcodeImage.setImageBitmap(bitmap);
                        binding.progress3.setText(Integer.toString(countNumber));

                        updateProgress();
                        Log.e("TableQRCreated :: ", text);
                    } catch (Exception e) { countNumber = table_count+1; }
                    if(countNumber < table_count){
                        CreateTablesQR();
                    }
                    else{
                        binding.progress3.setTypeface(null, Typeface.NORMAL);
                        binding.progressText3.setTypeface(null, Typeface.NORMAL);;
                        binding.progressSlash3.setTypeface(null, Typeface.NORMAL);;
                        binding.progressEnd3.setTypeface(null, Typeface.NORMAL);;

                        binding.ivComplete3.setVisibility(View.VISIBLE);
                        binding.ivComplete3.startAnimation(complete);
                    }
                }
            },200);
        }else{
            finish();
        }
        if(countNumber == table_count-1) waitForUploading();
    }

    private void waitForUploading(){
        binding.ivLoading.setVisibility(View.VISIBLE);
        binding.tvMaintext.setText("QR코드를 서버에 업로드하는 중입니다.");
        Glide.with(this).load(R.raw.uploading).into(binding.ivLoading);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                try {
                    Intent intent = new Intent(CreateQR.this, CreateQRSuccessActivity.class);
                    startActivity(intent);
                    BackWithAnim();
                } catch (Exception e) {}
            }
        },5000);
    }

    private void updateProgress(){
        progress += (1/PROGRESS_MAX*100);

        String progressText = String.format("%.0f", progress);

        binding.LoadingText.setText(progressText);
        binding.progressBar.setProgress((int)Math.round(progress));
    }
}
