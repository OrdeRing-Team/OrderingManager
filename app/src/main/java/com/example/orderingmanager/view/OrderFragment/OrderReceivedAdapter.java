package com.example.orderingmanager.view.OrderFragment;

import static com.example.orderingmanager.ENUM_CLASS.OrderType.TABLE;

import android.app.Activity;
import android.content.Context;
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
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.graphics.drawable.DrawableCompat;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.Dto.response.OrderPreviewDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrderReceivedAdapter extends RecyclerView.Adapter<OrderReceivedAdapter.CustomViewHolder>{
    List<OrderPreviewDto> arrayList;
    Context context;
    Activity activity;
    int position;
    boolean backWhite = true;

    private DialogWaitingCallListener mListener;
    private TimerTask second;
    private final Handler handler = new Handler();

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_receivedTime, tv_checkedTime, tv_orderId, tv_orderType, tv_order, tv_checkedTime_Header;
        Button btn_order_cancel, btn_order_accept;
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

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {



                    }
                }
            });
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


    public OrderReceivedAdapter(List<OrderPreviewDto> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
        this.activity = (Activity)context;
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
        // onBindViewHolder: put data of item list into xml widgets
        // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
        // position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tv_order.setText(String.valueOf(arrayList.get(position).getOrderSummary()));
        holder.tv_orderId.setText(String.format("주문번호 : %d번", arrayList.get(position).getOrderId()));
        holder.tv_orderType.setText(arrayList.get(position).getOrderType() == TABLE ?
                (Integer.toString(arrayList.get(position).getTableNumber())+"번 테이블") :
                "포장");
        holder.tv_receivedTime.setText(arrayList.get(position).getReceivedTime());
        if(arrayList.get(position).getCheckTime() == null){
            holder.tv_checkedTime.setVisibility(View.GONE);
            holder.tv_checkedTime_Header.setVisibility(View.GONE);
            holder.tv_orderId.setVisibility(View.GONE);
            holder.cl_order_received.setBackgroundColor(context.getColor(R.color.new_order));
        }else{
            holder.tv_checkedTime.setText(arrayList.get(position).getCheckTime());
            holder.btn_order_accept.setText("완료 처리");
        }
    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }


}
