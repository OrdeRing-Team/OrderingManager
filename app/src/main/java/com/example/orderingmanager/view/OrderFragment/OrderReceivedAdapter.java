package com.example.orderingmanager.view.OrderFragment;

import static com.example.orderingmanager.ENUM_CLASS.OrderType.TABLE;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.Dialog.CustomDialogOrderCancel;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.MessageDto;
import com.example.orderingmanager.Dto.response.OrderPreviewDto;
import com.example.orderingmanager.ENUM_CLASS.OrderStatus;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.view.InfoActivity;
import com.example.orderingmanager.view.MainActivity;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class OrderReceivedAdapter extends RecyclerView.Adapter<OrderReceivedAdapter.CustomViewHolder>{
    List<OrderPreviewDto> arrayList;
    CustomDialogOrderCancel customDialogOrderCancel;
    Context context;
    Activity activity;
    View.OnClickListener positiveButton;
    View.OnClickListener negativeButton;
    TextView receivedCount, processedCount, emptyreceived;

    int position;

    private DialogWaitingCallListener mListener;
    private TimerTask second;
    private final Handler handler = new Handler();

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_receivedTime, tv_checkedTime, tv_orderId, tv_orderType, tv_order, tv_checkedTime_Header;
        Button btn_order_cancel, btn_order_accept;
        ProgressBar progressBar;
        ConstraintLayout cl_order_received;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_receivedTime = itemView.findViewById(R.id.tv_received_time);
            tv_checkedTime = itemView.findViewById(R.id.tv_cheked_time);
            tv_orderId = itemView.findViewById(R.id.tv_orderid);
            tv_orderType = itemView.findViewById(R.id.tv_order_type);
            tv_order = itemView.findViewById(R.id.tv_order);
            tv_checkedTime_Header = itemView.findViewById(R.id.tv_cheked_time_header);

            btn_order_accept = itemView.findViewById(R.id.btn_order_accept);
            btn_order_cancel = itemView.findViewById(R.id.btn_order_cancel);

            cl_order_received = itemView.findViewById(R.id.cl_order_received);
            progressBar = itemView.findViewById(R.id.received_progressbar);

        }
    }

    public OrderReceivedAdapter(List<OrderPreviewDto> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public OrderReceivedAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public OrderReceivedAdapter(List<OrderPreviewDto> arrayList, Context context, TextView receivedCount, TextView processedCount, TextView emptyReceived) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
        this.activity = (Activity)context;
        this.receivedCount = receivedCount;
        this.processedCount = processedCount;
        this.emptyreceived = emptyReceived;
    }




    @NonNull
    @Override
    public OrderReceivedAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_received, parent, false);
        return new OrderReceivedAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderReceivedAdapter.CustomViewHolder holder, int position) {

        Log.e("//==========//","//====================================================//");
        Log.e("   position  /",Integer.toString(position));
        Log.e("//==========//","//====================================================//");
        Log.e("//==========//","//====================================================//");
        Log.e("     주문내용  /",arrayList.get(position).getOrderSummary());
        Log.e("//==========//","//====================================================//");

        holder.tv_order.setText(String.valueOf(arrayList.get(position).getOrderSummary()));
        holder.tv_orderId.setText(String.format("주문번호 : %d번", arrayList.get(position).getMyOrderNumber()));
        holder.tv_orderType.setText(arrayList.get(position).getOrderType() == TABLE ?
                (Integer.toString(arrayList.get(position).getTableNumber())+"번 테이블") :
                "포장");
        holder.tv_receivedTime.setText(arrayList.get(position).getReceivedTime());
        if(arrayList.get(position).getCheckTime() == null){
            holder.tv_checkedTime.setVisibility(View.GONE);
            holder.tv_checkedTime_Header.setVisibility(View.GONE);
//            holder.tv_orderId.setVisibility(View.GONE);
            holder.cl_order_received.setBackground(context.getDrawable(R.drawable.background_custom_new_order));

        }else{
            holder.tv_checkedTime.setText(arrayList.get(position).getCheckTime());
            holder.btn_order_accept.setText("완료 처리");
        }

        holder.btn_order_accept.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(holder.btn_order_accept.getText().equals("주문 승인")){
                    // 체크된 상태가 아닐 때 (주문 승인 전 상태)
                    try {
                        MainActivity.progressBar.setVisibility(View.VISIBLE);
                        new Thread() {
                            @SneakyThrows
                            public void run() {
                                Long orderId = arrayList.get(holder.getAbsoluteAdapterPosition()).getOrderId();
                                Log.e("orderID", Long.toString(orderId));
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://www.ordering.ml/api/order/"+ orderId +"/check/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<ResultDto<OrderPreviewDto>> call = service.acceptOrder(orderId);

                                call.enqueue(new Callback<ResultDto<OrderPreviewDto>>() {
                                    @Override
                                    public void onResponse(Call<ResultDto<OrderPreviewDto>> call, Response<ResultDto<OrderPreviewDto>> response) {

                                        if (response.isSuccessful()) {
                                            ResultDto<OrderPreviewDto> result;
                                            result = response.body();
                                            if (result.getData() != null) {
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @SuppressLint("DefaultLocale")
                                                    @Override
                                                    public void run() {
                                                        Log.e("주문승인", "성공");
//                                                    holder.progressBar.setVisibility(View.GONE);
                                                        MainActivity.progressBar.setVisibility(View.GONE);

                                                        holder.tv_checkedTime_Header.setVisibility(View.VISIBLE);
                                                        holder.tv_checkedTime.setVisibility(View.VISIBLE);
                                                        holder.tv_checkedTime.setText(arrayList.get(holder.getAbsoluteAdapterPosition()).getCheckTime());
                                                        holder.btn_order_accept.setText("완료 처리");
                                                        holder.tv_checkedTime_Header.setVisibility(View.VISIBLE);
                                                        holder.tv_checkedTime.setVisibility(View.VISIBLE);
                                                        holder.tv_checkedTime.setText(result.getData().getCheckTime());
//                                                        holder.tv_orderId.setVisibility(View.VISIBLE);
//                                                        holder.tv_orderId.setText(String.format(
//                                                                "주문번호 : %d번",
//                                                                arrayList.get(holder.getAbsoluteAdapterPosition()).getOrderId()
//                                                        ));
                                                        holder.cl_order_received.setBackground(context.getDrawable(R.drawable.background_custom_waiting));
                                                    }
                                                });
                                            }else{
                                                Toast.makeText(context,"주문 승인에 실패했습니다.",Toast.LENGTH_SHORT).show();
//                                            holder.progressBar.setVisibility(View.GONE);
                                                MainActivity.progressBar.setVisibility(View.GONE);

                                            }
                                        }
                                        else{
                                            Log.e("sadasd",Long.toString(orderId));
//                                        holder.progressBar.setVisibility(View.GONE);
                                            MainActivity.progressBar.setVisibility(View.GONE);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultDto<OrderPreviewDto>> call, Throwable t) {
//                                    holder.progressBar.setVisibility(View.GONE);
                                        MainActivity.progressBar.setVisibility(View.GONE);

                                        Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                        Log.e("e = ", t.getMessage());
                                    }
                                });
                            }
                        }.start();

                    } catch (Exception e) {
//                    holder.progressBar.setVisibility(View.GONE);
                        MainActivity.progressBar.setVisibility(View.GONE);

                        Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                        Log.e("e = ", e.getMessage());
                    }
                }
                else{
                    // 체크된 상태일 때(주문 승인된 상태일 때)
                    try {
                        MainActivity.progressBar.setVisibility(View.VISIBLE);
                        new Thread() {
                            @SneakyThrows
                            public void run() {
                                Long orderId = arrayList.get(holder.getAbsoluteAdapterPosition()).getOrderId();
                                Log.e("orderID", Long.toString(arrayList.get(holder.getAbsoluteAdapterPosition()).getOrderId()));
                                Retrofit retrofit = new Retrofit.Builder()
                                        .baseUrl("http://www.ordering.ml/api/order/" + orderId + "/complete/")
                                        .addConverterFactory(GsonConverterFactory.create())
                                        .build();

                                RetrofitService service = retrofit.create(RetrofitService.class);
                                Call<ResultDto<OrderPreviewDto>> call = service.completeOrder(orderId);

                                call.enqueue(new Callback<ResultDto<OrderPreviewDto>>() {
                                    @Override
                                    public void onResponse(Call<ResultDto<OrderPreviewDto>> call, Response<ResultDto<OrderPreviewDto>> response) {

                                        if (response.isSuccessful()) {
                                            ResultDto<OrderPreviewDto> result;
                                            result = response.body();
                                            if (result.getData() != null) {
                                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                    @SuppressLint("DefaultLocale")
                                                    @Override
                                                    public void run() {
                                                        Log.e("주문완료", "성공");
//                                                    holder.progressBar.setVisibility(View.GONE);
                                                        MainActivity.progressBar.setVisibility(View.GONE);
                                                        updateProcessedRecyclerView(result.getData(), true);
                                                        arrayList.remove(holder.getAbsoluteAdapterPosition());
                                                        if(arrayList.size() == 0){
                                                            emptyreceived.setVisibility(View.VISIBLE);
                                                        }
                                                        notifyItemRemoved(holder.getAbsoluteAdapterPosition());

                                                        receivedCount.setText(String.format("(%d)",arrayList.size()));


                                                    }
                                                });
                                            }else{
                                                Toast.makeText(context,"주문 완료 처리에 실패했습니다.",Toast.LENGTH_SHORT).show();
//                                            holder.progressBar.setVisibility(View.GONE);
                                                MainActivity.progressBar.setVisibility(View.GONE);

                                            }
                                        }
                                        else{
                                            Log.e("sadasd",Long.toString(orderId));
//                                        holder.progressBar.setVisibility(View.GONE);
                                            MainActivity.progressBar.setVisibility(View.GONE);

                                        }
                                    }

                                    @Override
                                    public void onFailure(Call<ResultDto<OrderPreviewDto>> call, Throwable t) {
//                                    holder.progressBar.setVisibility(View.GONE);
                                        MainActivity.progressBar.setVisibility(View.GONE);

                                        Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                        Log.e("e = ", t.getMessage());
                                    }
                                });
                            }
                        }.start();

                    } catch (Exception e) {
//                    holder.progressBar.setVisibility(View.GONE);
                        MainActivity.progressBar.setVisibility(View.GONE);

                        Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                        Log.e("e = ", e.getMessage());
                    }
                }
            }
        });

        holder.btn_order_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                customDialogOrderCancel = new CustomDialogOrderCancel(context, positiveButton, negativeButton, holder.getAbsoluteAdapterPosition());
                customDialogOrderCancel.show();
                Log.e("position",Integer.toString(position));
            }
        });

        positiveButton = view -> {
            String reason = customDialogOrderCancel.getReason();
            int canceledPosition = customDialogOrderCancel.getAbsolutePosition();

            Log.e("newPosition", Integer.toString(canceledPosition));
            Log.e("position", Integer.toString(canceledPosition));
            Log.e("reson", reason);
            Log.e("주문내용", arrayList.get(canceledPosition).getOrderSummary());
            Log.e("orderID", Long.toString(arrayList.get(canceledPosition).getOrderId()));
            customDialogOrderCancel.showProgress();
            if (!reason.equals("")) {
                try {
                    new Thread() {
                        @SneakyThrows
                        public void run() {
                            MessageDto messageDto = new MessageDto(reason);
                            Long orderId = arrayList.get(canceledPosition).getOrderId();
                            Log.e("orderID", Long.toString(arrayList.get(canceledPosition).getOrderId()));
                            Retrofit retrofit = new Retrofit.Builder()
                                    .baseUrl("http://www.ordering.ml/api/order/" + orderId + "/owner_cancel/")
                                    .addConverterFactory(GsonConverterFactory.create())
                                    .build();

                            RetrofitService service = retrofit.create(RetrofitService.class);
                            Call<ResultDto<OrderPreviewDto>> call = service.cancelOrder(orderId, messageDto);

                            call.enqueue(new Callback<ResultDto<OrderPreviewDto>>() {
                                @Override
                                public void onResponse(Call<ResultDto<OrderPreviewDto>> call, Response<ResultDto<OrderPreviewDto>> response) {

                                    if (response.isSuccessful()) {
                                        ResultDto<OrderPreviewDto> result;
                                        result = response.body();
                                        if (result.getData() != null) {
                                            new Handler(Looper.getMainLooper()).post(new Runnable() {
                                                @SuppressLint("DefaultLocale")
                                                @Override
                                                public void run() {
                                                    Log.e("주문취소", "성공");
                                                    customDialogOrderCancel.hideProgress();
                                                    updateProcessedRecyclerView(result.getData(),false);
                                                    arrayList.remove(canceledPosition);
                                                    if(arrayList.size() == 0){
                                                        emptyreceived.setVisibility(View.VISIBLE);
                                                    }
                                                    notifyItemRemoved(canceledPosition);

                                                    receivedCount.setText(String.format("(%d)",arrayList.size()));

                                                    customDialogOrderCancel.dismiss();
                                                }
                                            });
                                        }else{
                                            Toast.makeText(context,"주문 취소에 실패했습니다.",Toast.LENGTH_SHORT).show();
                                            customDialogOrderCancel.hideProgress();

                                        }
                                    }
                                    else{
                                        Log.e("sadasd",Long.toString(orderId));
                                        customDialogOrderCancel.hideProgress();

                                    }
                                }

                                @Override
                                public void onFailure(Call<ResultDto<OrderPreviewDto>> call, Throwable t) {
                                    customDialogOrderCancel.hideProgress();

                                    Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                                    Log.e("e = ", t.getMessage());
                                }
                            });
                        }
                    }.start();

                } catch (Exception e) {
                    customDialogOrderCancel.hideProgress();

                    Toast.makeText(context, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                    Log.e("e = ", e.getMessage());
                }
            }else{
                Toast.makeText(context,"취소 사유를 선택해주세요.",Toast.LENGTH_SHORT).show();
                customDialogOrderCancel.hideProgress();

            }
        };

        negativeButton = view -> {
            holder.progressBar.setVisibility(View.GONE);
            customDialogOrderCancel.dismiss();
        };
    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }

    @SuppressLint("DefaultLocale")
    public void updateProcessedRecyclerView(OrderPreviewDto orderPreviewDto, boolean isOrderComplete){
        if(!isOrderComplete) {
            orderPreviewDto.setOrderStatus(OrderStatus.CANCELED);
        }else{
            orderPreviewDto.setOrderStatus(OrderStatus.COMPLETED);
        }
        List<OrderPreviewDto> tmpList = new ArrayList<OrderPreviewDto>();
        tmpList.add(orderPreviewDto);
        tmpList.addAll(OrderListFragment.processedList);
        OrderListFragment.processedList = tmpList;
        OrderListFragment.setProcessedRecyclerView(OrderListFragment.processedList);
        processedCount.setText(String.format("(%d)",tmpList.size()));
    }

}
