package com.example.orderingmanager;

import android.os.Bundle;
import android.os.Handler;
//import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;

import com.loopeer.cardstack.AllMoveDownAnimatorAdapter;
import com.loopeer.cardstack.CardStackView;
import com.loopeer.cardstack.UpDownAnimatorAdapter;
import com.loopeer.cardstack.UpDownStackAnimatorAdapter;

import java.util.Arrays;

public class TestStack extends BasicActivity implements CardStackView.ItemExpendListener {
    public static Integer[] TEST_DATAS = new Integer[]{
            // 배경색상리스트 : 처음부터 순서대로 cardview에 정해지는 색상
            // 이건 민주 너가 생각해보고 나중에 배경 색을 메뉴 사진으로 변경하고
            // 디폴트 색상을 따로 정해서 메뉴 사진이 없는 item은 디폴트로 설정해도 좋을것 같아!
            R.color.main,
            R.color.blue,
            R.color.cardview_dark_background,
            R.color.error,
            R.color.main,
            R.color.blue,
            R.color.cardview_dark_background,
            R.color.error,
            R.color.main,
            R.color.blue,
            R.color.cardview_dark_background,
            R.color.error,
            R.color.main,
            R.color.blue,
            R.color.cardview_dark_background,
            R.color.error,
            R.color.main,
            R.color.blue,
            R.color.cardview_dark_background,
            R.color.error,
            R.color.main,
            R.color.blue,
            R.color.cardview_dark_background,
            R.color.error,
            R.color.main,
            R.color.blue
    };
    private CardStackView mStackView;
    private LinearLayout mActionButtonContainer;
    private TestStackAdapter mTestStackAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test_stack);

        mStackView = (CardStackView) findViewById(R.id.stackview_main);
        mActionButtonContainer = (LinearLayout) findViewById(R.id.button_container);
        mStackView.setItemExpendListener(this);
        mTestStackAdapter = new TestStackAdapter(this);
        mStackView.setAdapter(mTestStackAdapter);


        new Handler().postDelayed(
                new Runnable() {
                    @Override
                    public void run() {
                        mTestStackAdapter.updateData(Arrays.asList(TEST_DATAS));
                    }
                }
                , 200
        );
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.menu_all_down:
                mStackView.setAnimatorAdapter(new AllMoveDownAnimatorAdapter(mStackView));
                break;
            case R.id.menu_up_down:
                mStackView.setAnimatorAdapter(new UpDownAnimatorAdapter(mStackView));
                break;
            case R.id.menu_up_down_stack:
                mStackView.setAnimatorAdapter(new UpDownStackAnimatorAdapter(mStackView));
                break;
        }
        return super.onOptionsItemSelected(item);
    }

    public void onPreClick(View view) {
        mStackView.pre();
    }

    public void onNextClick(View view) {
        mStackView.next();
    }

    @Override
    public void onItemExpend(boolean expend) {
        mActionButtonContainer.setVisibility(expend ? View.VISIBLE : View.GONE);
    }
}