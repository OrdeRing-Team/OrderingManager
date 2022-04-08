package com.example.orderingmanager.view.ManageFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.orderingmanager.Dto.FoodDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.HttpApi;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityMenuItemBinding;
import com.example.orderingmanager.view.BasicActivity;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.net.URL;
import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.CustomViewHolder> {
    ArrayList<ManageData> arrayList;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업
        TextView tvName, tvPrice, tvIntro;
        ImageView ivMenu;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정
            tvName = itemView.findViewById(R.id.item_name);
            tvPrice = itemView.findViewById(R.id.item_price);
            tvIntro = itemView.findViewById(R.id.item_intro);
            ivMenu = itemView.findViewById(R.id.item_image);


            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        Intent intent = new Intent(context, MenuEditActivity.class);
                        intent.putExtra("menuName", arrayList.get(position).getName());
                        intent.putExtra("menuPrice", arrayList.get(position).getPrice());
                        intent.putExtra("menuImage", arrayList.get(position).getIv_menu());
                        intent.putExtra("menuIntro", arrayList.get(position).getIntro());
                        intent.putExtra("menuId", arrayList.get(position).getFoodId());
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
                                .setMessage(arrayList.get(position).getName() + "을(를) 삭제하시겠습니까?")
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
        holder.tvName.setText(arrayList.get(position).getName());
        holder.tvPrice.setText(String.valueOf(arrayList.get(position).getPrice()));
        holder.tvIntro.setText(String.valueOf(arrayList.get(position).getIntro()));

        // arrayList에 저장된 메뉴 이미지 url을 imageURL변수에 저장하고 Glide로 iv에 set
        String imageURL = String.valueOf(arrayList.get(position).getIv_menu());
        Glide.with(holder.itemView.getContext()).load(imageURL).into(holder.ivMenu);
        Log.e("리사이클러뷰 어댑터 이미지 url", imageURL);
    }

    @Override
    public int getItemCount() {
//        getItemCount: return the size of the item list
//        item list의 전체 데이터 개수 return
        return (arrayList != null ? arrayList.size() : 0);
    }

}

