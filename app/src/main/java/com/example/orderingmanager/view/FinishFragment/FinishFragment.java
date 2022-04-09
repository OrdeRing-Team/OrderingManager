package com.example.orderingmanager.view.FinishFragment;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentFinishBinding;
import com.example.orderingmanager.view.MainActivity;
import com.prolificinteractive.materialcalendarview.CalendarDay;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.OnDateSelectedListener;
import com.prolificinteractive.materialcalendarview.OnMonthChangedListener;
import com.prolificinteractive.materialcalendarview.OnRangeSelectedListener;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

import java.util.List;

public class FinishFragment extends Fragment {

    // private static MaterialCalendarView materialCalendarView;

    private View view;
    private FragmentFinishBinding binding;
    private MaterialCalendarView calendarView;
    public TextView diaryTextView;
    private Button btn_graph;

    Bundle extra;

    Boolean storeInitInfo;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentFinishBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            /* 이곳에 받아올 데이터를 추가하십셩 */
        }

        //btn_graph = view.findViewById(R.id.btn_graph);

        initButtonClickListener();
        storeInfoCheck();

        MaterialCalendarView materialCalendarView = view.findViewById(R.id.calendarView);
        materialCalendarView.setSelectedDate(CalendarDay.today());

        // 월, 요일을 한글로 보이게 설정 (MonthArrayTitleFormatter의 작동을 확인하려면 밑의 setTitleFormatter()를 지운다)
        materialCalendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        materialCalendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));

        materialCalendarView.setOnRangeSelectedListener(new OnRangeSelectedListener() {
            @Override
            public void onRangeSelected(@NonNull MaterialCalendarView widget, @NonNull List<CalendarDay> dates) {
                // 선택한 시작 날짜 ~ 마지막 날짜
                String startDay = dates.get(0).getDate().toString();
                String endDay = dates.get(dates.size() - 1).getDate().toString();
                // 선택한 시작 월, 마지막 월
                int startMonth = dates.get(0).getMonth();
                int endMonth = dates.get(dates.size() - 1).getMonth();
                // 선택한 시작 일, 마지막 일
                int startDate = dates.get(0).getDay();
                int endDate = dates.get(dates.size() - 1).getDay();

                Log.e("DATE", "시작일 : " + startDay + ", 종료일 : " + endDay);
                binding.diaryTextView.setText(startDay + " ~ " + endDay);
                binding.selectedView.setText(startMonth+ "월 " + startDate + "일 - " + endMonth + "월 " + endDate + "일 매출 : ");
                binding.monthView.setText(startMonth + "월 총 매출 : ");
            }
        });

        materialCalendarView.setOnDateChangedListener(new OnDateSelectedListener() {
            @Override
            public void onDateSelected(@NonNull MaterialCalendarView widget, @NonNull CalendarDay date, boolean selected) {
                String selectedDate = date.getDate().toString();
                int selectedMonth = date.getMonth();
                int selectedDay = date.getDay();


                Log.e("DATE", selectedDate);
                binding.diaryTextView.setText(selectedDate);
                binding.selectedView.setText(selectedMonth + "월 " + selectedDay + "일 매출 : ");
                binding.monthView.setText(selectedMonth + "월 총 매출 : ");
            }
        });

        materialCalendarView.setOnMonthChangedListener(new OnMonthChangedListener() {
            @Override
            public void onMonthChanged(MaterialCalendarView widget, CalendarDay date) {
            }
        });


        return view;
    }



    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("FinishFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            binding.finishFragment.setVisibility(View.GONE);
            //binding.refreshImageButton.setOnClickListener(onClickListener);
        }
        else{
            Log.e("FinishFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.finishFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void initButtonClickListener(){
        binding.viewErrorLoadStore.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        binding.btnGraph.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), GraphActivity.class);
                startActivity(intent);
            }
        });
    }


}