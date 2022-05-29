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

import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.response.OrderPreviewDto;
import com.example.orderingmanager.Dto.response.WaitingPreviewDto;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentOrderListBinding;

import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderListFragment extends Fragment {
    private FragmentOrderListBinding binding;
    public static List<OrderPreviewDto> processedList;
    static RecyclerView processedRecyclerView;
    View view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentOrderListBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initData();

        return view;
    }

    private void initData(){

        getReceivedData();
        processedRecyclerView = binding.rvProcessedOrder;
    }

    private void getReceivedData(){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+UserInfo.getRestaurantId() + "/orders/ongoing/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<OrderPreviewDto>>> call = service.getOrderReceivedList(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<List<OrderPreviewDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<OrderPreviewDto>>> call, Response<ResultDto<List<OrderPreviewDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<OrderPreviewDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {

                                            // 웨이팅 요청 손님이 0명일 때
                                            if (result.getData().size() > 0) {
                                                binding.tvReceivedCount.setText(String.format("(%d)",result.getData().size()));
                                                binding.rvReceivedOrder.setVisibility(View.VISIBLE);
                                                binding.tvEmptyReceived.setVisibility(View.GONE);

                                                RecyclerView recyclerView = binding.rvReceivedOrder;
                                                OrderReceivedAdapter orderReceivedAdapter = new OrderReceivedAdapter(result.getData(), getActivity(), binding.tvReceivedCount, binding.tvProcessedCount, binding.tvEmptyReceived);
                                                recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
                                                recyclerView.setAdapter(orderReceivedAdapter);
                                            }
                                            getProcessedData();
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<OrderPreviewDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            binding.progressbar.setVisibility(View.GONE);
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            binding.progressbar.setVisibility(View.GONE);
        }
    }

    public void getProcessedData(){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+UserInfo.getRestaurantId()+"/orders/finished/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<List<OrderPreviewDto>>> call = service.getOrderProcessedList(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<List<OrderPreviewDto>>>() {
                        @Override
                        public void onResponse(Call<ResultDto<List<OrderPreviewDto>>> call, Response<ResultDto<List<OrderPreviewDto>>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<List<OrderPreviewDto>> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            processedList = null;
                                            if (result.getData().size() > 0) {
                                                binding.tvProcessedCount.setText(String.format("(%d)",result.getData().size()));
                                                binding.rvProcessedOrder.setVisibility(View.VISIBLE);
                                                binding.tvEmptyProcessed.setVisibility(View.GONE);

                                                processedList = result.getData();

                                                setProcessedRecyclerView(processedList);
                                            }

                                            binding.progressbar.setVisibility(View.GONE);
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<List<OrderPreviewDto>>> call, Throwable t) {
                            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            binding.progressbar.setVisibility(View.GONE);
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            binding.progressbar.setVisibility(View.GONE);
        }
    }

    public static void setProcessedRecyclerView(List<OrderPreviewDto> processedList){
        OrderProcessedAdapter orderProcessedAdapter = new OrderProcessedAdapter(processedList, processedRecyclerView.getContext());
        processedRecyclerView.setLayoutManager(new LinearLayoutManager(processedRecyclerView.getContext()));
        processedRecyclerView.setAdapter(orderProcessedAdapter);
    }

    @Override
    public void onResume(){
        super.onResume();
        initData();
    }
}