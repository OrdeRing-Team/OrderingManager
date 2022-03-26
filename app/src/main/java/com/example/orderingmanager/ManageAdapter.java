package com.example.orderingmanager;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.CustomViewHolder> {
    ArrayList<ManageData> arrayList;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업
        TextView tvName, tvPrice;
        //ImageView ivMenu;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tvName = itemView.findViewById(R.id.item_name);
            tvPrice = itemView.findViewById(R.id.item_price);
            //ivMenu = itemView.findViewById(R.id.item_image);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        Intent intent = new Intent(context, MenuEditActivity.class);
                        intent.putExtra("name", arrayList.get(position).getTv_name());
                        intent.putExtra("price", arrayList.get(position).getTv_price());
                        //intent.putExtra("menuImage", arrayList.get(position).getIv_menu());
                        intent.putExtra("position", position);

                        context.startActivity(intent);
                    }
                }
            });
            itemView.setOnLongClickListener(new View.OnLongClickListener() {
                @Override
                public boolean onLongClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(context);
                        builder.setTitle("삭제")
                                .setMessage(arrayList.get(position).getTv_name() + "을(를) 삭제하시겠습니까?")
                                .setPositiveButton("삭제하기", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialogInterface, int i) {
                                        arrayList.remove(position);
                                        notifyDataSetChanged();
                                    }
                                })
                                .setNeutralButton("취소", null)
                                .show();
                    }
                    return false;
                }
            });
        }
    }

    public ManageAdapter(ArrayList<ManageData> arrayList) {
//        adapter constructor
        this.arrayList = arrayList;
    }

    public ManageAdapter(ArrayList<ManageData> arrayList, Context context) {
//        adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
//        onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
//        layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
//        onBindViewHolder: put data of item list into xml widgets
//        xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
//        position에 해당하는 data, viewHolder의 itemView에 표시함
        holder.tvName.setText(arrayList.get(position).getTv_name());
        holder.tvPrice.setText(String.valueOf(arrayList.get(position).getTv_price()));
        //holder.ivMenu.setImageResource(arrayList.get(position).getIv_menu());
    }

    @Override
    public int getItemCount() {
//        getItemCount: return the size of the item list
//        item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }
}

