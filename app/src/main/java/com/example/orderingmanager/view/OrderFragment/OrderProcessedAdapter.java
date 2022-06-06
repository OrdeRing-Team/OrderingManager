package com.example.orderingmanager.view.OrderFragment;

import static com.example.orderingmanager.ENUM_CLASS.OrderType.TABLE;

import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.Dto.response.OrderPreviewDto;
import com.example.orderingmanager.ENUM_CLASS.OrderStatus;
import com.example.orderingmanager.R;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class OrderProcessedAdapter extends RecyclerView.Adapter<OrderProcessedAdapter.CustomViewHolder>{
    List<OrderPreviewDto> arrayList;
    Context context;
    int position;

    private DialogWaitingCallListener mListener;
    private TimerTask second;
    private final Handler handler = new Handler();

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        TextView tv_receivedTime, tv_processedTime, tv_orderId, tv_orderType, tv_order, tv_processedTime_header;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_receivedTime = itemView.findViewById(R.id.tv_received_time);
            tv_processedTime = itemView.findViewById(R.id.tv_processed_time);
            tv_orderId = itemView.findViewById(R.id.tv_orderid);
            tv_orderType = itemView.findViewById(R.id.tv_order_type);
            tv_order = itemView.findViewById(R.id.tv_order);
            tv_processedTime_header = itemView.findViewById(R.id.tv_processed_time_header);

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

    public OrderProcessedAdapter(List<OrderPreviewDto> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public OrderProcessedAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public OrderProcessedAdapter(List<OrderPreviewDto> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public OrderProcessedAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_processed, parent, false);
        return new OrderProcessedAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull OrderProcessedAdapter.CustomViewHolder holder, int position) {
        // onBindViewHolder: put data of item list into xml widgets
        // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
        // position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tv_order.setText(String.valueOf(arrayList.get(position).getOrderSummary()));
        holder.tv_orderId.setText(String.format("주문번호 : %d번", arrayList.get(position).getOrderId()));
        holder.tv_orderType.setText(arrayList.get(position).getOrderType() == TABLE ?
                (Integer.toString(arrayList.get(position).getTableNumber())+"번 테이블") :
                "포장");
        holder.tv_receivedTime.setText(arrayList.get(position).getReceivedTime());
        holder.tv_processedTime.setText(arrayList.get(position).getCancelOrCompletedTime());
        if(arrayList.get(position).getOrderStatus() == OrderStatus.CANCELED){
            holder.tv_processedTime_header.setText("주문 취소 : ");
            holder.tv_processedTime_header.setTextColor(context.getColor(R.color.error));
            holder.tv_processedTime.setTextColor(context.getColor(R.color.error));
        }
    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }
}
