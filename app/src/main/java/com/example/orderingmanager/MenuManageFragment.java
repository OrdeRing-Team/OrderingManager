package com.example.orderingmanager;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;


public class MenuManageFragment extends Fragment {

    private View view;

    RecyclerView recyclerView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        view = inflater.inflate(R.layout.fragment_menu_manage, container, false);

        //리사이클러뷰 임시 데이터
        recyclerView = view.findViewById(R.id.recyclerview);

        ArrayList<ManageData> arrayList = new ArrayList<>();
        arrayList.add(new ManageData("후라이드 치킨", "18000"));
        arrayList.add(new ManageData("양념 치킨", "20000"));
        arrayList.add(new ManageData("간장 치킨", "20000"));
        arrayList.add(new ManageData("포테이토 피자", "15000"));
        arrayList.add(new ManageData("불고기 피자", "15000"));
        arrayList.add(new ManageData("후라이드 치킨 반 + 양념 치킨 반", "20000"));
        arrayList.add(new ManageData("양념 치킨 반 + 간장 치킨 반", "22000"));
        arrayList.add(new ManageData("양념 치킨 + 포테이토 피자", "32000"));
        arrayList.add(new ManageData("후라이드 치킨 + 포테이토 피자", "30000"));
        arrayList.add(new ManageData("간장 치킨 + 불고기 피자", "32000"));

        ManageAdapter manageAdapter = new ManageAdapter(arrayList, getActivity());
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        recyclerView.setAdapter(manageAdapter);

        return view;
    }


}