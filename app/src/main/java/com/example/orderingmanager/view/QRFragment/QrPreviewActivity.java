package com.example.orderingmanager.view.QRFragment;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;

import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.core.content.FileProvider;

import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityQrPreviewBinding;
import com.example.orderingmanager.view.MainActivity;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class QrPreviewActivity extends AppCompatActivity {

    //viewbinding
    private ActivityQrPreviewBinding binding;

    boolean transViewVisible = true;

    Animation alpha;

    int cardViewType;

    Bitmap bitmap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityQrPreviewBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        cardViewType = getIntent().getIntExtra("cardViewType", 0);
        bitmap = QrFragment.getQrPreviewList(cardViewType);

        initViews();
        initButtonListeners();
    }

    @SuppressLint("SetTextI18n")
    public void initViews(){
        switch (cardViewType){
            case 0:
                binding.tvPreviewName.setText("포장용QR");
                break;
            case 1:
                binding.tvPreviewName.setText("웨이팅용QR");
                break;
            default:
                binding.tvPreviewName.setText(cardViewType-1 + "번 테이블");
                break;
        }

        binding.tvStoreName.setText(UserInfo.getRestaurantName());

        binding.llTransview.setAlpha(1);
        alpha = new AlphaAnimation(0, 1);
        alpha.setDuration(300);
        alpha.setFillAfter(true);
        alpha.setStartOffset(500);
        binding.llTransview.setAnimation(alpha);

        binding.ivQrPreview.setImageBitmap(bitmap);

    }

    public void initButtonListeners(){

        // 뒤로가기 버튼
        binding.btnBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        // 공유 버튼
        binding.btnShare.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // 비트맵 자체를 바로 공유하면 오류가 발생함
                // 비트맵을 png로 변환하고 그것을 캐시에 저장을 함 -> 갤러리에는 안보임
                File cachePath = new File(getExternalCacheDir(), "my_images/");
                cachePath.mkdirs();

                File file = new File(cachePath, "Image_123.png");
                FileOutputStream fileOutputStream;
                try
                {
                    fileOutputStream = new FileOutputStream(file);
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, fileOutputStream);
                    fileOutputStream.flush();
                    fileOutputStream.close();

                } catch (FileNotFoundException e)
                {
                    e.printStackTrace();
                } catch (IOException e)
                {
                    e.printStackTrace();
                }

                Uri myImageFileUri = FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", file);

                Intent intent = new Intent(Intent.ACTION_SEND);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                intent.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                intent.putExtra(Intent.EXTRA_STREAM, myImageFileUri);
                intent.setType("image/png");
                startActivity(Intent.createChooser(intent, "Share with"));
            }
        });

        // 다운로드 버튼
        binding.llBtnDownload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showProgress();
                QrFragment.saveBitmaptoPng(getApplicationContext(),cardViewType, bitmap);
                MainActivity.showLongToast(QrPreviewActivity.this, "이미지가 저장되었습니다.");
                hideProgress();
            }
        });

        binding.ivTransparent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(transViewVisible) {
                    // 뷰가 보이는 상태였을 때
                    alpha = new AlphaAnimation(1, 0);
                    alpha.setDuration(0);
                    alpha.setFillAfter(true);
                    alpha.setStartOffset(0);
                    binding.llTransview.setAnimation(alpha);
                    binding.llTransview.setVisibility(View.GONE);

                    // 중간 투명 뷰의 영역을 matchParent로 변경한다.
                    // -> 투명 이미지뷰의 영역을 클릭했을 때 뷰를 보이게/안보이게 해야함
                    // 만약 위를 설정해 주지 않으면 위 아래 양쪽 투명바(?)를 클릭했을 때도 뷰가 보여짐/안보여짐
                    // 이를 막기 위해 이미지뷰의 영역을 클릭했을때만 변경되도록 설정
                    /**현재 작동 안됨 ㅣ 이유 살펴볼 것**/
                    expandArea();
                    Log.e("alpha","안보임");
                    transViewVisible = false;
                    buttonLock();
                }
                else{
                    // 뷰가 보이지 않는 상태였을 때
                    alpha = new AlphaAnimation(0, 1);
                    alpha.setDuration(0);
                    alpha.setFillAfter(true);
                    alpha.setStartOffset(0);
                    binding.llTransview.setAnimation(alpha);
                    binding.llTransview.setVisibility(View.VISIBLE);

                    // expandArea()와 이유는 동일
                    reduceArea();
                    Log.e("alpha","보임");
                    transViewVisible = true;
                    buttonLock();
                }
            }
        });
    }

//    public boolean onTouchEvent(MotionEvent event) {
//        switch (event.getAction()) {
//            case MotionEvent.ACTION_DOWN:
//                //손가락으로 화면을 누르기 시작했을 때 할 일
//                if(transViewVisible) {
//                    // 뷰가 보이는 상태였을 때
//                    alpha = new AlphaAnimation(1, 0);
//                    alpha.setDuration(0);
//                    alpha.setFillAfter(true);
//                    alpha.setStartOffset(0);
//                    binding.llTransview.setAnimation(alpha);
//
//                    // 중간 투명 뷰의 영역을 matchParent로 변경한다.
//                    // -> 투명 이미지뷰의 영역을 클릭했을 때 뷰를 보이게/안보이게 해야함
//                    // 만약 위를 설정해 주지 않으면 위 아래 양쪽 투명바(?)를 클릭했을 때도 뷰가 보여짐/안보여짐
//                    // 이를 막기 위해 이미지뷰의 영역을 클릭했을때만 변경되도록 설정
//                    expandArea();
//
//                    binding.llTransview.setVisibility(View.GONE);
//                    Log.e("alpha","안보임");
//                    transViewVisible = false;
//                    buttonLock();
//                }
//                else{
//                    // 뷰가 보이지 않는 상태였을 때
//                    alpha = new AlphaAnimation(0, 1);
//                    alpha.setDuration(0);
//                    alpha.setFillAfter(true);
//                    alpha.setStartOffset(0);
//                    binding.llTransview.setAnimation(alpha);
//
//                    // expandArea()와 이유는 동일
//                    reduceArea();
//
//                    binding.llTransview.setVisibility(View.VISIBLE);
//                    Log.e("alpha","보임");
//                    transViewVisible = true;
//                    buttonLock();
//                }
//                break;
//            case MotionEvent.ACTION_MOVE:
//                //터치 후 손가락을 움직일 때 할 일
//                break;
//            case MotionEvent.ACTION_UP:
//                //손가락을 화면에서 뗄 때 할 일
//                break;
//            case MotionEvent.ACTION_CANCEL:
//                // 터치가 취소될 때 할 일
//                break;
//            default:
//                break;
//        }
//        return true;
//    }

    private void buttonLock(){
        if(transViewVisible){
            binding.btnBack.setEnabled(true);
            binding.btnShare.setEnabled(true);
            binding.llBtnDownload.setEnabled(true);
        }
        else {
            binding.btnBack.setEnabled(false);
            binding.btnShare.setEnabled(false);
            binding.llBtnDownload.setEnabled(false);
        }
    }
    private void expandArea(){
        ConstraintSet set = new ConstraintSet();
        set.clone(binding.clPreviewRoot);
        set.connect(binding.ivTransparent.getId(), ConstraintSet.TOP, binding.clPreviewRoot.getId(), ConstraintSet.TOP);
        set.connect(binding.ivTransparent.getId(), ConstraintSet.BOTTOM, binding.clPreviewRoot.getId(), ConstraintSet.BOTTOM);
        set.applyTo(binding.clPreviewRoot);
        Log.e("expandArea","영역확장됨");
    }
    private void reduceArea(){
        ConstraintSet set = new ConstraintSet();
        set.clone(binding.clPreviewRoot);
        set.connect(binding.ivTransparent.getId(), ConstraintSet.TOP, binding.transviewTop.getId(), ConstraintSet.BOTTOM);
        set.connect(binding.ivTransparent.getId(), ConstraintSet.BOTTOM, binding.transviewBottom.getId(), ConstraintSet.TOP);
        set.applyTo(binding.clPreviewRoot);
        Log.e("expandArea","영역축소됨");
    }

    private void showProgress(){
        binding.progressBarMain.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress(){
        binding.progressBarMain.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}
