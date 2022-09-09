package com.example.teosutilities.fragment;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.data.FbProfile;
import com.example.teosutilities.data.FbProfileAdapter;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.database.DatabaseReference;
import com.ncorti.slidetoact.SlideToActView;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

public class MyUploadedFragment extends Fragment implements View.OnClickListener {
    public SlideToActView tvLoginName;
    public MaterialTextView tvUrl;

    private TextView tvNameUserHome;

    ImageView tv_back_search;
    ImageView image_front_cardview_top_search;

    DatabaseReference myData;
    LinearLayoutManager linearLayoutManager;
    public static FbProfileAdapter adapterMyUploaded;
    private RecyclerView rvMyuploadedUsers;

    public static ArrayList<FbProfile> myFbProfiles;

    public static FbProfileAdapter adapter;


    private EasyFlipView flipMyUploadedCardviewTop;

    EditText edt_id_search;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_uploaded, container, false);

        //       Load cac profile da tai len tu profiles



        // Khởi tạo RecyclerView.
        rvMyuploadedUsers = (RecyclerView) view.findViewById(R.id.rv_myuploaded_users);
        myFbProfiles = new ArrayList<>();
        flipMyUploadedCardviewTop = view.findViewById(R.id.flip_myuploaded_cardview_top);

        tv_back_search = view.findViewById(R.id.tv_back_search);
        image_front_cardview_top_search = view.findViewById(R.id.image_front_cardview_top_search);

        edt_id_search = view.findViewById(R.id.edt_id_search);

//       Gan view
        tvLoginName = (SlideToActView) view.findViewById(R.id.stav_login_name);
        tvNameUserHome = view.findViewById(R.id.tv_name_user_home);

        tvNameUserHome.setText("Hi, " + DataHelper.mAuth.getCurrentUser().getDisplayName());


        loadMyUploadedProfile();

//        Set layout de hien thi thong tin trong recycle view
        linearLayoutManager = new LinearLayoutManager(getContext());

//        Đảo ngược chiều thêm item từ dưới lên
        linearLayoutManager.setReverseLayout(true);

//        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);

        rvMyuploadedUsers.setLayoutManager(linearLayoutManager);

        adapterMyUploaded = new FbProfileAdapter(myFbProfiles, getContext());

        rvMyuploadedUsers.setAdapter(adapterMyUploaded);

        rvMyuploadedUsers.scrollToPosition(myFbProfiles.size() - 1);

        tv_back_search.setOnClickListener(this::onClick);

//      Reset ve list profile chinh khi thoat Search
        flipMyUploadedCardviewTop.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView easyFlipView, EasyFlipView.FlipState newCurrentSide) {
                if(flipMyUploadedCardviewTop.getCurrentFlipState() == EasyFlipView.FlipState.FRONT_SIDE){

                    rvMyuploadedUsers.setLayoutManager(linearLayoutManager);

                    rvMyuploadedUsers.setAdapter(adapterMyUploaded);

                    rvMyuploadedUsers.scrollToPosition(myFbProfiles.size() - 1);
                }
            }
        });

        image_front_cardview_top_search.setOnClickListener(this::onClick);


        edt_id_search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                String keySearch = edt_id_search.getText().toString().trim();
                ArrayList<FbProfile> profileFbSearch = searchIdAndUrl(keySearch);
                //        Set layout de hien thi thong tin trong recycle view
                LinearLayoutManager linearLayoutManagerSearch = new LinearLayoutManager(getContext());

                //        linearLayoutManager.setStackFromEnd(true);
                linearLayoutManagerSearch.setOrientation(RecyclerView.VERTICAL);

                rvMyuploadedUsers.setLayoutManager(linearLayoutManagerSearch);

                FbProfileAdapter adapterSearch = new FbProfileAdapter(profileFbSearch, getContext());

                rvMyuploadedUsers.setAdapter(adapterSearch);
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }

    private void loadMyUploadedProfile() {

        for (FbProfile profileFb : DataHelper.fbProfiles){
            if (DataHelper.mAuth.getCurrentUser().getEmail().equals(profileFb.author)){
                myFbProfiles.add(profileFb);
            }
        }
    }

    @Override
    public void onClick(View view) {

        if (view == tv_back_search){
            flipMyUploadedCardviewTop.flipTheView();
        }
        if (view == image_front_cardview_top_search){
            flipMyUploadedCardviewTop.flipTheView();
        }
    }
    private ArrayList<FbProfile> searchIdAndUrl(String keySearch){
        ArrayList<FbProfile> resultSearch = new ArrayList<>();

        for (FbProfile profileFb : myFbProfiles){
            if (keySearch.equals(profileFb.id) || keySearch.equals(profileFb.link)){
                resultSearch.add(profileFb);
                break;
            }
        }

        return resultSearch;

    }

}