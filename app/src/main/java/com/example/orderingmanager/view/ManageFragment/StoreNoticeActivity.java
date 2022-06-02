package com.example.orderingmanager.view.ManageFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.example.orderingmanager.Dialog.CustomDialog;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.MessageDto;
import com.example.orderingmanager.Dto.response.RestaurantInfoDto;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityStoreNoticeBinding;
import com.example.orderingmanager.view.BasicActivity;
import com.example.orderingmanager.view.MainActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreNoticeActivity extends BasicActivity {
    private ActivityStoreNoticeBinding binding;

    CustomDialog dialog;

    String savedNoticeInstance;
    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);

        binding = ActivityStoreNoticeBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        initData();
        initButtonListener();
    }

    private void initData(){
        savedNoticeInstance = getIntent().getStringExtra("notice");
        if(!savedNoticeInstance.equals(ManageFragment.EMPTY_NOTICE)){
            binding.etNotice.setText(savedNoticeInstance);
        }
    }

    private void initButtonListener(){
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(!binding.etNotice.getText().toString().equals(savedNoticeInstance)) {
                    Log.e("etNotice.getText", binding.etNotice.getText().toString());
                    Log.e("savedNoticeInstance",savedNoticeInstance);
                    showCustomDialog();
                }
                else{
                    FinishWithAnim();
                }
            }
        });

        binding.btnSaveNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNotice();
            }
        });
    }

    private void saveNotice(){
        try {
            binding.progressBarNotice.setVisibility(View.VISIBLE);

            new Thread() {
                @SneakyThrows
                public void run() {
                    String getNoticeTexts = binding.etNotice.getText().toString();
                    MessageDto messageDto = new MessageDto(getNoticeTexts);
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+ UserInfo.getRestaurantId() +"/notice/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.saveNotice(UserInfo.getRestaurantId(), messageDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Boolean> result;
                                result = response.body();
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.progressBarNotice.setVisibility(View.GONE);
                                            Toast.makeText(StoreNoticeActivity.this, "공지사항을 저장했습니다..", Toast.LENGTH_LONG).show();

                                            FinishWithAnim();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(StoreNoticeActivity.this, "공지사항을 저장하는 과정에서 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            binding.progressBarNotice.setVisibility(View.GONE);

                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(StoreNoticeActivity.this, "공지사항을 저장하는 과정에서 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            binding.progressBarNotice.setVisibility(View.GONE);

        }
    }
    private void showCustomDialog(){
        dialog = new CustomDialog(
                StoreNoticeActivity.this,
                "변경된 내용을 저장하지 않고 나가시겠습니까?",
                "확인 버튼을 누르시면 변경된 내용은 저장되지 않습니다.",
                "확인","취소",
                positiveButton,negativeButton);

        dialog.show();
    }

    private final View.OnClickListener positiveButton = view -> {
        dialog.dismiss();

        FinishWithAnim();
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };
}
