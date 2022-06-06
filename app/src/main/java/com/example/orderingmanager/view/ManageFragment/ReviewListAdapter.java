package com.example.orderingmanager.view.ManageFragment;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.cardview.widget.CardView;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.response.ReviewPreviewDto;
import com.example.orderingmanager.Dialog.CustomDialog;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.response.ReviewPreviewDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.view.MainActivity;
import com.google.android.material.chip.Chip;
import com.google.android.material.chip.ChipGroup;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;
import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ReviewListAdapter extends RecyclerView.Adapter<ReviewListAdapter.CustomViewHolder>{
    List<ReviewPreviewDto> arrayList;
    Context context;
    int position;

    ConstraintLayout cl_emptyReview;
    CustomDialog dialog;
    View.OnClickListener positiveButton;
    View.OnClickListener negativeButton;

    public class CustomViewHolder extends RecyclerView.ViewHolder {

        CircleImageView circleImageView;
        CardView cv_delete, cv_reviewImage;
        TextView tv_reviewerName, tv_review;
        ImageView iv_reviewImage;
        ChipGroup chipGroup;
        RatingBar ratingBar;
        public CustomViewHolder(@NonNull View itemView) {
            super(itemView);
            //item 에 대한 클릭 이벤트 설정

            circleImageView = itemView.findViewById(R.id.iv_review_profile);

            cv_reviewImage = itemView.findViewById(R.id.cv_review_image);

            tv_reviewerName = itemView.findViewById(R.id.tv_nickname);
            tv_review = itemView.findViewById(R.id.tv_review);

            iv_reviewImage = itemView.findViewById(R.id.iv_review_image);

            chipGroup = itemView.findViewById(R.id.chip_group);

            ratingBar = itemView.findViewById(R.id.ratingBar_review_inner);
        }
    }

    public ReviewListAdapter(List<ReviewPreviewDto> arrayList) {
        this.arrayList = arrayList;
    }

    public ReviewListAdapter(int position) {
        this.position = position;
    }


    public ReviewListAdapter(List<ReviewPreviewDto> arrayList, Context context, ConstraintLayout cl_emptyReview) {
        this.arrayList = arrayList;
        this.context = context;
        this.cl_emptyReview = cl_emptyReview;
    }

    @NonNull
    @Override
    public CustomViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_review, parent, false);
        return new CustomViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull CustomViewHolder holder, int position) {
        if(arrayList.get(position).getImageUrl() != null){
            Glide.with(context).load(arrayList.get(position).getImageUrl()).into(holder.iv_reviewImage);
        }else holder.cv_reviewImage.setVisibility(View.GONE);
        holder.tv_review.setText(arrayList.get(position).getReview());

        holder.tv_reviewerName.setText(arrayList.get(position).getNickname());

        Log.e("raiting",Float.toString(arrayList.get(position).getRating()));
        holder.ratingBar.setRating(arrayList.get(position).getRating());

        String orderSummary = arrayList.get(position).getOrderSummary();

        Log.e("##########Summary###########", orderSummary);

        String[] s1 = orderSummary.split(",");
        for(int i = 0; i<s1.length-1; i++){
            String[] s2 = s1[i].split(":");
            Chip chip = new Chip(holder.itemView.getContext());
            chip.setCheckable(false);
            chip.setCloseIconVisible(false);
            chip.setText(s2[0]);
            holder.chipGroup.addView(chip);

            Log.e("##########메뉴 chip 추가###########", s1[0]);
        }

        setProfileIcon(holder);
    }

    @SuppressLint("UseCompatLoadingForDrawables")
    private void setProfileIcon(ReviewListAdapter.CustomViewHolder holder) {
        String nickname = arrayList.get(holder.getAbsoluteAdapterPosition()).getNickname();

        try {
            String seconNick = nickname.split(" ")[1];
            Log.e("seconNick",seconNick);
            Integer key = MainActivity.nickIconHashMap.get(seconNick);
            // 닉네임에 해당하는 icon 설정 -> Review 리스트에서 사용
            // 치킨 통닭 달걀 -> chicken
            // 국밥 마라탕 라면 부대찌개 -> jjim
            // 아이스크림 치즈 샌드위치 케이크 샐러드 팥빙수 아메리카노 -> dessert
            // 피자 -> pizza
            // 비빔밥 두루치기 제육볶음 곱창 -> hansik
            // 탕수육 짜장면 우동 짬뽕 -> chinesefood
            // 떡볶이 -> bunsik2
            // 냉면  -> asianfood
            // 파스타 만두 왕갈비 햄버거 -> fastfood
            // 초밥 회 돈까스 -> japanesefood
            // 족발 통삼겹 -> jokbal
            if (key != null) {
                switch (key) {
                    case 1:
                        Glide.with(context).load(context.getDrawable(R.drawable.chicken)).into(holder.circleImageView);
                        break;
                    case 2:
                        Glide.with(context).load(context.getDrawable(R.drawable.jjim)).into(holder.circleImageView);
                        break;
                    case 3:
                        Glide.with(context).load(context.getDrawable(R.drawable.dessert)).into(holder.circleImageView);
                        break;
                    case 4:
                        Glide.with(context).load(context.getDrawable(R.drawable.pizza)).into(holder.circleImageView);
                        break;
                    case 5:
                        Glide.with(context).load(context.getDrawable(R.drawable.hansik)).into(holder.circleImageView);
                        break;
                    case 6:
                        Glide.with(context).load(context.getDrawable(R.drawable.chinesefood)).into(holder.circleImageView);
                        break;
                    case 7:
                        Glide.with(context).load(context.getDrawable(R.drawable.bunsik2)).into(holder.circleImageView);
                        break;
                    case 8:
                        Glide.with(context).load(context.getDrawable(R.drawable.asianfood)).into(holder.circleImageView);
                        break;
                    case 9:
                        Glide.with(context).load(context.getDrawable(R.drawable.fastfood)).into(holder.circleImageView);
                        break;
                    case 10:
                        Glide.with(context).load(context.getDrawable(R.drawable.japanesefood)).into(holder.circleImageView);
                        break;
                    case 11:
                        Glide.with(context).load(context.getDrawable(R.drawable.jokbal)).into(holder.circleImageView);
                        break;
                    default:
                        Glide.with(context).load(context.getDrawable(R.drawable.icon)).into(holder.circleImageView);
                        break;
                }
            } else {

                Glide.with(context).load(context.getDrawable(R.drawable.icon)).into(holder.circleImageView);
            }
        }catch (NullPointerException e){
            Glide.with(context).load(context.getDrawable(R.drawable.icon)).into(holder.circleImageView);
            Log.e("SplitNickname","Null");
        }
    }

    @Override
    public int getItemCount() {
        return (arrayList != null ? arrayList.size() : 0);
    }
}
