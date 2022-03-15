package com.example.orderingmanager;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.orderingmanager.databinding.FragmentManageBinding;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class ManageFragment extends Fragment {

    private View view;
    private FragmentManageBinding binding;

    Bundle extra;

    Boolean storeInitInfo;

    private Button btnInfo;
    private Button btnStoreInfo;
    private Button btnMenuManage;
    TextView tv_nikname;
    TextView tv_storeName;
    TextView tv_name;
    TextView tv_mealMethod;
    TextView tv_category;
    TextView tv_address;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentManageBinding.inflate(inflater, container, false);
        view = binding.getRoot();

        extra = this.getArguments();
        if(extra != null) {
            extra = getArguments();

            // 매장정보 입력 여부
            storeInitInfo = extra.getBoolean("StoreInitInfo");

            /* 이곳에 받아올 데이터를 추가하십셩 */

            btnMenuManage = view.findViewById(R.id.btn_manage);
            btnMenuManage.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), MenuManageActivity.class));
                    //getActivity().finish();
                }
            });

            FirebaseFirestore db = FirebaseFirestore.getInstance();
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
            //닉네임 set
            tv_nikname = view.findViewById(R.id.tv_nikname);
            DocumentReference docRef = db.collection("users").document(user.getUid());
            docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("dd",  "DocumentSnapshot data: " + document.get("닉네임"));
                            tv_nikname.setText(document.get("닉네임").toString());
                        } else {
                            Log.d("dd", "No such document");
                        }
                    } else {
                        Log.d("dd", "get failed with ", task.getException());
                    }
                }
            });

            //매장정보 set
            tv_storeName = view.findViewById(R.id.tv_storeName);
            tv_name = view.findViewById(R.id.tv_name);
            tv_mealMethod = view.findViewById(R.id.tv_mealMethod);
            tv_category = view.findViewById(R.id.tv_category);
            tv_address = view.findViewById(R.id.tv_address);
            DocumentReference docRef2 = db.collection("users").document(user.getUid()).collection("매장정보").document(
                    "5Y0qSLVyZo48elPnWJ5H"); //document이름값 난수로 되어있는데 어떻게 가져오지 ?
            docRef2.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()) {
                        DocumentSnapshot document = task.getResult();
                        if (document.exists()) {
                            Log.d("dd",  "DocumentSnapshot data: " + document.get("매장명"));
                            tv_storeName.setText(document.get("매장명").toString());
                            tv_name.setText(document.get("사업자명").toString());
                            tv_mealMethod.setText(document.get("매장종류").toString());
                        } else {
                            Log.d("dd", "No such document");
                        }
                    } else {
                        Log.d("dd", "get failed with ", task.getException());
                    }
                }
            });

            //개인정보수정 버튼 클릭 이벤트
            btnInfo = view.findViewById(R.id.btn_info);
            btnInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), EditPersonalInfoActivity.class));
                }
            });

            //매장정보수정 버튼 클릭 이벤트
            btnStoreInfo = view.findViewById(R.id.btn_store_info);
            btnStoreInfo.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(new Intent(getActivity(), EditStoreInfoActivity.class));
                }
            });

        }


        storeInfoCheck();
        return view;
    }


    public void storeInfoCheck(){
        if(!storeInitInfo){
            binding.errorNotFound.setVisibility(View.VISIBLE);
            binding.manageFragment.setVisibility(View.GONE);
            binding.refreshImageButton.setOnClickListener(onClickListener);
        }

        else{
            //메뉴입력화면 보이게 하기.
            binding.manageFragment.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        binding = null;
    }

    View.OnClickListener onClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View view) {
            switch(view.getId()){
                case R.id.refreshImageButton:
                    startActivity(new Intent(getActivity(), MainActivity.class));
                    getActivity().finish();
                    break;
            }
        }
    };



}