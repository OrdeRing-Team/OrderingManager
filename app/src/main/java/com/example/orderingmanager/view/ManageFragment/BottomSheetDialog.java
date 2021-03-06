package com.example.orderingmanager.view.ManageFragment;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.orderingmanager.Dto.FoodStatusDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetDialog extends BottomSheetDialogFragment {

    public static BottomSheetDialog getInstance() {
        return new BottomSheetDialog();
    }

    private LinearLayout btn_represent;
    private LinearLayout btn_soldout;
    private LinearLayout btn_edit;
    private LinearLayout btn_delete;
    private TextView tv_soldout;
    private TextView tv_represent;

    public Bundle menuData;
    public Long foodId;
    public Long representId;
    boolean soldout, represent = false;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container, false);

        btn_represent = (LinearLayout) view.findViewById(R.id.btn_represent);
        btn_soldout = (LinearLayout) view.findViewById(R.id.btn_soldout);
        btn_edit = (LinearLayout) view.findViewById(R.id.btn_edit);
        btn_delete = (LinearLayout) view.findViewById(R.id.btn_delete);
        tv_soldout = view.findViewById(R.id.tv_soldout);
        tv_represent = view.findViewById(R.id.tv_represent);
        // bundle??? ?????? foodId Long?????? ??????????????? foodId??? ??????
        menuData = getArguments();
        foodId = menuData.getLong("foodId");
        // bundle??? ?????? menuSoldout??? boolean?????? ?????? ????????? soldout??? ??????
        soldout = menuData.getBoolean("menuSoldout");
        if (menuData.getBoolean("represent")) {
            tv_represent.setText("???????????? ????????????");
            represent = true;
            representId = menuData.getLong("representId");

        } else {
            tv_represent.setText("???????????? ????????????");
            represent = false;
        }

        //bundle?????? ????????? soldout
        // ??? ????????????????????? ?????? ????????? ?????? bottomsheetdialog??? ?????? ?????? ???????????? ?????? ??????.
        if (soldout) {  // soldout??? true??? ???
            tv_soldout.setText("?????? ????????????");
        } else {  // soldout??? false??? ???
            tv_soldout.setText("?????? ????????????");
        }

        btn_soldout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                soldout = !soldout;
                putSoldOut();
            }
        });
        btn_edit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                getMenuData();
            }
        });
        btn_delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteData();
            }
        });
        btn_represent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                updateRepresent();
            }
        });
        return view;
    }

    private void updateRepresent() {
//        Toast.makeText(getActivity(), "???????????? ??????",Toast.LENGTH_SHORT).show();
        if (represent) {
            // ?????????????????? -> ???????????? ???????????? API
            try {
                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String url = "http://www.ordering.ml/";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Boolean>> call;
                        call = retrofitService.deleteRepresent(representId);

                        call.enqueue(new Callback<ResultDto<Boolean>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                if (response.isSuccessful()) {
                                    ResultDto<Boolean> result;
                                    result = response.body();
                                    if (result.getData()) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                startActivity(new Intent(getActivity(), StoreManageActivity.class));
                                                getActivity().finish();
                                                Log.e("asdasd", "?????? ?????? ??????");
                                                Toast.makeText(getActivity(), "???????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                            }
                                        });
                                        Log.e("result.getData() ", Boolean.toString(result.getData()));
                                    }
                                }else{
                                    Log.e("?????? ?????? ??????","is not Successful");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                Log.e("e = ", t.getMessage());
                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                Log.e("e = ", e.getMessage());
            }
        } else {
            // ??????????????? ?????????
            try {
                new Thread() {
                    @SneakyThrows
                    public void run() {
                        String url = "http://www.ordering.ml/";

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl(url)
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Boolean>> call;
                        call = retrofitService.setRepresent(UserInfo.getRestaurantId(), foodId);

                        call.enqueue(new Callback<ResultDto<Boolean>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                if (response.isSuccessful()) {
                                    ResultDto<Boolean> result;
                                    result = response.body();
                                    if (result.getData()) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
//                                            UserInfo.setBasketCount(totalCount);
//                                                startActivity(new Intent(getActivity(), StoreManageActivity.class));
//                                                getActivity().finish();
                                                startActivity(new Intent(getActivity(), StoreManageActivity.class));
                                                getActivity().finish();
                                                Log.e("asdasd","???????????? ?????? ?????? FoodId : " + foodId);
                                                Toast.makeText(getActivity(), "??????????????? ?????????????????????", Toast.LENGTH_SHORT).show();
                                                dismiss();
                                            }
                                        });
                                        Log.e("result.getData() ", Boolean.toString(result.getData()));
                                    }
                                }else{
                                    Log.e("???????????? ??????", "is not Successful");
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                                Log.e("e = ", t.getMessage());
                            }
                        });
                    }
                }.start();

            } catch (Exception e) {
                Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                Log.e("e = ", e.getMessage());
            }
        }
    }

    // ????????????????????? ???????????? ???????????? ??????
    private void getMenuData() {

        // ??????????????? ????????? ???????????? ????????? ????????? ????????????.
        String name = menuData.getString("menuName");
        String price = menuData.getString("menuPrice");
        String intro = menuData.getString("menuIntro");
        String imageUrl = menuData.getString("menuImage");

        // MenuEditActivity??? ????????? ????????? ?????? intent??? ????????????.
        Intent intent = new Intent(getActivity(), MenuEditActivity.class);
        intent.putExtra("menuName", name);
        intent.putExtra("menuPrice", price);
        intent.putExtra("menuImage", imageUrl);
        intent.putExtra("menuIntro", intro);
        intent.putExtra("foodId", foodId);
        startActivity(intent);
        getActivity().finish();
    }

    // ???????????? ???????????? ???????????? ??????
    private void deleteData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/food/" + foodId + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody ?????? ??????
        Call<ResultDto<Boolean>> call;
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        call = retrofitService.deleteFood(foodId);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), StoreManageActivity.class));
                        getActivity().finish();
                        Toast.makeText(getActivity(), "?????????????????????.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }

    // ????????? ?????? ?????? ????????? ???????????? ??????
    private void putSoldOut() {

        FoodStatusDto foodStatusDto = new FoodStatusDto(soldout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/food/" + foodId + "/status/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody ?????? ??????
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<Boolean>> call = retrofitService.putSoldout(foodId, foodStatusDto);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), StoreManageActivity.class));
                        getActivity().finish();

                        // soldout ????????? ?????? ????????? ????????? ????????? ????????????
                        if (soldout) {
                            Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        } else {
                            Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(getActivity(), "?????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show();
                Log.e("e = ", t.getMessage());
            }
        });
    }


}
