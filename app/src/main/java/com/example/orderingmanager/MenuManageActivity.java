package com.example.orderingmanager;

import android.os.Bundle;
import android.widget.Button;
import android.widget.RatingBar;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuManageActivity extends AppCompatActivity {

    String TAG = "@@★@";
    Button btnCreate;
    RecyclerView recyclerView;
    ArrayList<ManageData> arrayList = new ArrayList<>();
    RatingBar ratingBar;
    TextView tvScore;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_manage);

        tvScore = findViewById(R.id.tv_score);
        tvScore.setText("0점");
        ratingBar = findViewById(R.id.ratingBar);
        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float v, boolean b) {
                tvScore.setText(v + "점");
            }
        });

    }



}