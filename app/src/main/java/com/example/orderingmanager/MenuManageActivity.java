package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class MenuManageActivity extends AppCompatActivity {

    //리사이클러뷰 관련 선언
    private Button btnMenuManage;
    private ArrayList<ManageData> arrayList;
    private ManageAdapter manageAdapter;
    private RecyclerView recyclerView;
    private LinearLayoutManager linearLayoutManager;
    private Button btnAdd;
    private Button btnAddMenu;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_manage);

        btnAdd = findViewById(R.id.btn_create);
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getApplicationContext(), MenuAddActivity.class));
                finish();
            }
        });


        recyclerView = findViewById(R.id.recyclerview); // 프래그먼트에선 view. 붙여줘야함 !
        linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        arrayList = new ArrayList<>();
        manageAdapter = new ManageAdapter(arrayList); // adapter를 가져와 arrayList에 넣어준다.
        recyclerView.setAdapter(manageAdapter); // 담아진 데이터를 리사이클러뷰에 세팅.

//        String name;
//        int price;
//        name = getIntent().getStringExtra("name");
//        price = getIntent().getIntExtra("price", -1);
//        String price_str = Integer.toString(price);
//        arrayList.add(new ManageData(R.mipmap.ic_launcher, name, price_str));
//        manageAdapter.notifyDataSetChanged();
    }





    @Override
    protected void onStart() {
        super.onStart();
        int action = 0;
        if (getIntent().getBooleanExtra("new", false)) action = 1;
        else if (getIntent().getBooleanExtra("edit", false)) action = 2;
        Log.d("onStart", "onStart: " + action);
        if (action > 0) {
            String name;
            int price;
            name = getIntent().getStringExtra("name");
            price = getIntent().getIntExtra("price", -1);
            String price_str = Integer.toString(price);
            if (action == 1) {
                ManageData manageData = new ManageData(R.mipmap.ic_launcher, name, price_str);
                arrayList.add(manageData);
            }
            else { // when action == 2
                int position = getIntent().getIntExtra("position", -1);
                if (position != -1) {
                    arrayList.get(position).setTv_name(name);
                    arrayList.get(position).setTv_name(price_str);
                }
            }
            manageAdapter.notifyDataSetChanged();
        }
    }


}

