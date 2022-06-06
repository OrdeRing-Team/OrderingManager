package com.example.orderingmanager.view.OrderFragment;

import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;

import java.util.ArrayList;

public class WaitingAdapter extends RecyclerView.Adapter<WaitingAdapter.CustomViewHolder>{
    ArrayList<WaitingData> arrayList;
    Context context;
    int position;

    private DialogWaitingCallListener mListener;



    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업

        TextView tv_waitingId, tv_teamOfPeople, tv_waitingState, tv_nowTime;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tv_waitingId = itemView.findViewById(R.id.tv_waiting_request);
            tv_teamOfPeople = itemView.findViewById(R.id.tv_num_of_people);
            tv_nowTime = itemView.findViewById(R.id.tv_now_time);
            tv_waitingState = itemView.findViewById(R.id.tv_waiting_state);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        // Bundle에 담아서 WaitingBottomDialog로 보낸다.
                        WaitingCallDialog waitingTimeSetDialog = new WaitingCallDialog();
                        Bundle waitingData = new Bundle();
                        waitingData.putString("waitingId", String.valueOf(arrayList.get(position).getWaitingId()));
                        waitingData.putString("waitingNum", String.valueOf(arrayList.get(position).getWaitingNum()));
                        waitingData.putString("waitingNumOfPeople", String.valueOf(arrayList.get(position).getNumOfTeamMembers()));
                        waitingData.putString("waitingPhoneNum", String.valueOf(arrayList.get(position).getPhoneNumber()));
                        waitingData.putString("waitingTime", String.valueOf(UserInfo.getAdmissionWaitingTime() * (position + 1)));

                        waitingTimeSetDialog.setArguments(waitingData);
                        waitingTimeSetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(), "WaitingCallDialog");

                    }
                }
            });
        }
    }

    public WaitingAdapter(ArrayList<WaitingData> arrayList) {
        // adapter constructor
        this.arrayList = arrayList;
    }

    public WaitingAdapter(int position) {
        // adapter constructor
        this.position = position;
    }


    public WaitingAdapter(ArrayList<WaitingData> arrayList, Context context) {
        // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }




    @NonNull
    @Override
    public WaitingAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
        // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_order_waiting, parent, false);
        return new WaitingAdapter.CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull WaitingAdapter.CustomViewHolder holder, int position) {
        // onBindViewHolder: put data of item list into xml widgets
        // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
        // position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tv_waitingId.setText(String.valueOf(arrayList.get(position).getWaitingNum()));
        holder.tv_teamOfPeople.setText(String.valueOf(arrayList.get(position).getNumOfTeamMembers()) + "명");
        Byte countPeople = arrayList.get(position).getNumOfTeamMembers();
        holder.tv_nowTime.setText(String.valueOf(arrayList.get(position).getWaitingRequestTime()));
        holder.tv_waitingState.setText(String.valueOf(UserInfo.getAdmissionWaitingTime() * (position + 1)) + "분");
        Log.e("대기시간 계산", String.valueOf(UserInfo.getAdmissionWaitingTime() * countPeople.intValue()));
    }


    @Override
    public int getItemCount() {
        // getItemCount: return the size of the item list
        // item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }

    public void deleteItem(int position) {
        /* view에서 삭제 */
        arrayList.remove(position);
        notifyItemRemoved(position);
    }

}
