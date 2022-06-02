package com.example.orderingmanager.view.ManageFragment;

import static android.graphics.Color.TRANSPARENT;

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
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.DialogFragment;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.WaitingTimeDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.DialogTakeoutWaitingTimeSetBinding;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class TakeOutWaitingTimeSetDialog extends DialogFragment {

    public static TakeOutWaitingTimeSetDialog getInstance() { return new TakeOutWaitingTimeSetDialog(); }

    private View view;
    private DialogTakeoutWaitingTimeSetBinding binding;
    String waitingTime;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        binding = DialogTakeoutWaitingTimeSetBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        //Custom Dialog 배경 투명하게 -> 모서리 둥글게 커스텀했더니 각진 DialogFragment의 뒷 배경이 보이기 때문
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(TRANSPARENT));

        initTime();
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

        binding.btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                waitingTime = binding.etWaitingTime.getText().toString();

                // EditText 비었는지 확인
                if (waitingTime.equals("")) {
                    Toast.makeText(getActivity(), "빈칸을 모두 채워주세요.", Toast.LENGTH_SHORT).show();
                }
                else {
                    setWaitingTime();
                    Toast.makeText(getActivity(), "대기시간이 설정되었습니다.", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    // 대기 시간 기본 세팅 (ManageFragment에 있는 시간 데이터)
    private void initTime() {
        Bundle waitingData = getArguments();
        String takeoutWaitingTime = waitingData.getString("takeoutWaitingTime");
        binding.etWaitingTime.setText(takeoutWaitingTime);
    }

    // 대기 시간 서버 업로드
    private void setWaitingTime() {
        Log.e("포장 대기시간 서버업로드 함수 안의 waiting time", String.valueOf(Integer.valueOf(waitingTime)));
        WaitingTimeDto waitingTimeDto = new WaitingTimeDto(Integer.valueOf(waitingTime));

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/" + UserInfo.getRestaurantId() + "/order_waiting_time/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<Boolean>> call = retrofitService.setOrderingWaitingTime(UserInfo.getRestaurantId(), waitingTimeDto);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("대기시간 서버 업로드", String.valueOf(result));
                        dismiss();
                        ((AppCompatActivity)getContext()).getSupportFragmentManager().beginTransaction().add(R.id.main_frame, new ManageFragment()).commit();
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
