package com.example.orderingmanager.view.QRFragment;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.orderingmanager.R;

import java.util.ArrayList;

public class QrAdapter extends RecyclerView.Adapter<QrAdapter.CustomViewHolder> {
    ArrayList<QrData> arrayList;
    Context context;
    String url;


    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업
        TextView tvExplain, tvStoreName;
        ImageView ivQrImage;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tvExplain = itemView.findViewById(R.id.tv_explain);
            tvStoreName = itemView.findViewById(R.id.tv_qrStoreName);
            ivQrImage = itemView.findViewById(R.id.iv_qrcoderv);

        }
    }

    public QrAdapter(ArrayList<QrData> arrayList) {
//        adapter constructor
        this.arrayList = arrayList;
    }

    public QrAdapter(ArrayList<QrData> arrayList, Context context) {
//        adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
//        layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_qr, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
//        onBindViewHolder: put data of item list into xml widgets
//        xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
//        position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tvExplain.setText(arrayList.get(position).getExplain());
        holder.tvStoreName.setText(arrayList.get(position).getStoreName());
        holder.ivQrImage.setImageBitmap(arrayList.get(position).getBitmap());
    }

    @Override
    public int getItemCount() {
//        getItemCount: return the size of the item list
//        item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }

}

