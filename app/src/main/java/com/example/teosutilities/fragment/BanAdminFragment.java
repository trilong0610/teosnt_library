package com.example.teosutilities.fragment;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.data.UserControl;
import com.example.teosutilities.data.UserControlAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

import java.util.ArrayList;


public class BanAdminFragment extends Fragment {

    RecyclerView rvFragmentBanContainer;

    ArrayList<UserControl> listUsers;

    UserControlAdapter userControlAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ban_admin, container, false);

        Mapping(view);

        LoadUsers();

        //        Set layout de hien thi thong tin trong recycle view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

//        Đảo ngược chiều thêm item từ dưới lên
        linearLayoutManager.setReverseLayout(true);

//        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        rvUsers.setLayoutManager(new GridLayoutManager(getContext(), 1));

        rvFragmentBanContainer.setLayoutManager(linearLayoutManager);

        userControlAdapter = new UserControlAdapter(listUsers,getContext());

        rvFragmentBanContainer.setAdapter(userControlAdapter);


        return view;
    }

    private void Mapping(View view) {
        rvFragmentBanContainer = view.findViewById(R.id.rv_fragment_ban_container);

        listUsers = new ArrayList<>();


    }


    private void LoadUsers(){
        DataHelper.myData.child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserControl userControl = snapshot.getValue(UserControl.class);
                listUsers.add(userControl);
                userControlAdapter.notifyDataSetChanged();
                rvFragmentBanContainer.scrollToPosition(listUsers.size() - 1);
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}