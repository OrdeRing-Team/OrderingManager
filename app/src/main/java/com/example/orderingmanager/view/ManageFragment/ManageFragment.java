package com.example.orderingmanager.view.ManageFragment;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.FoodCategory;
import com.example.orderingmanager.Dto.request.RestaurantType;
import com.example.orderingmanager.Dto.request.SignInDto;
import com.example.orderingmanager.Dto.response.OwnerSignInResultDto;
import com.example.orderingmanager.Dto.response.RestaurantInfoDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityStoreNoticeBinding;
import com.example.orderingmanager.databinding.FragmentManageBinding;
import com.example.orderingmanager.view.MainActivity;
import com.example.orderingmanager.view.QRFragment.QrList;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.journeyapps.barcodescanner.BarcodeEncoder;

import java.util.ArrayList;
import java.util.List;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ManageFragment extends Fragment {

    private View view;
    private FragmentManageBinding binding;
    public static final String EMPTY_NOTICE = "SECRETCODEFOREMPTYNOTICE";
    Boolean storeInitInfo;
    String notice;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        // 새로고침
//        FragmentTransaction ft = getFragmentManager().beginTransaction();
//        ft.detach(this).attach(this).commit();

        initButtonClickListener();

        storeInfoCheck();

        if (UserInfo.getRestaurantId() != null) {
            initView();


            getDataFromServer();
        }
        return view;
    }

    private void initView() {
        // 매장명
        binding.tvStoreName.setText(UserInfo.getRestaurantName());

        // 사업자명
        binding.tvName.setText(UserInfo.getOwnerName());

        // 매장주소
        binding.tvAddress.setText(UserInfo.getAddress());

        // 매장종류
        RestaurantType restaurantType = UserInfo.getRestaurantType();
        switch (restaurantType) {
            case ONLY_TO_GO:
                binding.tvMealMethod.setText("포장");
                break;
            case FOR_HERE_TO_GO:
                binding.tvMealMethod.setText("매장식사, 포장");
                break;
        }
        // 카테고리
        FoodCategory foodCategory = UserInfo.getFoodCategory();
        switch (foodCategory) {
            case PIZZA:
                binding.tvCategory.setText("피자");
                break;
            case BUNSIK:
                binding.tvCategory.setText("분식");
                break;
            case CHICKEN:
                binding.tvCategory.setText("치킨");
                break;
            case KOREAN_FOOD:
                binding.tvCategory.setText("한식");
                break;
            case CAFE_DESSERT:
                binding.tvCategory.setText("카페/디저트");
                break;
            case PORK_CUTLET_ROW_FISH_SUSHI:
                binding.tvCategory.setText("돈가스/회/초밥");
                break;
            case FAST_FOOD:
                binding.tvCategory.setText("패스트푸드");
                break;
            case JJIM_TANG:
                binding.tvCategory.setText("찜/탕");
                break;
            case CHINESE_FOOD:
                binding.tvCategory.setText("중국집");
                break;
            case JOKBAL_BOSSAM:
                binding.tvCategory.setText("족발/보쌈");
                break;
            case ASIAN_FOOD_WESTERN_FOOD:
                binding.tvCategory.setText("아시안/양식");
                break;
        }

        setStoreNoticeFromServerData();
    }

    private void initButtonClickListener() {
        binding.btnManage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), StoreManageActivity.class));
                //getActivity().finish();
            }
        });

        binding.btnInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), EditPersonalInfoActivity.class));
            }
        });

        binding.btnStoreInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), EditStoreInfoActivity.class);
                startActivity(intent);
                // 저장 후 화면을 갱신하기 위해 startActivityForResult 로 호출
                // 이 호출함수는 나중에 돌아오면 MainActivity 의 onActivityResult 함수 에서 받는다.
                //startActivityForResult(intent,MainActivity.MANAGEFRAGMENT);
            }
        });//

        binding.viewErrorLoadStore.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });


        binding.btnSettingWaitingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Bundle에 담아서 WaitingBottomDialog로 보낸다.
                WaitingTimeSetDialog waitingTimeSetDialog = new WaitingTimeSetDialog();
                Bundle waitingData = new Bundle();
                waitingData.putString("waitingTime", String.valueOf(binding.tvWaitingTime.getText()));
                waitingTimeSetDialog.setArguments(waitingData);
                waitingTimeSetDialog.show((getActivity()).getSupportFragmentManager(), "WaitingTimeSetDialog");
            }
        });

        binding.btnSettingTakeOutWaitingTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Bundle에 담아서 WaitingBottomDialog로 보낸다.
                OrderWaitingTimeSetDialog takeOutwaitingTimeSetDialog = new OrderWaitingTimeSetDialog();
                Bundle waitingData = new Bundle();
                waitingData.putString("takeoutWaitingTime", String.valueOf(binding.tvTakeOutWaitingTime.getText()));
                takeOutwaitingTimeSetDialog.setArguments(waitingData);
                takeOutwaitingTimeSetDialog.show((getActivity()).getSupportFragmentManager(), "TakeOutWaitingTimeSetDialog");
            }
        });

        binding.btnStoreNotice.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String noticeSavedInstance = binding.tvNoticePreview.getText().toString();

                Intent intent = new Intent(getActivity(), StoreNoticeActivity.class);
                if(notice.equals(EMPTY_NOTICE)){
                    intent.putExtra("notice", EMPTY_NOTICE);
                    Log.e("notice333",notice);
                }else{
                    intent.putExtra("notice", noticeSavedInstance);
                }
                startActivity(intent);
            }
        });
    }

    public void storeInfoCheck() {
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if (!storeInitInfo) {
            Log.e("ManageFragment", storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            binding.manageFragment.setVisibility(View.GONE);
        } else {
            Log.e("ManageFragment", storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.manageFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        // 정보 수정이 이루어지고 fragment 로 다시 돌아왔을때는 onResume 이 호출된다
        // 뷰를 새로 다시 세팅해준다.

        if (UserInfo.getRestaurantId() != null) {
            initView();
            initQrList();
            getDataFromServer();
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    public void initQrList() {
        // MainActivity가 실행되면 QrList를 초기화한 뒤 UserInfo에 입력된 tableCount를 가져오고
        // QrList에 포장Qr,웨이팅Qr,테이블Qr Bitmap을 저장한다.
        // static 클래스에 저장되기 때문에 앱이 실행종료 되기전까지 리스트는 유효함
        // 어디서는 QrList를 불러올 수 있음

        int tableCount = UserInfo.getTableCount();
        ArrayList<Bitmap> qrArrayList = new ArrayList<>();
        qrArrayList.add(CreateTakeoutQR());
        qrArrayList.add(CreateWaitingQR());

        if (tableCount != 0) {
            for (int i = 1; i < tableCount + 1; i++) {
                qrArrayList.add(CreateTableQR(i));
            }
        }

        QrList qrList = new QrList(qrArrayList);
    }

    private Bitmap CreateTakeoutQR() {
        String url;
        url = "http://www.ordering.ml/" + UserInfo.getRestaurantId() + "/takeout";
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);
            Log.e("takeout qr ", "성공");
            return bitmap;
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            Log.e("takeout qr ", e.toString());
            Log.e("url = ", url);
            return bitmap;
        }
    }

    private Bitmap CreateWaitingQR() {
        String url;
        url = "http://www.ordering.ml/" + Long.toString(UserInfo.getRestaurantId()) + "/waiting";
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            return bitmap;
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            return bitmap;
        }
    }

    private Bitmap CreateTableQR(int i) {
        String url;
        url = "http://www.ordering.ml/" + Long.toString(UserInfo.getRestaurantId()) + "/table" + i;
        try {
            MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
            BitMatrix bitMatrix = multiFormatWriter.encode(url, BarcodeFormat.QR_CODE, 250, 250);
            BarcodeEncoder barcodeEncoder = new BarcodeEncoder();
            Bitmap bitmap = barcodeEncoder.createBitmap(bitMatrix);

            return bitmap;
        } catch (Exception e) {
            Bitmap bitmap = BitmapFactory.decodeResource(getResources(), R.drawable.ordering_bitmap);
            return bitmap;
        }
    }

    //getRestaurantInfo
    private void getDataFromServer() {
        FirebaseMessaging.getInstance().getToken()
                .addOnCompleteListener(new OnCompleteListener<String>() {
                    @Override
                    public void onComplete(@NonNull Task<String> task) {
                        if (!task.isSuccessful()) {
                            Log.e("토큰 조회", "Fetching FCM registration token failed", task.getException());
                            return;
                        }
                        String token = task.getResult();

                        SignInDto signInDto = new SignInDto(UserInfo.getUserId(), UserInfo.getUserPW(), token);

                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/owner/signin/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<OwnerSignInResultDto>> call = service.ownerSignIn(signInDto);

                        call.enqueue(new Callback<ResultDto<OwnerSignInResultDto>>() {
                            @Override
                            public void onResponse(Call<ResultDto<OwnerSignInResultDto>> call, Response<ResultDto<OwnerSignInResultDto>> response) {
                                ResultDto<OwnerSignInResultDto> result = response.body();

                                new Handler(Looper.getMainLooper()).post(new Runnable() {
                                    @Override
                                    public void run() {
                                        // 서버에 업로드된 이미지Url을 변수에 저장
                                        if (result.getData().getProfileImageUrl() == null) {
                                            Glide.with(getActivity()).load(R.drawable.icon).into(binding.ivStoreIcon);
                                        } else {
                                            Glide.with(getActivity()).load(result.getData().getProfileImageUrl()).into(binding.ivStoreIcon);
                                        }

                                        // 서버에 업로드된 웨이팅 시간 저장
                                        Integer waitingTime = result.getData().getAdmissionWaitingTime();
                                        UserInfo.setAdmissionWaitingTime(waitingTime);
                                        binding.tvWaitingTime.setText(String.valueOf(waitingTime));

                                        Integer takeoutWaitingTime = result.getData().getOrderingWaitingTime();
                                        UserInfo.setOrderingWaitingTime(takeoutWaitingTime);
                                        binding.tvTakeOutWaitingTime.setText(String.valueOf(takeoutWaitingTime));
                                    }


                                });

                            }

                            @Override
                            public void onFailure(Call<ResultDto<OwnerSignInResultDto>> call, Throwable t) {
                                Log.e("e = ", t.getMessage());
                            }
                        });

                        String msg = getString(R.string.msg_token_fmt, token);
                        Log.e("token Log", msg);
                    }
                });
    }

    private void setStoreNoticeFromServerData(){
        try {
            new Thread() {
                @SneakyThrows
                public void run() {

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/restaurant/"+UserInfo.getRestaurantId()+"/info/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<RestaurantInfoDto>> call = service.getStoreNoticeAndCoordinate(UserInfo.getRestaurantId());

                    call.enqueue(new Callback<ResultDto<RestaurantInfoDto>>() {
                        @Override
                        public void onResponse(Call<ResultDto<RestaurantInfoDto>> call, Response<ResultDto<RestaurantInfoDto>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<RestaurantInfoDto> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            binding.progressBarStoreInfo.setVisibility(View.GONE);
                                            notice = result.getData().getNotice();
                                            if(notice != null){
                                                binding.tvNoticePreview.setText(notice);
                                            }else{
                                                Log.e("notice = ", "nullllllllllll");
                                                binding.tvNoticePreview.setText("공지사항을 작성해주세요.");
                                                notice = EMPTY_NOTICE;
                                                Log.e("notice222 = ", notice);
                                            }
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<RestaurantInfoDto>> call, Throwable t) {
                            Toast.makeText(getActivity(), "공지사항을 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            Log.e("e = ", t.getMessage());
                            binding.progressBarStoreInfo.setVisibility(View.GONE);

                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(getActivity(), "공지사항을 불러오는 중 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            Log.e("e = ", e.getMessage());
            binding.progressBarStoreInfo.setVisibility(View.GONE);

        }
    }
}
