package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import androidx.appcompat.app.AppCompatActivity;

public class MenuAddActivity extends AppCompatActivity {

    EditText edtName, edtPrice;
    Button btnSubmit;
    String name;
    int price;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu_add);

        edtName = findViewById(R.id.edt_name);
        edtPrice = findViewById(R.id.edt_price);

        btnSubmit = findViewById(R.id.btn_submit);
        btnSubmit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                name = edtName.getText().toString();
                price = Integer.parseInt(edtPrice.getText().toString());
                if (name.length() > 0 && price > 0) {
                    Intent intent = new Intent(getApplicationContext(), MenuManageActivity.class);
                    intent.putExtra("new", true);
                    intent.putExtra("name", name);
                    intent.putExtra("price", price);
                    startActivity(intent);
                    finish();
                }
            }
        });
    }
}

