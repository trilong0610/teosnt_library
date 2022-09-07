package com.example.teosutilities.activity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.fragment.BanAdminFragment;
import com.example.teosutilities.fragment.LogAdminFragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.ismaeldivita.chipnavigation.ChipNavigationBar;

public class AdminActivity extends AppCompatActivity {

    private View mainContainer;
    ChipNavigationBar chipNavigationBar;


    public static int methodLogin;// Giữ phương thức đăng nhập: Email = 0, Google = 1
    private DatabaseReference myData;

    PackageManager manager;
    PackageInfo info;
    private int versionCode;

    String urlUpdate;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnhXa();

        checkIsAdmin();


        getSupportFragmentManager().beginTransaction().replace(R.id.activity_admin,new LogAdminFragment()).commit();
        bottomMenu();

    }

    private void AnhXa(){
        myData = FirebaseDatabase.getInstance().getReference();
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        chipNavigationBar = findViewById(R.id.bottom_nav_menu_admin);
        chipNavigationBar.setItemSelected(R.id.mnu_item_log,true);

        mainContainer = findViewById(R.id.admin_container);
        manager = this.getPackageManager();


    }


    private void bottomMenu() {


        chipNavigationBar.setOnItemSelectedListener(new ChipNavigationBar.OnItemSelectedListener() {
            @SuppressLint("NonConstantResourceId")
            @Override
            public void onItemSelected(int i) {
                Fragment fragment = null;
                switch (i) {
                    case R.id.mnu_item_log:
                        fragment = new LogAdminFragment();
                        break;
                    case R.id.mnu_item_ban:
                        fragment = new BanAdminFragment();
                        break;

                }
                getSupportFragmentManager().beginTransaction().replace(R.id.activity_admin,fragment).commit();
            }
        });


    }

    private void checkIsAdmin() {
//      Kiem tra la tk admin moi cho truy cap
//                   Neu User hien tai != Admin thi dang xuat -> chuyen den man hinh dang nhap
        if (!DataHelper.mAuth.getCurrentUser().getUid().equals(DataHelper.uidAdmin) || DataHelper.uidAdmin.isEmpty()) {
            Toast.makeText(AdminActivity.this, "Bạn không có quyền truy cập!",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(AdminActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }
    }
}