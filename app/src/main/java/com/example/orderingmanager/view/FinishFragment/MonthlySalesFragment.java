package com.example.orderingmanager.view.FinishFragment;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.RequiresApi;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.SalesRequestDto;
import com.example.orderingmanager.Dto.response.SalesResponseDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentDailySalesBinding;
import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MonthlySalesFragment extends Fragment {

    private View view;
    private FragmentDailySalesBinding binding;
    ArrayList<String> salesListFinal;
    ArrayList<Integer> salesMonth = new ArrayList<>();
    String arrayMonthTemp;

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentDailySalesBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        int currentYear;
        int index = 0;

        LocalDate now = LocalDate.now();
        currentYear = now.getYear();

        String from2Server = String.format("%s", currentYear);

        getSalesRequestFromServer(from2Server);


        return view;
    }

    @SuppressLint("DefaultLocale")
    @RequiresApi(api = Build.VERSION_CODES.O)
    public void getSalesRequestFromServer(String from2Server) {
        ArrayList<String> salesList = new ArrayList<>();

        // 매장 한달 매출 불러오기
        try {
            Log.e("sales1", from2Server);
            SalesRequestDto salesRequestDto = new SalesRequestDto(from2Server);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/monthly_sales/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<SalesResponseDto>>> call = service.getSalesMontly(UserInfo.getRestaurantId(), salesRequestDto);

                    call.enqueue(new Callback<ResultDto<List<SalesResponseDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<SalesResponseDto>>> call, Response<ResultDto<List<SalesResponseDto>>> response) {
                            ResultDto<List<SalesResponseDto>> result = response.body();

                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                @Override
                                public void run() {
//                                    result.getData().forEach(SalesResponseDto -> {
//                                        salesList.add(SalesResponseDto.getSales());
//                                        Log.e("sales list", String.valueOf(salesList));
//                                    });

                                    //한달 매출 합계
                                    int sum = 0;
                                    for (int i = 0; i < salesList.size(); i++) {
                                        int salesOfInt = Integer.valueOf(salesList.get(i));
                                        sum += salesOfInt;
                                        //salesMonth.add(sum);
                                        Log.e("sum", String.valueOf(sum));
                                    }

//                                    salesListFinal = salesList;
//                                    Log.e("salesListFinal", String.valueOf(salesListFinal));
                                    salesMonth.add(sum);
                                    //arrayMonthTemp += String.valueOf(sum);
                                    Log.e("salesMonth", String.valueOf(salesMonth));
                                    Log.e("salesMonth 크기", String.valueOf(salesMonth.size()));

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


                                    Log.e("!!!!", String.valueOf(salesMonth));

                                    ArrayList<BarEntry> entries = new ArrayList<>();
                                    entries.add(new BarEntry(1, 10000));
                                    entries.add(new BarEntry(2, 20000));
                                    entries.add(new BarEntry(3, 30000));
                                    entries.add(new BarEntry(4, 40000));
                                    entries.add(new BarEntry(5, 50000));
                                    entries.add(new BarEntry(6, 60000));
                                    entries.add(new BarEntry(7, 70000));
                                    entries.add(new BarEntry(8, 80000));
                                    entries.add(new BarEntry(9, 90000));
                                    entries.add(new BarEntry(10, 100000));
                                    entries.add(new BarEntry(11, 110000));
                                    entries.add(new BarEntry(12, 120000));
                                    //fit the data into a bar
//                                    for (int i = 1; i < salesMonth.size(); i++) {
//                                        entries.add(new BarEntry(i, Integer.valueOf(salesMonth.get(i))));
//                                    }

                                    Log.e("!!!", String.valueOf(entries));

                                    BarDataSet barDataSet = new BarDataSet(entries, "월별 매출"); // 변수로 받아서 넣어줘도 됨
                                    barDataSet.setDrawValues(false); //그래프 텍스트 없애기

                                    BarData data = new BarData(barDataSet); // 라이브러리 v3.x 사용하면 에러 발생함

                                    barChart.setFitBars(true);
                                    barChart.setData(data);
                                    //barChart.getDescription().setText("월별 매출");
                                    barChart.animateY(1000);
                                    //data.setBarWidth(0.2f);

//                                    barChart.setData(data);
//
//                                    barChart.invalidate();
//
//                                    barChart.setDrawValueAboveBar(false); //입력값이 차트 위or아래에 그려질건지 (true=위, false=아래)
//                                    barChart.setPinchZoom(false); //줌 설정
                                    barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
//
                                    barDataSet.setColors(colorArray);
////
//                                    barChart.setFitBars(true);
                                    barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
//                                    barChart.animateY(2000);
                                    barChart.setTouchEnabled(false); // 터치 유무
//                                    barChart.setVisibleXRangeMaximum(salesMonth.size()); //최대 x좌표 기준으로 몇개를 보여줄 것인지


                                    XAxis xAxis = barChart.getXAxis();
                                    xAxis.setGranularity(1f);
                                    xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
                                    xAxis.setDrawGridLines(false);
                                    xAxis.setLabelCount(40); // x축 레이블 표시 개수
//
                                    YAxis yAxis = barChart.getAxisLeft(); // y축 왼쪽
                                    yAxis.setDrawGridLines(true);


                                    //configureChartAppearance(); // BarChart의 기본적인 것들을 세팅해준다

                                }
                            });
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<SalesResponseDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
        }
    }


    private void configureChartAppearance() {
//        BarChart barChart = view.findViewById(R.id.barChart);
////        int[] colorArray = new int[]{
////                Color.rgb(219, 167, 95),
////                Color.rgb(218, 133, 95),
////                Color.rgb(217, 100, 94),
////                Color.rgb(217, 98, 127),
////                Color.rgb(217, 98, 158),
////                Color.rgb(217, 98, 191),
////                Color.rgb(197, 98, 204),
////                Color.rgb(160, 98, 204),
////                Color.rgb(127, 98, 205),
////                Color.rgb(103, 108, 205),
////                Color.rgb(105, 141, 203),
////                Color.rgb(105, 174, 203)};
//
//
//        Log.e("!!!!", String.valueOf(salesMonth));
//
//        ArrayList<BarEntry> entries = new ArrayList<>();
//        //fit the data into a bar
//        for (int i = 0; i < salesMonth.size(); i++) {
//            entries.add(new BarEntry(i, Integer.valueOf(salesMonth.get(i))));
//            Log.e("!!!", String.valueOf(entries));
//        }
//
//
//        BarDataSet barDataSet = new BarDataSet(entries, "월별 매출"); // 변수로 받아서 넣어줘도 됨
//        BarData data = new BarData(barDataSet); // 라이브러리 v3.x 사용하면 에러 발생함
//
//        barChart.setData(data);
//        barChart.invalidate();
//        barDataSet.setDrawValues(false); //그래프 텍스트 없애기
//        barChart.setDrawValueAboveBar(false); //입력값이 차트 위or아래에 그려질건지 (true=위, false=아래)
//        barChart.setPinchZoom(false); //줌 설정
//        barChart.getLegend().setEnabled(false); // Legend는 차트의 범례
//
//        //barDataSet.setColors(colorArray);
////
//        barChart.setFitBars(true);
//        barChart.getDescription().setEnabled(false); // chart 밑에 description 표시 유무
//        barChart.animateY(2000);
//        barChart.setTouchEnabled(false); // 터치 유무
//        barChart.setVisibleXRangeMaximum(salesMonth.size()); //최대 x좌표 기준으로 몇개를 보여줄 것인지
//
//
//        XAxis xAxis = barChart.getXAxis();
//        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
//        xAxis.setDrawGridLines(false);
//        xAxis.setLabelCount(40); // x축 레이블 표시 개수
//
//        YAxis yAxis = barChart.getAxisLeft(); // y축 왼쪽
//        yAxis.setDrawGridLines(true);
//

    }

}