package com.example.orderingmanager.view.ManageFragment;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.R;
import com.example.orderingmanager.view.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.CustomViewHolder> {
    ArrayList<ManageData> arrayList;
    HashMap<Long, Integer> representMenuHashMap;
    Context context;

    public class CustomViewHolder extends RecyclerView.ViewHolder {
        //        adapter의 viewHolder에 대한 inner class (setContent()와 비슷한 역할)
        //        itemView를 저장하는 custom viewHolder 생성
        //        findViewById & 각종 event 작업
        TextView tvName, tvPrice, tvIntro, tvSoldout, tvRepresent;
        ImageView ivMenu;


        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정

            tvName = itemView.findViewById(R.id.item_name);
            tvPrice = itemView.findViewById(R.id.item_price);
            tvIntro = itemView.findViewById(R.id.item_intro);
            ivMenu = itemView.findViewById(R.id.item_image);
            tvSoldout = itemView.findViewById(R.id.item_soldout);
            tvRepresent = itemView.findViewById(R.id.tv_represent);

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    int position = getAdapterPosition();
                    if (position != RecyclerView.NO_POSITION) {

                        // Bundle에 담아서 BottomSheetDialog로 보낸다.
                        BottomSheetDialog bottomSheetDialog = new BottomSheetDialog();
                        bottomSheetDialog.show(((AppCompatActivity) context).getSupportFragmentManager(),"bottomSheet");

                        Bundle menuData = new Bundle();
                        menuData.putString("menuName", arrayList.get(position).getName());
                        menuData.putString("menuPrice", arrayList.get(position).getPrice());
                        menuData.putString("menuImage", arrayList.get(position).getIv_menu());
                        menuData.putString("menuIntro", arrayList.get(position).getIntro());
                        menuData.putLong("menuId", arrayList.get(position).getFoodId());
                        menuData.putLong("position", position);
                        menuData.putBoolean("menuSoldout", arrayList.get(position).getSoldout());
                        if(representMenuHashMap.containsKey(arrayList.get(position).getFoodId())) {
                            menuData.putBoolean("represent", true);
                        }else{
                            menuData.putBoolean("represent", false);
                        }
                        bottomSheetDialog.setArguments(menuData);
                    }
                }
            });
        }
    }

    public ManageAdapter(ArrayList<ManageData> arrayList) {
    // adapter constructor
        this.arrayList = arrayList;
    }


    public ManageAdapter(ArrayList<ManageData> arrayList, HashMap<Long, Integer> representMenuHashMap, Context context) {
    // adapter constructor for needing context part
        this.arrayList = arrayList;
        this.context = context;
        this.representMenuHashMap = representMenuHashMap;
    }




    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
    // onCreateViewHolder: make xml as an object using LayoutInflater & create viewHolder with the object
    // layoutInflater로 xml객체화. viewHolder 생성.
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_menu, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
    // onBindViewHolder: put data of item list into xml widgets
    // xml의 위젯과 데이터를 묶는(연결하는, setting하는) 작업.
    // position에 해당하는 data, viewHolder의 itemView에 표시함

        holder.tvName.setText(arrayList.get(position).getName());
        holder.tvPrice.setText(String.valueOf(arrayList.get(position).getPrice()));
        holder.tvIntro.setText(String.valueOf(arrayList.get(position).getIntro()));
        holder.tvIntro.setText(String.valueOf(arrayList.get(position).getIntro()));

        if(representMenuHashMap.containsKey(arrayList.get(position).getFoodId())){
            holder.tvRepresent.setVisibility(View.VISIBLE);
        }
        // arrayList에 저장된 메뉴 이미지 url을 imageURL변수에 저장하고 Glide로 iv에 set
        String imageURL = String.valueOf(arrayList.get(position).getIv_menu());
        Glide.with(holder.itemView.getContext()).load(imageURL).into(holder.ivMenu);

        // 품절 정보 불러와서 true이면 리사이클러뷰에 "품절" 출력하기
        boolean soldout = arrayList.get(position).getSoldout();
        if (soldout) {
            holder.tvSoldout.setVisibility(View.VISIBLE);
            holder.tvSoldout.setText("품절");
        }
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
        notifyDataSetChanged();
    }

}

