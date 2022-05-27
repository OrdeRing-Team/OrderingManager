package com.example.orderingmanager.view.ManageFragment;

import android.content.Context;
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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.example.orderingmanager.Dto.FoodStatusDto;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.PasswordChangeDto;
import com.example.orderingmanager.R;
import com.google.android.material.bottomsheet.BottomSheetDialogFragment;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class BottomSheetDialog extends BottomSheetDialogFragment{

    public static BottomSheetDialog getInstance() { return new BottomSheetDialog(); }

    private LinearLayout btn_represent;
    private LinearLayout btn_soldout;
    private LinearLayout btn_edit;
    private LinearLayout btn_delete;
    private TextView tv_soldout;

    public Bundle menuData;
    public Long menuId;
    boolean soldout;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.bottom_sheet_dialog, container,false);

        btn_represent = (LinearLayout) view.findViewById(R.id.btn_represent);
        btn_soldout = (LinearLayout) view.findViewById(R.id.btn_soldout);
        btn_edit = (LinearLayout) view.findViewById(R.id.btn_edit);
        btn_delete = (LinearLayout) view.findViewById(R.id.btn_delete);
        tv_soldout = view.findViewById(R.id.tv_soldout);

        // bundle에 담긴 menuId를 Long형의 전역변수인 menuId에 선언
        menuData = getArguments();
        menuId = menuData.getLong("menuId");
        // bundle에 담긴 menuSoldout을 boolean형의 전역 변수인 soldout에 선언
        soldout = menuData.getBoolean("menuSoldout");


        //bundle에서 받아온 soldout
        // 즉 리사이클러뷰의 품절 상태에 따라 bottomsheetdialog의 품절 선택 텍스트를 달리 한다.
        if (soldout) {  // soldout이 true일 때
            tv_soldout.setText("품절 해제하기");
        }
        else {  // soldout이 false일 때
            tv_soldout.setText("품절 등록하기");
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
        return view;
    }

    // 리사이클러뷰의 데이터를 가져오는 함수
    private void getMenuData() {

        // 어댑터에서 받아온 데이터를 풀어서 변수에 담아준다.
        String name = menuData.getString("menuName");
        String price = menuData.getString("menuPrice");
        String intro = menuData.getString("menuIntro");
        String imageUrl = menuData.getString("menuImage");

        // MenuEditActivity에 보내기 위해서 다시 intent에 담아준다.
        Intent intent = new Intent(getActivity(), MenuEditActivity.class);
        intent.putExtra("menuName", name);
        intent.putExtra("menuPrice", price);
        intent.putExtra("menuImage", imageUrl);
        intent.putExtra("menuIntro", intro);
        intent.putExtra("menuId", menuId);
        startActivity(intent);
        getActivity().finish();
    }

    // 서버에서 데이터를 삭제하는 함수
    private void deleteData() {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/food/" + menuId + "/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        Call<ResultDto<Boolean>> call;
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        call = retrofitService.deleteFood(menuId);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), StoreManageActivity.class));
                        getActivity().finish();
                        Toast.makeText(getActivity(), "삭제되었습니다.", Toast.LENGTH_SHORT).show();
                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(getActivity(), "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });
    }

    // 서버에 메뉴 품절 상태를 저장하는 함수
    private void putSoldOut() {

        FoodStatusDto foodStatusDto = new FoodStatusDto(soldout);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://www.ordering.ml/api/restaurant/food/" + menuId + "/status/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        // RequestBody 객체 생성
        RetrofitService retrofitService = retrofit.create(RetrofitService.class);
        Call<ResultDto<Boolean>> call = retrofitService.putSoldout(menuId, foodStatusDto);

        call.enqueue(new Callback<ResultDto<Boolean>>() {
            @Override
            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                ResultDto<Boolean> result = response.body();

                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        startActivity(new Intent(getActivity(), StoreManageActivity.class));
                        getActivity().finish();

                        // soldout 상태에 따라 토스트 메시지 다르게 출력하기
                        if (soldout) {
                            Toast.makeText(getActivity(), "품절 등록이 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }
                        else {
                            Toast.makeText(getActivity(), "품절 해제가 완료되었습니다.", Toast.LENGTH_SHORT).show();
                        }

                    }
                });
            }

            @Override
            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                Toast.makeText(getActivity(), "서버 요청에 실패하였습니다.", Toast.LENGTH_SHORT).show();
                Log.e("e = " , t.getMessage());
            }
        });
    }


}
