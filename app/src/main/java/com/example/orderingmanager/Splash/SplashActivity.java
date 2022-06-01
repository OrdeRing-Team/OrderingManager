package com.example.orderingmanager.Splash;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.ImageView;

import com.example.orderingmanager.R;
import com.example.orderingmanager.view.login_register.LoginActivity;

public class SplashActivity extends Activity {

    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        startSplash2();
        startLoading();

    }

    private void startLoading() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(getApplicationContext(), LoginActivity.class);

//                if(getIntent().getStringExtra("FromFCM_Channel") != null) {
//                    intent.putExtra("fromFCM_Channel",getIntent().getStringExtra("fromFCM_Channel"));
//                }
                startActivity(intent);
                finish();   //현재 액티비티 종료
            }
        }, 3000);

    }

    private void startSplash2() {
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                ImageView splash1 = findViewById(R.id.splash1img);
                splash1.setVisibility(View.GONE);
                ImageView splash2 = findViewById(R.id.splash2img);
                splash2.setVisibility(View.VISIBLE);
            }
        }, 2000);

    }

}
