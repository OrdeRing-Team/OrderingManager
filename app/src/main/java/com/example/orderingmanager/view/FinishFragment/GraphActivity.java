package com.example.orderingmanager.view.FinishFragment;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;

import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.ActivityGraphBinding;
import com.example.orderingmanager.databinding.ActivityMenuItemBinding;
import com.example.orderingmanager.view.ManageFragment.MenuAddActivity;
import com.example.orderingmanager.view.ManageFragment.StoreManageActivity;
import com.example.orderingmanager.view.VPAdapter;
import com.example.orderingmanager.view.ViewPagerAdapter;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.util.ArrayList;

public class GraphActivity extends AppCompatActivity {

    private ActivityGraphBinding binding;
    private TabLayout tabLayout;
    private ViewPager viewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);
        binding = ActivityGraphBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //탭레이아웃
        tabLayout = findViewById(R.id.tab_layout_sales);
        viewPager = findViewById(R.id.viewpager);

        tabLayout.setupWithViewPager(viewPager);

        VPAdapter vpAdapter = new VPAdapter(getSupportFragmentManager(), FragmentPagerAdapter.BEHAVIOR_RESUME_ONLY_CURRENT_FRAGMENT);
        vpAdapter.addFragment(new MonthlySalesFragment(),"월별 매출");
        vpAdapter.addFragment(new DailySalesFragment(),"일별 매출");
        viewPager.setAdapter(vpAdapter);

        //뒤로가기 버튼 클릭 이벤트
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });


        BarChart barChart = findViewById(R.id.barChart);
        int[] colorArray = new int[] {Color.rgb(255, 162, 162), Color.rgb(255, 198, 198),Color.rgb(255, 180, 180)};

        //샘플 데이터
        ArrayList<BarEntry> sales = new ArrayList<>();
        sales.add(new BarEntry(1, 10000));
        sales.add(new BarEntry(2, 20000));
        sales.add(new BarEntry(3, 30000));
        sales.add(new BarEntry(4, 40000));
        sales.add(new BarEntry(5, 50000));
        sales.add(new BarEntry(6, 60000));
        sales.add(new BarEntry(7, 40000));
        sales.add(new BarEntry(8, 50000));
        sales.add(new BarEntry(9, 60000));

        sales.add(new BarEntry(10, 10000));
        sales.add(new BarEntry(11, 20000));
        sales.add(new BarEntry(12, 30000));
        sales.add(new BarEntry(13, 40000));
        sales.add(new BarEntry(14, 50000));
        sales.add(new BarEntry(15, 60000));
        sales.add(new BarEntry( 16,40000));
        sales.add(new BarEntry(17, 50000));
        sales.add(new BarEntry(18, 60000));

        sales.add(new BarEntry(19, 10000));
        sales.add(new BarEntry(20, 20000));
        sales.add(new BarEntry(21, 30000));
        sales.add(new BarEntry(22, 40000));
        sales.add(new BarEntry(23, 50000));
        sales.add(new BarEntry(24, 60000));
        sales.add(new BarEntry(25, 40000));
        sales.add(new BarEntry(26, 50000));
        sales.add(new BarEntry(27, 60000));


        BarDataSet barDataSet = new BarDataSet(sales, "일일 매출");

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        /* 그래프 y값 텍스트 스타일 설정 */
        //barDataSet.setValueTextColor(Color.BLACK);
        //barDataSet.setValueTextSize(16f);

        /* 그래프 텍스트 없애기 */
        barDataSet.setDrawValues(false);

        BarData barData = new BarData(barDataSet);
        barDataSet.setColors(colorArray);

//        BarChart barChart = findViewById(R.id.barChart);
//        int[] colorArray = new int[] {
//                Color.rgb(248, 168, 146),
//                Color.rgb(250, 198, 141),
//                Color.rgb(253, 229, 133),
//                Color.rgb(199, 226, 160),
//                Color.rgb(141, 198, 190),
//                Color.rgb(161, 178, 216),
//                Color.rgb(206, 160, 203)};
//
//
//    //샘플 데이터
//        ArrayList<BarEntry> sales = new ArrayList<>();
//        sales.add(new BarEntry(1, 10000));
//        sales.add(new BarEntry(2, 20000));
//        sales.add(new BarEntry(3, 30000));
//        sales.add(new BarEntry(4, 40000));
//        sales.add(new BarEntry(5, 50000));
//        sales.add(new BarEntry(6, 60000));
//        sales.add(new BarEntry(7, 40000));
//        sales.add(new BarEntry(8, 50000));
//        sales.add(new BarEntry(9, 60000));
//
//        sales.add(new BarEntry(10, 10000));
//        sales.add(new BarEntry(11, 20000));
//        sales.add(new BarEntry(12, 30000));
//        sales.add(new BarEntry(13, 40000));
//        sales.add(new BarEntry(14, 50000));
//        sales.add(new BarEntry(15, 60000));
//        sales.add(new BarEntry( 16,40000));
//        sales.add(new BarEntry(17, 50000));
//        sales.add(new BarEntry(18, 60000));
//
//        sales.add(new BarEntry(19, 10000));
//        sales.add(new BarEntry(20, 20000));
//        sales.add(new BarEntry(21, 30000));
//        sales.add(new BarEntry(22, 40000));
//        sales.add(new BarEntry(23, 50000));
//        sales.add(new BarEntry(24, 60000));
//        sales.add(new BarEntry(25, 40000));
//        sales.add(new BarEntry(26, 50000));
//        sales.add(new BarEntry(27, 60000));
//
//
//        BarDataSet barDataSet = new BarDataSet(sales, "일일 매출");
//
//        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);
//
//        /* 그래프 y값 텍스트 스타일 설정 */
//        //barDataSet.setValueTextColor(Color.BLACK);
//        //barDataSet.setValueTextSize(16f);
//
//        /* 그래프 텍스트 없애기 */
//        barDataSet.setDrawValues(false);
//
//        BarData barData = new BarData(barDataSet);
//        barDataSet.setColors(colorArray);
//
//        barChart.setFitBars(true);
//        barChart.setData(barData);
//        barChart.getDescription().setText("Bar Chart Example");
//        barChart.animateY(2000);
    }


}