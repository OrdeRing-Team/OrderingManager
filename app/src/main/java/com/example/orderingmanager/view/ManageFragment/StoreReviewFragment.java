package com.example.orderingmanager.view.ManageFragment;

import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.fiveStars;
import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.fourStars;
import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.oneStar;
import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.reviewList;
import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.reviewTotalRating;
import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.threeStars;
import static com.example.orderingmanager.view.ManageFragment.StoreManageActivity.twoStars;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentStoreReviewBinding;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.response.ReviewPreviewDto;
import com.example.orderingmanager.databinding.FragmentStoreReviewBinding;
import com.example.orderingmanager.view.ManageFragment.ReviewListAdapter;

import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class StoreReviewFragment extends Fragment {

    private View view;
    private FragmentStoreReviewBinding binding;

    Long restaurantId;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        binding = FragmentStoreReviewBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initData();

        return view;
    }

    private void initData(){
        restaurantId = UserInfo.getRestaurantId();

        initReviewRecyclerView();
    }

    private void initReviewRecyclerView() {
        int totalStars = StoreManageActivity.totalStars;
        binding.progressbar1.setMax(totalStars);
        binding.progressbar2.setMax(totalStars);
        binding.progressbar3.setMax(totalStars);
        binding.progressbar4.setMax(totalStars);
        binding.progressbar5.setMax(totalStars);

        Log.e("oneStar",Integer.toString(oneStar));
        Log.e("oneStar",Integer.toString(twoStars));
        Log.e("oneStar",Integer.toString(threeStars));
        Log.e("oneStar",Integer.toString(fourStars));
        Log.e("oneStar",Integer.toString(fiveStars));
        binding.progressbar1.setProgress(oneStar);
        binding.progressbar2.setProgress(twoStars);
        binding.progressbar3.setProgress(threeStars);
        binding.progressbar4.setProgress(fourStars);
        binding.progressbar5.setProgress(fiveStars);


        binding.ratingBar.setRating(reviewTotalRating);
        binding.tvRating.setText(Float.toString(reviewTotalRating));

        if(reviewList.size() == 0){
            binding.rvReview.setVisibility(View.GONE);
            binding.clEmptyReview.setVisibility(View.VISIBLE);
        }

        RecyclerView recyclerView = binding.rvReview;
        ReviewListAdapter reviewListAdapter = new ReviewListAdapter(reviewList, getContext(), binding.clEmptyReview);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(reviewListAdapter);
        recyclerView.addItemDecoration(new DividerItemDecoration(recyclerView.getContext(), 1));


    }



}