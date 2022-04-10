package com.example.orderingmanager.view.FinishFragment;

import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.FragmentDailySalesBinding;
import com.example.orderingmanager.databinding.FragmentFinishBinding;
import com.example.orderingmanager.databinding.FragmentMonthlySalesBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.ValueFormatter;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;

public class MonthlySalesFragment extends Fragment {

    private View view;
    private FragmentMonthlySalesBinding binding;
    ArrayList<String> labelList = new ArrayList<>(); // ArrayList 선언

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentMonthlySalesBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        configureChartAppearance(); // BarChart의 기본적인 것들을 세팅해준다

        return view;
    }



    private void configureChartAppearance() {

        BarChart barChart = view.findViewById(R.id.barChart);
        int[] colorArray = new int[]{
                Color.rgb(219, 167, 95),
                Color.rgb(218, 133, 95),
                Color.rgb(217, 100, 94),
                Color.rgb(217, 98, 127),
                Color.rgb(217, 98, 158),
                Color.rgb(217, 98, 191),
                Color.rgb(197, 98, 204),
                Color.rgb(160, 98, 204),
                Color.rgb(127, 98, 205),
                Color.rgb(103, 108, 205),
                Color.rgb(105, 141, 203),
                Color.rgb(105, 174, 203)};


        //샘플 데이터
        ArrayList<BarEntry> sales = new ArrayList<>();
        sales.add(new BarEntry(1, 10000));
        sales.add(new BarEntry(2, 11000));
        sales.add(new BarEntry(3, 13000));
        sales.add(new BarEntry(4, 13100));
        sales.add(new BarEntry(5, 14000));
        sales.add(new BarEntry(6, 12000));
        sales.add(new BarEntry(7, 13400));
        sales.add(new BarEntry(8, 17000));
        sales.add(new BarEntry(9, 16400));
        sales.add(new BarEntry(10, 15000));
        sales.add(new BarEntry(11, 14500));
        sales.add(new BarEntry(12, 14200));


        BarDataSet barDataSet = new BarDataSet(sales, "일별 매출");

        barDataSet.setColors(ColorTemplate.MATERIAL_COLORS);

        barDataSet.setDrawValues(false); //그래프 텍스트 없애기
        barChart.setDrawValueAboveBar(false); //입력값이 차트 위or아래에 그려질건지 (true=위, false=아래)
        barChart.setPinchZoom(false); //줌 설정
        barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
        // barChart.setExtraOffsets(10f, 0f, 40f, 0f);


        BarData barData = new BarData(barDataSet);
        barDataSet.setColors(colorArray);

        barChart.setFitBars(true);
        barChart.setData(barData);
        barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
        barChart.animateY(2000);
        barChart.setTouchEnabled(false); // 터치 유무
        barChart.setVisibleXRangeMaximum(31); //최대 x좌표 기준으로 몇개를 보여줄 것인지


        XAxis xAxis = barChart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setTextSize(11f);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelCount(11, true); //X축의 데이터를 최대 몇개 까지 나타낼지에 대한 설정 5개 force가 true 이면 반드시 보여줌

    }


}


