package com.example.orderingmanager.view;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;

import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.ActivityCreateQrSuccessBinding;

import nl.dionsegijn.konfetti.models.Shape;
import nl.dionsegijn.konfetti.models.Size;

public class CreateQRSuccessActivity extends BasicActivity {

    private ActivityCreateQrSuccessBinding binding;

    Animation complete;
    Animation fadeIn;
    Animation fadeIn2;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_qr_success);

        binding = ActivityCreateQrSuccessBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        complete = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);
        fadeIn = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_in);
        fadeIn2 = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_fade_in);

        displayComplete();
        displaykonfetti();
    }

    private void displayComplete(){

        if(count < 4){
            new Handler().postDelayed(new Runnable(){
                @Override
                public void run(){
                    try {
                        switch (count){
                            case 0:
                                binding.ivComplete.setVisibility(View.VISIBLE);
                                binding.ivComplete.startAnimation(complete);
                                count++;
                                break;
                            case 1:
                                binding.tvCompleteMsg.setVisibility(View.VISIBLE);
                                binding.tvCompleteMsg.startAnimation(fadeIn);
                                count++;
                                break;
                            case 2:
                                binding.tvCompleteMsg2.setVisibility(View.VISIBLE);
                                binding.tvCompleteMsg2.startAnimation(fadeIn2);
                                count++;
                                break;
                            case 3:
                                binding.btnComplete.setVisibility(View.VISIBLE);
                                binding.btnComplete.startAnimation(complete);
                                count++;
                                break;
                        }
                    } catch (Exception e) { count = 4; }
                    if(count < 4){
                        displayComplete();
                    }
                    else{
                        binding.btnComplete.setOnClickListener(new View.OnClickListener(){
                            @Override
                            public void onClick(View view) {
                                startActivity(new Intent(CreateQRSuccessActivity.this, MainActivity.class));
                                FinishWithAnim();
                            }
                        });
                    }
                }
            },2000);
        }


    }

    private void displaykonfetti(){
        DisplayMetrics display = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(display);
        binding.viewKonfetti.build()
                .addColors(Color.parseColor("#E1695E"), Color.parseColor("#0066FF"), Color.MAGENTA)
                .setDirection(0.0, 359.0)
                .setSpeed(1f, 5f)
                .setFadeOutEnabled(true)
                .setTimeToLive(2000L)
                .addShapes(Shape.RECT, Shape.CIRCLE)
                .addSizes(new Size(12, 5))
                .setPosition(-50f, display.widthPixels + 50f, -50f, -50f)
                .streamFor(300, 5000L);
    }
}
