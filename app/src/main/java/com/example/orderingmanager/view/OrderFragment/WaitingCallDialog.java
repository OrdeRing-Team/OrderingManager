package com.example.orderingmanager.view.OrderFragment;

import static android.graphics.Color.TRANSPARENT;

import android.content.res.ColorStateList;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.DialogFragment;
import androidx.viewpager2.widget.ViewPager2;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.R;
import com.example.orderingmanager.databinding.DialogWaitingCallBinding;
import com.example.orderingmanager.view.ViewPagerAdapter;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class WaitingCallDialog extends DialogFragment {

    public static WaitingCallDialog getInstance() { return new WaitingCallDialog(); }

    private View view;
    private DialogWaitingCallBinding binding;
    private String waitingId;


    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogWaitingCallBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //Custom Dialog 배경 투명하게 -> 모서리 둥글게 커스텀했더니 각진 DialogFragment의 뒷 배경이 보이기 때문
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

        getWaitingData();
        initButtonListeners();

        return view;

    }


    public void initButtonListeners() {
        binding.btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });

        binding.btnPermitWaiting.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });

    }

    private void buttonLock (View view){
        view.setClickable(false);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.light_gray)));
    }
    private void buttonRelease (View view){
        view.setClickable(true);
        view.setBackgroundTintList(ColorStateList.valueOf(ContextCompat.getColor(getContext(), R.color.black)));
    }

    private void getWaitingData() {
        Bundle waitingData = getArguments();
        waitingId = waitingData.getString("waitingId");
        String waitingNum = waitingData.getString("waitingNum");
        String waitingNumOfPeople = waitingData.getString("waitingNumOfPeople");
        String waitingPhoneNum = waitingData.getString("waitingPhoneNum");
        String waitingTime = waitingData.getString("waitingTime");

        binding.tvWaitingNum.setText(waitingNum + " 번");
        binding.tvNumOfPeople.setText(waitingNumOfPeople + " 명");
        binding.tvCustomerPhoneNum.setText(waitingPhoneNum);
        binding.tvWaitingTime.setText(waitingTime + " 분");

    }

    // 웨이팅 내역 지우기 -> 웨이팅 호출 후 서버에서 삭제해야하기 떄문.
    private void deleteData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/waiting/"+waitingId+"/call/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        Call<ResultDto<Boolean>> call;
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        call = retrofitService.callWaiting(Long.valueOf(waitingId));

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(getActivity(), "해당 고객에게 호출 메세지를 전송했습니다.", Toast.LENGTH_SHORT).show();
                        dismiss();

                        //뷰페이저 재세팅
                        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), 2, 2);
                        ViewPager2 vp = getActivity().findViewById(R.id.order_viewPager);
                        vp.setAdapter(adapter);
                        //adapter.createFragment(1);

                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(getActivity(), "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });
    }

}
