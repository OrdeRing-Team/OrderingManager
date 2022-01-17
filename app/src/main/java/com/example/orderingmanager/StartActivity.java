package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class StartActivity extends BasicActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_guide);
        ImageView eggGif = (ImageView)findViewById(R.id.gif_egg);
        Glide.with(this).load(R.raw.egg).into(eggGif);

        (findViewById(R.id.button_right_in)).setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getApplicationContext(), GuidesActivity.class);
                startActivity(intent);
            }
        });


    }
}
