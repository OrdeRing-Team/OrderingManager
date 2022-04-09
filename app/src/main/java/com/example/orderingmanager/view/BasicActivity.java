package com.example.orderingmanager.view;


import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.orderingmanager.R;

public class BasicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // 이 화면은 왼쪽에서 오른쪽으로 슬라이딩 하면서 켜집니다.
        overridePendingTransition(R.anim.anim_slide_in_left, R.anim.anim_none);
        InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
    }
    protected void FinishWithAnim() {
        finish();
        if (isFinishing()) {
            // 이 화면은 왼쪽에서 오른쪽으로 슬라이딩 하면서 사라집니다.
            overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_out_right);
        }
    }
    protected void BackWithAnim() {
        finish();
        if (isFinishing()) {
            // 이 화면은 오른쪽에서 왼쪽으로 슬라이딩 하면서 사라집니다.
            overridePendingTransition(R.anim.anim_slide_in_right, R.anim.anim_slide_out_right);
        }
    }

    public static void showToast(Activity activity, String msg){
        Toast.makeText(activity, msg, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (isFinishing()) {
            // back 버튼으로 화면 종료가 야기되면 동작한다.
            overridePendingTransition(R.anim.anim_none, R.anim.anim_slide_out_right);
        }
    }


}
