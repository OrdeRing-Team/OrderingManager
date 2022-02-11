package com.example.orderingmanager;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.chauthai.swipereveallayout.SwipeRevealLayout;
import com.chauthai.swipereveallayout.ViewBinderHelper;

import java.util.ArrayList;

public class ManageAdapter extends RecyclerView.Adapter<ManageAdapter.CustomViewHolder> {

    private Context context;
    private ArrayList<ManageData> arrayList;
    private final ViewBinderHelper viewBinderHelper = new ViewBinderHelper();

    public ManageAdapter(ArrayList<ManageData> arrayList) {
        this.context = context;
        this.arrayList = arrayList;
    }

    @NonNull
    @Override // 생명주기
    public ManageAdapter.CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {

        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_list, parent, false);
        CustomViewHolder holder = new CustomViewHolder(view);

        return holder;
    }

    @Override // 추가될 때 생명주기
    public void onBindViewHolder(@NonNull ManageAdapter.CustomViewHolder holder, int position) {

        holder.iv_menu.setImageResource(arrayList.get(position).getIv_menu()); //iv를 생성할 애들을 가져옴.
        holder.tv_name.setText(arrayList.get(position).getTv_name());
        holder.tv_price.setText(arrayList.get(position).getTv_price());

        //swipe도 같이 생성되어야 함.
        viewBinderHelper.setOpenOnlyOne(true);
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(arrayList.get(position).getIv_menu()));
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(arrayList.get(position).getTv_name()));
        viewBinderHelper.bind(holder.swipeRevealLayout, String.valueOf(arrayList.get(position).getTv_price()));
        viewBinderHelper.closeLayout(String.valueOf(arrayList.get(position).getIv_menu()));
        viewBinderHelper.closeLayout(String.valueOf(arrayList.get(position).getTv_name()));
        viewBinderHelper.closeLayout(String.valueOf(arrayList.get(position).getTv_price()));


        //리스트뷰가 클릭이 됐을 때, 롱클릭이 됐을 때
        holder.itemView.setTag(position);
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String currentName = holder.tv_name.getText().toString(); //curName의 값에 클릭한 요소의 이름을 가져옴.
                Toast.makeText(view.getContext(), currentName, Toast.LENGTH_SHORT).show();
            }
        });

        holder.txtDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                remove(holder.getAdapterPosition());
            }
        });

//        holder.txtEdit.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                if (position != RecyclerView.NO_POSITION) {
//                    Intent intent = new Intent(context, MenuEditActivity.class);
//                    intent.putExtra("name", arrayList.get(position).getTv_name());
//                    intent.putExtra("price", arrayList.get(position).getTv_price());
//                    intent.putExtra("position", position);
//
//                    context.startActivity(intent);
//                }
//            }
//        });
    }

    @Override
    public int getItemCount() {
        return (null != arrayList ? arrayList.size() : 0); // 리사이클러뷰 바깥에서 추가버튼 클릭됐을 경우는 ManageFragment에서 구현.
    }

    // 항목 삭제 함수
    public void remove(int position) {  // try-catch문 : 예외상황 시 강제 실행
        try {
            arrayList.remove(position);
            notifyItemRemoved(position); // 새로고침

        } catch (IndexOutOfBoundsException ex) {
            ex.printStackTrace();
        }
    }

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        protected ImageView iv_menu;
        protected TextView tv_name;
        protected TextView tv_price;

        protected TextView txtEdit;
        protected TextView txtDelete;
        protected SwipeRevealLayout swipeRevealLayout;

        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            this.iv_menu = (ImageView) itemView.findViewById(R.id.iv_menu);
            this.tv_name = (TextView) itemView.findViewById(R.id.tv_name);
            this.tv_price = (TextView) itemView.findViewById(R.id.tv_price);

            this.txtEdit = (TextView) itemView.findViewById(R.id.txtEdit);
            this.txtDelete = (TextView) itemView.findViewById(R.id.txtDelete);
            this.swipeRevealLayout = itemView.findViewById(R.id.swipelayout);

            //Handling the click events on the txtviews
            txtEdit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Edit is Clicked", Toast.LENGTH_SHORT).show();
                }
            });

            txtDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Toast.makeText(view.getContext(), "Delete is Clicked", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
}
