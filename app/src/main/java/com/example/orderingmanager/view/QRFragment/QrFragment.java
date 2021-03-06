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

        //???????????? ??????
        ViewPagerAdapter adapter = new ViewPagerAdapter(getActivity(), 1,3);
        binding.qrViewPager.setAdapter(adapter);

        new TabLayoutMediator(binding.tabLayoutQr, binding.qrViewPager,
                new TabLayoutMediator.TabConfigurationStrategy() {
                    @Override
                    public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                        switch (position){
                            case 0:
                                tab.setText("??????");
                                break;
                            case 1:
                                tab.setText("?????????");
                                break;
                            default:
                                tab.setText("?????????");
                                break;
                        }
                    }
                }).attach();

        return view;
    }

    @SuppressLint("SetTextI18n")
    private void extractQrViews(){

        // qrFragment??? ???????????? add????????? ????????????
        // ?????? ????????? ?????????
        qrImagePreviewList = new ArrayList<>();

        for(int i = 0; i < UserInfo.getTableCount()+2; i++){
            binding.ivQrcoderv.setImageBitmap(QrList.getQrBitmap(i));
            binding.tvQrStoreName.setText(UserInfo.getRestaurantName());
            Log.e("i",Integer.toString(i));
            // i == 0, ????????? QR
            // i == 1, ???????????? QR
            // i >= 2, ????????? QR
            switch (i){
                case 0:
                    binding.tvExplain.setText("???????????? ??????\nQR ?????? ?????? ???????????????");
                    binding.tvTableNum.setVisibility(View.GONE);
                    break;
                case 1:
                    binding.tvExplain.setText("??? ?????? ??????\nQR ?????? ?????? ???????????????");
                    binding.tvTableNum.setVisibility(View.GONE);
                    break;
                default:
                    binding.tvTableNum.setVisibility(View.VISIBLE);
                    binding.tvTableNum.setText( i-1 + "??? ?????????");
                    binding.tvExplain.setText("???????????????\nQR ?????? ???????????????");
                    break;
            }
            capture();
        }
        MainActivity.progressBar.setVisibility(View.GONE);
        getActivity().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void capture(){
        LinearLayout linearlayout = binding.viewQrCapture;

        // width???  height??? null??? ???????????? ????????? View??? ?????? ???????????????.
        linearlayout.measure(View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED),
                View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED));

        // view??? ?????? ?????? ????????? ????????? ?????? ??????
        linearlayout.layout(0, 0, linearlayout.getMeasuredWidth(), linearlayout.getMeasuredHeight());

        linearlayout.setDrawingCacheEnabled(true);
        linearlayout.buildDrawingCache(true);
        Bitmap bitmap = Bitmap.createBitmap(linearlayout.getDrawingCache());

        // ??????, ?????????, ????????? ?????? ?????? text??? ???????????????
        // setDrawingCacheEnabled(false)??? ???????????????.
        // ????????? ?????? ???????????? ????????? ???????????? ??????
        linearlayout.setDrawingCacheEnabled(false);

        addQrPreviewList(bitmap);

    }

    private void initButtonClickListener(){
        // ?????? ?????? ??????
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
                "QR?????? ???????????? ?????? ???????????? ???????????????????",
                "???????????? ????????? ????????? ????????? ?????? ???????????? ???????????????.",
                "????????????","??????",
                positiveButton,negativeButton);

        dialog.show();
    }

    private final View.OnClickListener positiveButton = view -> {
        dialog.dismiss();

        MainActivity.progressBar.setVisibility(View.VISIBLE);
        for(int qrNum = 0; qrNum < qrImagePreviewList.size(); qrNum++){

            // bitmap???????????? jpeg??? ??????
            saveBitmaptoPng(getContext(), qrNum, qrImagePreviewList.get(qrNum));
        }

        MainActivity.progressBar.setVisibility(View.GONE);
        MainActivity.showToast(getActivity(), "QR???????????? ?????? ?????? ???????????????.");
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };

    public void storeInfoCheck(){
        storeInitInfo = UserInfo.getRestaurantId() != null;
        if(!storeInitInfo){
            Log.e("qrFragment",storeInitInfo.toString());
            binding.viewErrorLoadStore.errorNotFound.setVisibility(View.VISIBLE);
            binding.btnDownloadQr.setVisibility(View.GONE);
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
            MainActivity.showToast(getActivity(), "?????? ?????? ??????");
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
        SimpleDateFormat sdf = new SimpleDateFormat( "yyyyMMddHHmmss"); //???,???,???,?????? ?????? ??????
        Date time = new Date(); //????????? ?????? ????????? ?????? ????????? ????????????
        String current_time = sdf.format(time); //String??? ????????? ??????

        // ????????? ??????
        if(qrNum == 0) fileName = "TakeoutQR_"+current_time;
        else if(qrNum == 1) fileName = "WaitingQR_"+current_time;
        else fileName = "TableQR_" + Integer.toString(qrNum-1) + "_" + current_time;

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, fileName + ".PNG");
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/png");

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            // ?????? is_pending ???????????? ???????????????.
            // ?????? ????????? ??? ???????????? ???????????? ??????????????? ?????????, ?????? ???????????? ????????? ??? ??????.
            values.put(MediaStore.Images.Media.IS_PENDING, 1);
        }

        ContentResolver contentResolver = context.getContentResolver();
        Uri collection = MediaStore.Images.Media.getContentUri(MediaStore.VOLUME_EXTERNAL_PRIMARY);

        // ???????????? ????????? uri??? ?????? ?????????
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

                    // IS_PENDING ?????? 0?????? ????????? ?????? ?????? ????????? ?????? ??????????????? ??????
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