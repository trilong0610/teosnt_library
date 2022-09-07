package com.example.teosutilities.activity;

import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.teosutilities.R;
import com.example.teosutilities.data.UserControl;
import com.example.teosutilities.fragment.AboutUsFragment;
import com.example.teosutilities.fragment.HomeFragment;
import com.example.teosutilities.fragment.MyUploadedFragment;
import com.example.teosutilities.fragment.MyUserFragment;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class HomeActivity extends AppCompatActivity {

    private View mainContainer;
    ChipNavigationBar chipNavigationBar;


    public static int methodLogin;// Giữ phương thức đăng nhập: Email = 0, Google = 1
    private DatabaseReference myData;
    private FirebaseAuth mAuth;

    PackageManager manager;
    PackageInfo info;
    private int versionCode;

    String urlUpdate;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnhXa();

        setEventFirebase();

        getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,new HomeFragment()).commit();

        bottomMenu();

        Snackbar.make(mainContainer,"Đang nhận dữ liệu...", Snackbar.LENGTH_SHORT).show();




    }

    private void AnhXa(){
        myData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        chipNavigationBar = findViewById(R.id.bottom_nav_menu);
        chipNavigationBar.setItemSelected(R.id.mnu_item_facebook,true);

        mainContainer = findViewById(R.id.main_container);
        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionCode = info.versionCode;
    }

    private void setEventFirebase(){

        //    ----------Kiểm tra active của account----------------
        myData.child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserControl userControl = snapshot.getValue(UserControl.class);
                if (userControl.email.equals(mAuth.getCurrentUser().getEmail()) && userControl.active == false){
                    Intent intent = new Intent(HomeActivity.this, BanActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserControl userControl = snapshot.getValue(UserControl.class);
                if (userControl.email.equals(mAuth.getCurrentUser().getEmail()) && userControl.active == false){
                    Intent intent = new Intent(HomeActivity.this, BanActivity.class);
                    startActivity(intent);
                    finish();
                }
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

        //    ----------Kiểm tra update của app----------------
//        Kiểm tra url
        myData.child("Update").child("UrlUpdate").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                urlUpdate = snapshot.getValue(String.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

//        Kiểm tra version
        myData.child("Update").child("Version").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int versionFirebase = snapshot.getValue(Integer.class);
//                Nếu version trên DB lớn hơn version hiện tại thì thông báo cập nhật
                if (versionFirebase > versionCode){
                    Intent intent = new Intent(HomeActivity.this, UpdateActivity.class);
                    intent.putExtra("urlUpdate",urlUpdate);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //    ----------Kiểm tra bảo trì của app----------------
        myData.child("MaintenanceApp").child("Status").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue(Boolean.class)) {
                    Intent intent = new Intent(HomeActivity.this, MaintenanceActivity.class);
                    startActivity(intent);
                    finish();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



    }

    private void bottomMenu() {


        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.mnu_item_facebook:
                        fragment = new HomeFragment();
                        break;
                    case R.id.mnu_item_my_facebook:
                        fragment = new MyUploadedFragment();
                        break;
                    case R.id.mnu_item_user:
                        fragment = new MyUserFragment();
                        break;
                    case R.id.mnu_item_about_us:
                        fragment = new AboutUsFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_main,fragment).commit();
            }
        });


    }



}

