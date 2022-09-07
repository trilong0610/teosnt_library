package com.example.teosutilities.fragment;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.example.teosutilities.activity.AddProfileActivity;
import com.example.teosutilities.data.FbProfile;
import com.example.teosutilities.data.FbProfileAdapter;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ncorti.slidetoact.SlideToActView;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

import static com.example.teosutilities.data.DataHelper.fbProfiles;

public class HomeFragment extends Fragment implements View.OnClickListener {

    Button btnId;
    public SlideToActView tvLoginName;
    public MaterialTextView tvUrl;
    public FloatingActionButton btnAddFb;
    private static final int PERMISSION_REQUEST_STORAGE = 0;
    private View mLayout;

    private TextView tvNameUserHome;

    public static Context contextHomeFragment;

    ImageView tv_back_search;
    ImageView image_front_cardview_top_search;

    DatabaseReference myData;
    public static RecyclerView rvUsers;

    public static FbProfileAdapter adapter;

    private FirebaseAuth mAuth;

    private EasyFlipView flipCardviewTop;

    EditText edt_id_search;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        // Write a message to the database
        FirebaseDatabase database = FirebaseDatabase.getInstance();

        myData = database.getReference();
        mAuth = FirebaseAuth.getInstance();

        //       Load cac profile tu FBase
        getAllProfileFbFromFirebase();

        // Khởi tạo RecyclerView.

        rvUsers = (RecyclerView) view.findViewById(R.id.rv_users);
        mLayout = view.findViewById(R.id.activity_main);

        flipCardviewTop = view.findViewById(R.id.flip_cardview_top);

        tv_back_search = view.findViewById(R.id.tv_back_search);
        image_front_cardview_top_search = view.findViewById(R.id.image_front_cardview_top_search);

        edt_id_search = view.findViewById(R.id.edt_id_search);

//       Gan view
        tvLoginName = (SlideToActView) view.findViewById(R.id.stav_login_name);
        tvNameUserHome = view.findViewById(R.id.tv_name_user_home);
        tvNameUserHome.setText("Hi, " + mAuth.getCurrentUser().getDisplayName());




//        tvUrl = (MaterialTextView) findViewById(R.id.tv_url);
        btnAddFb = (FloatingActionButton) view.findViewById(R.id.btn_add_fb);

//        Set layout de hien thi thong tin trong recycle view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

//        Đảo ngược chiều thêm item từ dưới lên
        linearLayoutManager.setReverseLayout(true);

//        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        rvUsers.setLayoutManager(new GridLayoutManager(getContext(), 1));
        rvUsers.setLayoutManager(linearLayoutManager);

        adapter = new FbProfileAdapter(fbProfiles, getContext());

        rvUsers.setAdapter(adapter);

        rvUsers.scrollToPosition(fbProfiles.size() - 1);

        btnAddFb.setOnClickListener(this);

        tv_back_search.setOnClickListener(this::onClick);

//      Reset ve list profile chinh khi thoat Search
        flipCardviewTop.setOnFlipListener(new EasyFlipView.OnFlipAnimationListener() {
            @Override
            public void onViewFlipCompleted(EasyFlipView easyFlipView, EasyFlipView.FlipState newCurrentSide) {
                if(flipCardviewTop.getCurrentFlipState() == EasyFlipView.FlipState.FRONT_SIDE){

                    rvUsers.setLayoutManager(linearLayoutManager);

                    rvUsers.setAdapter(adapter);

                    rvUsers.scrollToPosition(fbProfiles.size() - 1);
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

                rvUsers.setLayoutManager(linearLayoutManagerSearch);

               FbProfileAdapter adapter = new FbProfileAdapter(profileFbSearch, getContext());

                rvUsers.setAdapter(adapter);

            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });


        return view;
    }



    @Override
    public void onClick(View view) {
        if (view == btnAddFb){
            Intent addFb = new Intent(getContext(), AddProfileActivity.class);
            startActivity(addFb);
        }
        if (view == tv_back_search){
            flipCardviewTop.flipTheView();
        }
        if (view == image_front_cardview_top_search){
            flipCardviewTop.flipTheView();
        }
    }

    private ArrayList<FbProfile> searchIdAndUrl(String keySearch){
        ArrayList<FbProfile> resultSearch = new ArrayList<>();

        for (FbProfile profileFb : fbProfiles){
            if (keySearch.equals(profileFb.id) || keySearch.equals(profileFb.link)){
                resultSearch.add(profileFb);
                break;
            }
        }

        return resultSearch;

    }

    private void getAllProfileFbFromFirebase(){

        myData.child("ProfileFb").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                FbProfile dataFromDb = snapshot.getValue(FbProfile.class);

                fbProfiles.add(dataFromDb);

                adapter.notifyDataSetChanged();

//                ---------------Cuộn đến item trên cùng khi thêm---------------------------
                rvUsers.scrollToPosition(fbProfiles.size() - 1);

            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
//                Cap nhat lai list + adapter khi sua note
                FbProfile fbProfile = snapshot.getValue(FbProfile.class);
                for (int i = 0; i < fbProfiles.size(); i++) {
                    if (fbProfile.keyNote.equals(fbProfiles.get(i).keyNote)){
                        fbProfiles.get(i).id = fbProfile.id;
                        fbProfiles.get(i).link = fbProfile.link;
                        fbProfiles.get(i).imageUrl = fbProfile.imageUrl;
                        fbProfiles.get(i).author = fbProfile.author;
                        fbProfiles.get(i).keyNote = fbProfile.keyNote;
                        HomeFragment.adapter.notifyDataSetChanged();
                        Log.i("changeProfile","ChangeProfile");
                        break;
                    }
                }
            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                FbProfile fbProfile = snapshot.getValue(FbProfile.class);

//                Xoa profile da bi xoa khoi Arraylist

                for (int i = 0; i < fbProfiles.size(); i++) {

                    if (fbProfiles.get(i).keyNote.equals(fbProfile.keyNote)){

                        Log.i("indexRemove", String.valueOf(i));

                        fbProfiles.remove(i);
                        adapter.notifyDataSetChanged();

                        break;
                    }
                }
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