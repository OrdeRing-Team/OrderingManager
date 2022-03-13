package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuManageActivity extends AppCompatActivity {

    String TAG = "@@★@";
    Button btnCreate;
    RecyclerView recyclerView;
    ArrayList<ManageData> arrayList = new ArrayList<>();

    //임시
    ImageButton ibHome;


    ManageAdapter adapter = new ManageAdapter(arrayList, this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_manage);

        setContent();

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuAddActivity.class));
                finish();
            }
        });

        //임시 홈 버튼
        ibHome = findViewById(R.id.btn_home);
        ibHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
                //ManageFragment로 돌아가야 하는데...
            }
        });

        //임시 데이터
        arrayList.add(new ManageData("후라이드 치킨", "18000"));
        arrayList.add(new ManageData("양념 치킨", "20000"));
        arrayList.add(new ManageData("간장 치킨", "20000"));
        arrayList.add(new ManageData("포테이토 피자", "15000"));
        arrayList.add(new ManageData("불고기 피자", "15000"));
        arrayList.add(new ManageData("후라이드 치킨 반 + 양념 치킨 반", "20000"));
        arrayList.add(new ManageData("양념 치킨 반 + 간장 치킨 반", "22000"));
        arrayList.add(new ManageData("양념 치킨 + 포테이토 피자", "32000"));
        arrayList.add(new ManageData("후라이드 치킨 + 포테이토 피자", "30000"));
        arrayList.add(new ManageData("간장 치킨 + 불고기 피자", "32000"));

        recyclerView = findViewById(R.id.recyclerview);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(adapter);
    }

    void setContent() {
        btnCreate = findViewById(R.id.btn_create);
        recyclerView = findViewById(R.id.recyclerview);
    }



    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        int action = 0;
        if (getIntent().getBooleanExtra("new", false)) action = 1;
        else if (getIntent().getBooleanExtra("edit", false)) action = 2;
        Log.d(TAG, "onStart: " + action);
        if (action > 0) {
            String name;
            String price;
            name = getIntent().getStringExtra("name");
            price = getIntent().getStringExtra("price");

            //menu add
            if (action == 1) arrayList.add(new ManageData(name, price));

            //menu edit
            else { // when action == 2
                int position = getIntent().getIntExtra("position", -1);
                if (position != -1) {
                    //파베 연동 후 수정.
                    arrayList.get(position).setTv_name(name);
                    arrayList.get(position).setTv_price(price);
                }
            }
            adapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        arrayList = this.arrayList;
    }
}