package com.example.orderingmanager.view.OrderFragment;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.response.WaitingPreviewDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentWaitingListBinding;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;


public class WaitingListFragment extends Fragment{

    private View view;
    private FragmentWaitingListBinding binding;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_menu_manage, container, false);
        binding = FragmentWaitingListBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        getWaitingListFromServer();

        refreshRecyclerView();

        // 리사이클러뷰가 아래 네비바에 의해 마지막이 잘리는 경우가 발생.
        // 네비바의 높이를 계산해서 그 높이만큼 마진을 준다.
        // 이렇게 하면 리사이클러뷰가 높이만큼 올라와서 마지막 끝부분까지 보이게 됨.
//        FrameLayout.LayoutParams mLayoutParams = (FrameLayout.LayoutParams) binding.rvWaiting.getLayoutParams();
//        mLayoutParams.bottomMargin = MainActivity.getBottomNaviHeight();


        return view;
    }


    public void getWaitingListFromServer(){
        ArrayList<WaitingData> waitingList = new ArrayList<>();

        // 웨이팅 정보 불러오기
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+UserInfo.getRestaurantId()+"/waitings/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<WaitingPreviewDto>>> call = service.getWaitingList(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<List<WaitingPreviewDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<WaitingPreviewDto>>> call, Response<ResultDto<List<WaitingPreviewDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<WaitingPreviewDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            result.getData().forEach(waitingPreviewDto ->{
                                                waitingList.add(0, new WaitingData(waitingPreviewDto.getWaitingNumber(), waitingPreviewDto.getNumOfTeamMembers(), waitingPreviewDto.getPhoneNumber(), waitingPreviewDto.getWaitingRegisterTime()));
                                                Log.e("num of team members", String.valueOf(waitingPreviewDto.getNumOfTeamMembers()));
                                                UserInfo.setWaitingId(waitingPreviewDto.getWaitingId());
                                            });

                                            // 웨이팅 요청 손님이 0명일 때
                                            if (waitingList.size() == 0) {
                                                Log.e("waitingList.size()", "is 0");
                                                Toast.makeText(getActivity(), "웨이팅 신청 내역이 없습니다.", Toast.LENGTH_LONG).show();

                                            }
                                            else {
                                                Log.e("waitingList.size()", "is not 0");
                                                RecyclerView recyclerView = binding.rvWaiting;
                                                WaitingAdapter waitingAdapter = new WaitingAdapter(waitingList, getActivity());
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                recyclerView.setAdapter(waitingAdapter);
                                            }

                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<WaitingPreviewDto>>> call, Throwable t) {
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



    private void refreshRecyclerView() {
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(WaitingListFragment.this).attach(WaitingListFragment.this).commit();
        SwipeRefreshLayout mSwipeRefreshLayout = view.findViewById(R.id.swipe_layout);
        mSwipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                //ft.commit();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getWaitingListFromServer();
                        mSwipeRefreshLayout.setRefreshing(false);
                    }
                }, 500);

            }
        });
    }

}


