package com.example.orderingmanager.view.ManageFragment;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;

import com.example.orderingmanager.Dialog.CustomDialogInputType;
import com.example.orderingmanager.Dto.ResultDto;
import com.example.orderingmanager.Dto.RetrofitService;
import com.example.orderingmanager.Dto.request.MemberIdDto;
import com.example.orderingmanager.Dto.request.PasswordChangeDto;
import com.example.orderingmanager.Dto.request.PhoneNumberDto;
import com.example.orderingmanager.Dto.request.VerificationDto;
import com.example.orderingmanager.R;
import com.example.orderingmanager.UserInfo;
import com.example.orderingmanager.databinding.ActivityEditPersonalInfoBinding;
import com.example.orderingmanager.view.BasicActivity;
import com.example.orderingmanager.view.login_register.LoginActivity;

import lombok.SneakyThrows;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class EditPersonalInfoActivity extends BasicActivity {

    private ActivityEditPersonalInfoBinding binding;

    Animation complete;

    CustomDialogInputType dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditPersonalInfoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        complete = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.anim_scale);

        initButtonClickListener();
        initTextChangedListener();

        setData();

    }

    private void initButtonClickListener() {
        binding.btnLogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                logout();
            }
        });
        binding.btnDeleteAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteAccountDialog();
            }
        });

        binding.btnBackToManageFrag.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        binding.btnChangePassword.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                passwordCheck();
            }
        });

        binding.btnReverify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reverify();
            }
        });
    }

    /* 로그아웃 */
    public void logout() {
        startActivity(new Intent(this, LoginActivity.class));
        clearSharedPreferences();
        Toast.makeText(this, "로그아웃 되었습니다.", Toast.LENGTH_SHORT).show();

        // 켜져있던 모든 activity 종료
        finishAffinity();
    }

    /* 회원탈퇴 */
    public void deleteAccount() {
        try {
            showProgress();

            MemberIdDto memberId = new MemberIdDto(UserInfo.getOwnerId());

            new Thread() {
                @SneakyThrows
                public void run() {
                    // login

                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/owner/"+ UserInfo.getOwnerId().toString()+"/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.deleteaccount(UserInfo.getOwnerId());

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            if (response.isSuccessful()) {
                                ResultDto<Boolean> result;
                                result = response.body();
                                if (result.getData() != null) {
                                    // 아이디 비밀번호 일치할 때
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast(EditPersonalInfoActivity.this, "회원탈퇴 되었습니다.");
                                            clearSharedPreferences();
                                            startActivity(new Intent(EditPersonalInfoActivity.this, LoginActivity.class));

                                            // 켜져있던 모든 activity 종료
                                            finishAffinity();
                                        }
                                    });
                                } else {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showToast(EditPersonalInfoActivity.this, "서버 연결에 문제가 발생했습니다.");
                                        }
                                    });
                                }
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            Toast.makeText(EditPersonalInfoActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            Toast.makeText(EditPersonalInfoActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

    private void initTextChangedListener(){
        binding.etNewPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = binding.etNewPW.getText().toString();
                String inputCheck = binding.etCheckPW.getText().toString();
                int inputLength = binding.etNewPW.getText().toString().length();
                if (inputLength > 5) {
                    binding.ivNewPWcomplete.setVisibility(View.VISIBLE);
                    binding.ivNewPWcomplete.startAnimation(complete);
                    if(input.equals(inputCheck)){
                        binding.ivCheckPWcomplete.setVisibility(View.VISIBLE);
                        binding.ivCheckPWcomplete.startAnimation(complete);
                    }
                } else if(inputLength <6){
                    binding.ivNewPWcomplete.setVisibility(View.GONE);
                }

                if (input.length() > 0) {
                    if (!input.equals(binding.etCheckPW.getText().toString())) {
                        binding.ivCheckPWcomplete.setVisibility(View.GONE);
                    }
                }
            }
        });

        binding.etCheckPW.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void afterTextChanged(Editable editable) {
                String input = binding.etNewPW.getText().toString();
                String inputCheck = binding.etCheckPW.getText().toString();
                int inputLength = binding.etNewPW.getText().toString().length();
                if (inputLength > 5 && inputCheck.equals(input)) {
                    binding.ivCheckPWcomplete.setVisibility(View.VISIBLE);
                    binding.ivCheckPWcomplete.startAnimation(complete);
                } else {
                    binding.ivCheckPWcomplete.setVisibility(View.GONE);
                }
            }
        });
    }

    @SuppressLint("SetTextI18n")
    private void setData() {
        // 각 입력 항목에 기존 정보로 세팅

        // 점주 아이디
        binding.tvId.setText(UserInfo.getUserId());
    }

    private void clearSharedPreferences(){
        SharedPreferences loginSP = getSharedPreferences("autoLogin", Activity.MODE_PRIVATE);
        SharedPreferences.Editor spEdit = loginSP.edit();
        spEdit.clear();
        spEdit.commit();
    }

    private void passwordCheck(){
        String currentPw = binding.etNowPW.getText().toString();
        String newPw = binding.etNewPW.getText().toString();
        String checkPw = binding.etCheckPW.getText().toString();

        if(currentPw.equals(newPw)) showPopUp("비밀번호 변경 실패","현재 사용중인 비밀번호와 동일합니다.");
        else if(!newPw.equals(checkPw)) showPopUp("비밀번호 변경 실패","새로 사용할 비밀번호를 다시 확인해 주세요.");
        else if(currentPw.length() < 6 || newPw.length() < 6 || checkPw.length() < 6){
            showPopUp("비밀번호 변경 실패", "비밀번호는 6자 이상이어야 합니다.");
        }
        else if(newPw.equals(checkPw)){
            // 새로운 비밀번호 == 비밀번호 확인
            changePassword(currentPw, newPw);
        }
    }

    private void showPopUp(String title, String message) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title).setMessage(message);
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    private void changePassword(String currentPw, String newPw) {
        try {
            showProgress();

            PasswordChangeDto passwordChangeDto = new PasswordChangeDto(currentPw, newPw);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/owner/" + Long.toString(UserInfo.getOwnerId()) + "/password/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.changePassword(UserInfo.getOwnerId(), passwordChangeDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            showLongToast(EditPersonalInfoActivity.this,"비밀번호를 성공적으로 변경하였습니다");
                                            clearSharedPreferences();
                                            finish();
                                        }
                                    });
                                }
                            }else{
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다");
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();

        } catch (Exception e) {
            showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다");
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

    private void reverify(){
        String phoneNum = binding.etPhoneNum.getText().toString();
        if(phoneNum.length() != 11){
            showPopUp("전화번호 변경 실패", "전화번호를 다시 확인해 주세요.");
        }
        else{
            showProgress();
            try {

                PhoneNumberDto phoneNumberDto = new PhoneNumberDto(phoneNum);

                new Thread() {
                    @SneakyThrows
                    public void run() {
                        Retrofit retrofit = new Retrofit.Builder()
                                .baseUrl("http://www.ordering.ml/api/owner/verification/get/")
                                .addConverterFactory(GsonConverterFactory.create())
                                .build();

                        RetrofitService service = retrofit.create(RetrofitService.class);
                        Call<ResultDto<Boolean>> call = service.phoneNumber(phoneNumberDto);

                        call.enqueue(new Callback<ResultDto<Boolean>>() {
                            @Override
                            public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                                if (response.isSuccessful()) {
                                    if (response.body().getData()) {
                                        new Handler(Looper.getMainLooper()).post(new Runnable() {
                                            @Override
                                            public void run() {
                                                showCustomDialogInputType();
                                                hideProgress();
                                            }
                                        });
                                    } else {
                                        showPhoneNumberErrorPopup();
                                        hideProgress();
                                    }
                                }
                            }

                            @Override
                            public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                                showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다");
                                hideProgress();
                                Log.e("e = ", t.getMessage());
                            }
                        });
                    }
                }.start();
            } catch (Exception e) {
                Toast.makeText(EditPersonalInfoActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
                hideProgress();
                Log.e("e = ", e.getMessage());
            }
        }
    }

    private void verifyCode(String codeNum) {
        showProgress();
        try {
            String totalPhoneNum = "+82" + binding.etPhoneNum.getText().toString();
            VerificationDto verificationDto = new VerificationDto(totalPhoneNum, codeNum);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/owner/verification/check/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.verification(verificationDto);

                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {

                            ResultDto<Boolean> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                if (result.getData()) {
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            hideProgress();
                                            changePhoneNum();
                                            dialog.dismiss();
                                        }
                                    });
                                }
                                else{
                                    showLongToast(EditPersonalInfoActivity.this, "인증번호가 일치하지 않습니다");
                                    hideProgress();
                                }
                            }else{
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다");
                            hideProgress();
                            Log.e("e = onFailure : ", t.getMessage());
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(EditPersonalInfoActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = catchException : ", e.getMessage());
        }
    }

    private void changePhoneNum(){
        showProgress();
        try {
            String successPhoneNum = binding.etPhoneNum.getText().toString();
            PhoneNumberDto phoneNumberDto = new PhoneNumberDto(successPhoneNum);

            new Thread() {
                @SneakyThrows
                public void run() {
                    Retrofit retrofit = new Retrofit.Builder()
                            .baseUrl("http://www.ordering.ml/api/owner/"+UserInfo.getOwnerId()+"/phone_number/")
                            .addConverterFactory(GsonConverterFactory.create())
                            .build();

                    RetrofitService service = retrofit.create(RetrofitService.class);
                    Call<ResultDto<Boolean>> call = service.reverify(UserInfo.getOwnerId(), phoneNumberDto);
                    Log.e("run()","aaaaaaaaaaa");
                    call.enqueue(new Callback<ResultDto<Boolean>>() {
                        @Override
                        public void onResponse(Call<ResultDto<Boolean>> call, Response<ResultDto<Boolean>> response) {
                            Log.e("onResponse","aaaaaaaaaaa");
                            ResultDto<Boolean> result;
                            result = response.body();
                            if (response.isSuccessful()) {
                                Log.e("response.isSuccessful","aaaaaaaaaaa");
                                if (result.getData()) {
                                    Log.e("result.getData",Boolean.toString(result.getData()));
                                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                                        @Override
                                        public void run() {
                                            Log.e("run2","zzzzzzzzzzzz");
                                            hideProgress();
                                            showLongToast(EditPersonalInfoActivity.this,"휴대폰 번호를 변경하였습니다");
                                            finish();
                                        }
                                    });
                                }
                                else{
                                    Log.e("result.getData",Boolean.toString(result.getData()));
                                    showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다.");
                                    hideProgress();
                                }
                            }else{
                                showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다.");
                                hideProgress();
                            }
                        }

                        @Override
                        public void onFailure(Call<ResultDto<Boolean>> call, Throwable t) {
                            showLongToast(EditPersonalInfoActivity.this, "일시적인 오류가 발생했습니다");
                            hideProgress();
                            Log.e("e = ", t.getMessage());
                        }
                    });
                }
            }.start();
        } catch (Exception e) {
            Toast.makeText(EditPersonalInfoActivity.this, "일시적인 오류가 발생하였습니다.", Toast.LENGTH_LONG).show();
            hideProgress();
            Log.e("e = ", e.getMessage());
        }
    }

    private void showCustomDialogInputType(){
        dialog = new CustomDialogInputType(this,
                "휴대폰 인증",
                "문자로 받은 6자리 인증번호를 입력해 주세요",
                "인증","취소",
                positiveButton,negativeButton);

        dialog.show();
    }

    private void showPhoneNumberErrorPopup() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("휴대폰 번호 변경 실패").setMessage("이미 등록된 번호입니다.\n자세한 사항은 관리자에게 문의해 주세요.");
        builder.setPositiveButton("닫기", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int id) {
                return;
            }
        });
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    // 회원탈퇴 확인 다이얼로그
    public void deleteAccountDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this, R.style.AccountDeleteDialogStyle);

        //dialog.show();
        builder.setTitle("회원탈퇴를 하시겠습니까?").setMessage("회원탈퇴 이후에는 데이터를 복구할 수 없습니다.");
        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                deleteAccount();
            }
        });

        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });

        AlertDialog dialog = builder.show();
        TextView messageText = (TextView)dialog.findViewById(android.R.id.message);
        messageText.setTextSize(13);
        dialog.show();
    }


    private final View.OnClickListener positiveButton = view -> {
        showProgress();
        verifyCode(dialog.getReverifyCode());
    };

    private final View.OnClickListener negativeButton = view -> {
        dialog.dismiss();
    };

    private void showProgress(){
        binding.progressBar.setVisibility(View.VISIBLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE,
                WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

    private void hideProgress(){
        binding.progressBar.setVisibility(View.GONE);
        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE);
    }

}