package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.example.orderingmanager.databinding.FragmentFinishBinding;
import com.prolificinteractive.materialcalendarview.MaterialCalendarView;
import com.prolificinteractive.materialcalendarview.format.ArrayWeekDayFormatter;
import com.prolificinteractive.materialcalendarview.format.MonthArrayTitleFormatter;

public class FinishFragment extends Fragment {

    private View view;
    private FragmentFinishBinding binding;
    private MaterialCalendarView calendarView;

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

        storeInfoCheck();

        //calendarView = getView().findViewById(R.id.calendarView);


        // 월, 요일을 한글로 보이게 설정 (MonthArrayTitleFormatter의 작동을 확인하려면 밑의 setTitleFormatter()를 지운다)
        //calendarView.setTitleFormatter(new MonthArrayTitleFormatter(getResources().getTextArray(R.array.custom_months)));
        //calendarView.setWeekDayFormatter(new ArrayWeekDayFormatter(getResources().getTextArray(R.array.custom_weekdays)));


        return view;
    }


    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("finishFrag",storeInitInfo.toString());
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        }
        else{
            Log.e("finishFrag",storeInitInfo.toString());
            binding.errorNotFound.setVisibility(View.GONE);
            binding.finishFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.refreshImageButton:
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };
}

