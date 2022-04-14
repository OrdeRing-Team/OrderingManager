package com.example.orderingmanager.view.QRFragment;

import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.LinearLayout;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.Dialog.CustomDialog;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.FragmentQrBinding;
import com.example.orderingmanager.view.MainActivity;
import com.example.orderingmanager.view.ViewPagerAdapter;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

public class QrFragment extends Fragment {
    private View view;

    //viewbinding
    private FragmentQrBinding binding;
    private CustomDialog dialog;
    Bundle extra;

    Boolean storeInitInfo;
    String url;

    ArrayList<QrData> qrList = new ArrayList<>();
    ArrayList<Bitmap> qrBitmapList = new ArrayList<>();
    private static ArrayList<Bitmap> qrImagePreviewList = new ArrayList<>();

    int table_count;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();
        }

        binding = FragmentQrBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        initButtonClickListener();
        storeInfoCheck();
        if(UserInfo.getRestaurantId() != null) {
            extractQrViews();
        }

        //뷰페이저 세팅
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), 1,3);
        binding.qrViewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayoutQr, binding.qrViewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("포장");
                                break;
                            case 1:
                                tab.setText("웨이팅");
                                break;
                            default:
                                tab.setText("테이블");
                                break;
                        }
                    }
                }).attach();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void extractQrViews(){

        // qrFragment로 올때마다 add시키면 안되니까
        // 매번 초기화 시켜줌
        qrImagePreviewList = new ArrayList<>();

        for(int i = 0; i < UserInfo.getTableCount()+2; i++){
            binding.ivQrcoderv.setImageBitmap(QrList.getQrBitmap(i));
            binding.tvQrStoreName.setText(UserInfo.getRestaurantName());
            Log.e("i",Integer.toString(i));
            // i == 0, 포장용 QR
            // i == 1, 웨이팅용 QR
            // i >= 2, 테이블 QR
            switch (i){
                case 0:
                    binding.tvExplain.setText("기다리지 말고\nQR 찍고 포장 주문하세요");
                    binding.tvTableNum.setVisibility(View.GONE);
                    break;
                case 1:
                    binding.tvExplain.setText("줄 서지 말고\nQR 찍고 대기 등록하세요");
                    binding.tvTableNum.setVisibility(View.GONE);
                    break;
                default:
                    binding.tvTableNum.setVisibility(View.VISIBLE);
                    binding.tvTableNum.setText( i-1 + "번 테이블");
                    binding.tvExplain.setText("테이블에서\nQR 찍고 주문하세요");
                    break;
            }
            capture();
        }
        MainActivity.progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void capture(){
        LinearLayout linearlayout = binding.viewQrCapture;

        // width와  height가 null로 유지되지 않도록 보기를 미리 측정해둔다.
        linearlayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // view와 모든 하위 항목에 크기와 위치 지정
        linearlayout.layout(0, 0, linearlayout.getMeasuredWidth(), linearlayout.getMeasuredHeight());

        linearlayout.setDrawingCacheEnabled(true);
        linearlayout.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(linearlayout.getDrawingCache());

        // 포장, 웨이팅, 테이블 마다 내부 text가 변경되므로
        // setDrawingCacheEnabled(false)를 해줘야한다.
        // 안하면 캡쳐 결과물에 수정이 반영되지 않음
        linearlayout.setDrawingCacheEnabled(false);

        addQrPreviewList(bitmap);

    }

    private void initButtonClickListener(){
        // 버튼 기능 추가
        binding.viewErrorLoadStore.btnRefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(getActivity(), MainActivity.class));
                getActivity().finish();
            }
        });

        binding.btnDownloadQr.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });
    }

    private void showCustomDialog(){
        dialog = new CustomDialog(
                getContext(),
                "QR코드 이미지를 모두 다운로드 하시겠습니까?",
                "다운로드 버튼을 누르면 기기에 모든 이미지가 저장됩니다.",
                "다운로드","취소",
                positiveButton,negativeButton);

        dialog.show();
    }

    private final View.OnClickListener positiveButton = view -> {
        dialog.dismiss();

        MainActivity.progressBar.setVisibility(View.VISIBLE);
        for(int qrNum = 0; qrNum < qrImagePreviewList.size(); qrNum++){

            // bitmap이미지를 jpeg로 저장
            saveBitmaptoPng(getContext(), qrNum, qrImagePreviewList.get(qrNum));
        }

        MainActivity.progressBar.setVisibility(View.GONE);
        MainActivity.showToast(getActivity(), "QR이미지가 모두 저장 되었습니다.");
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };

    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("qrFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            //binding.refreshImageButton.setOnClickListener(onClickListener);
        }
        else{
            Log.e("qrFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.GONE);
            binding.qrFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    @Override public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu,inflater); inflater.inflate(R.menu.menu_qr,menu);
    }
    @Override public boolean onOptionsItemSelected(MenuItem item) {
        int curId = item.getItemId();
        if (curId == R.id.menu_save_all_qr) {
            MainActivity.showToast(getActivity(), "저장 버튼 클릭");
        }
        return super.onOptionsItemSelected(item);
    }

    private void showProgress(){
        binding.progressBarFragment.setVisibility(View.VISIBLE);
        getActivity().getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress(){
        binding.progressBarFragment.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    public static void saveBitmaptoPng(Context context, int qrNum, Bitmap bitmap){
        String fileName;

        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss"); //년,월,일,시간 포멧 설정
        Date time = new Date(); //파일명 중복 방지를 위해 사용될 현재시간
        String current_time = sdf.format(time); //String형 변수에 저장

        // 파일명 지정
        if(qrNum == 0) fileName = "TakeoutQR_"+current_time;
        else if(qrNum == 1) fileName = "WaitingQR_"+current_time;
        else fileName = "TableQR_" + Integer.toString(qrNum-1) + "_" + current_time;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + ".PNG");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // 현재 is_pending 상태임을 만들어준다.
            // 다른 곳에서 이 데이터를 요구하면 무시하라는 의미로, 해당 저장소를 독점할 수 있다.
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        // 이미지를 저장할 uri를 미리 설정함
        Uri item = contentResolver.insert(collection, values);

        try{
            ParcelFileDescriptor pdf = contentResolver.openFileDescriptor(item, "w", null);

            if(pdf == null){
                Log.e("pdf","null");
            }
            else{
                FileOutputStream fos = new FileOutputStream(pdf.getFileDescriptor());
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fos);
                fos.close();

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                    values.clear();

                    // IS_PENDING 값을 0으로 설정해 주고 다른 곳에서 사용 가능하도록 복구
                    values.put(MediaStore.Images.Media.IS_PENDING, 0);
                    contentResolver.update(item, values, null, null);
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static Bitmap getQrPreviewList(int pos){
        return qrImagePreviewList.get(pos);
    }
    public static void addQrPreviewList(Bitmap bitmap){
        qrImagePreviewList.add(bitmap);
    }
}