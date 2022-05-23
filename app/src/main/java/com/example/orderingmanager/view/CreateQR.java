package com.example.orderingmanager.view;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.view.ManageFragment.EditStoreInfoActivity;
import com.example.orderingmanager.databinding.ActivityCreateQrBinding;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

public class CreateQR extends BasicActivity {

    Animation complete;

    private ActivityCreateQrBinding binding;

    private String text;

    MultiFormatWriter multiFormatWriter;

    int table_count;
    int countNumber = 0;
    double PROGRESS_MAX;

    double progress;

    int getIntentValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr);
        binding = ActivityCreateQrBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        complete = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);

        multiFormatWriter = new MultiFormatWriter();

        // EditStoreInfoActivity와 InfoActivity가 본 액티비티인 CreateQR을 공유한다.
        // 따라서 EditStoreInfoActivity 에서 넘어왔을 경우 인텐트로 값을 넘겨주었는데,
        // EDIT_ONLY : 포장이 선택됐을 경우
        // EDIT_BOTH : 매장식사, 포장을 선택하여 테이블 수를 입력했을 경우
        // EDIT_NONE : EditStoreInfoActivity에서 실행 되지 않고 InfoActivity에서 본 액티비티가 실행되었을 경우이다.

        getIntentValue = getIntent().getIntExtra("EditStoreInfo", EditStoreInfoActivity.EDIT_NONE);
        // InfoActivity에서 실행되면 intent로 넘겨받은 값이 존재하지 않기 때문에
        // EDIT_NONE을 default 값으로 설정하여 InfoActivity에서 실행되었음을 알린다.

        progress = 0;
        binding.progressBar.setProgress(0);

        getTableCount();
        CreateTakeoutQR();
        CreateWaitingQR();

        if(getIntentValue != EditStoreInfoActivity.EDIT_ONLY) {
            // 포장인 경우 테이블QR을 생성할 필요가 없으므로 포장만 예외로 한다.
            CreateTableQR();
        }
    }

    private void getTableCount(){
        table_count = UserInfo.getTableCount();
        PROGRESS_MAX = table_count + 2;
    }

    private void CreateTakeoutQR(){
        Log.e("RestaurantId : ", Long.toString(UserInfo.getRestaurantId()));
        text = "http://www.ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/takeout";
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
        text = "http://www.ordering.ml/"+ Long.toString(UserInfo.getRestaurantId()) +"/waiting";
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

            // 포장일때는 바로 업로딩 UI를 표시한다
            if(getIntentValue == EditStoreInfoActivity.EDIT_ONLY) waitForUploading();

        }catch (Exception e){}
    }

    private void CreateTableQR(){
        binding.progressTextLinearLayout3.setVisibility(View.VISIBLE);
        binding.progressEnd3.setText(Integer.toString(table_count));
        if(countNumber < table_count){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    countNumber += 1;
                    text = "http://www.ordering.ml/"+Long.toString(UserInfo.getRestaurantId())+"/table" + Integer.toString(countNumber);
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
                        CreateTableQR();
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
        binding.tvMaintext.setText("잠시만 기다려 주세요");
        Glide.with(this).load(R.raw.uploading).into(binding.ivLoading);
        new Handler().postDelayed(new Runnable(){
            @Override
            public void run(){
                try {
                    if(getIntentValue == EditStoreInfoActivity.EDIT_NONE) {
                        // EDIT_NONE인 경우(InfoActivity에서 넘어왔을 경우) CreateQrSuccessActivity를 실행
                        Intent intent = new Intent(CreateQR.this, CreateQRSuccessActivity.class);
                        startActivity(intent);
                        FinishWithAnim();
                    }
                    else{
                        showToast(CreateQR.this, "매장정보를 수정했습니다.");
                        // EditStoreInfoActivity 에서 넘어왔을 경우 EditStoreInfoActivity 로 Result 값을 보내주어 돌아간 뒤 onActivityResult 함수를 실행한다.
//                        Intent intent = new Intent(CreateQR.this, MainActivity.class);
//                        setResult(RESULT_OK, intent);
                        FinishWithAnim();
                    }
                } catch (Exception e) {
                    showToast(CreateQR.this, "알 수 없는 오류가 발생했습니다.");
                }
            }
        },3000);
    }

    private void updateProgress(){
        progress += (1/PROGRESS_MAX*100);

        String progressText = String.format("%.0f", progress);

        binding.LoadingText.setText(progressText);
        binding.progressBar.setProgress((int)Math.round(progress));
    }
}
