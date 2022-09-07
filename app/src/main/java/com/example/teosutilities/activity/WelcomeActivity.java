package com.example.teosutilities.activity;

import android.app.ActivityOptions;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.data.NotiNewLinkWork;
import com.example.teosutilities.data.UserControl;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.concurrent.TimeUnit;

public class WelcomeActivity extends AppCompatActivity {

    private static int SPLASH_SCREEN = 1800;
    Animation topAnim, bottomAnim;
    ImageView ivLogo;
    MaterialTextView tvLogo,tvAuthor;

    private DatabaseReference myData;

    private FirebaseAuth mAuth;

    PackageManager manager;
    PackageInfo info;
    private int versionCode;

    String urlUpdate;

    public static int totalProfileDB = 0;

    public static ArrayList<UserControl> listUserControl;

    public static PeriodicWorkRequest checkTotalProfileRequest;

    public static WorkManager workManager;

    public static SharedPreferences sharedPreferences;

    public static SharedPreferences.Editor editor;

    public static boolean statusSwitch;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_welcome);

//        Dinh nghia cac view
        AnhXa();

        getUserFromDB();

        getTotalProfileFromDB();

        getUidAdminFromDB();



//       Lay tong so link
        myData.child("TotalProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataHelper.totalProfile = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        initPreferences();

//      Neu user da dang ki nhan thong bao thi chay workmanager de check thong bao khi co link moi
        runWorkManger();

//        Delay + hien thi hieu ung truoc khi mo HomeActiv
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {

                if (checkInternet()){
                    FirebaseUser currentUser = mAuth.getCurrentUser();
                    // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
                    GoogleSignInAccount accountGG = GoogleSignIn.getLastSignedInAccount(WelcomeActivity.this);

//              Da dang nhap -> Chuyen den Home
                    if (currentUser != null){
                        DataHelper dataHelper = new DataHelper();
                        dataHelper.writeLogLogin(WelcomeActivity.this,mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getEmail());
                        Intent gotoHome = new Intent(WelcomeActivity.this, HomeActivity.class);
                        startActivity(gotoHome);
                        finish();
                    }
                    else {

//                Chua dang nhap -> chuyen den man hinh dang nhap

                        Pair[] pairs = new Pair[2];
                        pairs[0] = new Pair<View, String>(ivLogo, "logo_image");
                        pairs[1] = new Pair<View, String>(tvLogo, "logo_text");
                        Intent gotoLogin = new Intent(WelcomeActivity.this, LoginActivity.class);
                        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                            ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(WelcomeActivity.this, pairs);
                            startActivity(gotoLogin, options.toBundle());
                            finish();
                        }
                    }
                }
                else {
                    Intent intent = new Intent(WelcomeActivity.this, NoInternetActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        },SPLASH_SCREEN);
    }

    private void AnhXa(){
        topAnim = AnimationUtils.loadAnimation(this,R.anim.welcome_top_animation);
        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.welcome_bottom_animation);

        ivLogo = findViewById(R.id.iv_logo);
        tvLogo = findViewById(R.id.tv_logo);
        tvAuthor = findViewById(R.id.tv_author);

        ivLogo.setAnimation(topAnim);
        tvLogo.setAnimation(bottomAnim);
        tvAuthor.setAnimation(bottomAnim);

        myData = FirebaseDatabase.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();

        mAuth = FirebaseAuth.getInstance();

        manager = this.getPackageManager();
        try {
            info = manager.getPackageInfo(this.getPackageName(), PackageManager.GET_ACTIVITIES);
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        versionCode = info.versionCode;

        listUserControl = new ArrayList<>();

        //        Lap lai work trong 1 tiếng
        checkTotalProfileRequest = new PeriodicWorkRequest.Builder(NotiNewLinkWork.class, 1, TimeUnit.HOURS).build();

        workManager = WorkManager.getInstance(getApplicationContext());
    }

    private boolean checkInternet(){
        ConnectivityManager connectivityManager = (ConnectivityManager) this.getSystemService(this.CONNECTIVITY_SERVICE);

        NetworkInfo wifiConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo mobileConn = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
        if ((wifiConn != null && wifiConn.isConnected()) || (mobileConn != null && mobileConn.isConnected())){
            return true;
        }
        else
            return false;
    }

    private void initPreferences() {

        sharedPreferences = getSharedPreferences("UserSetting", Context.MODE_PRIVATE);
        editor = sharedPreferences.edit();

        statusSwitch = WelcomeActivity.sharedPreferences.getBoolean("addNewLink", false);

    }

    private void runWorkManger(){

        workManager.enqueue(checkTotalProfileRequest);

        if (!statusSwitch){
            workManager.cancelAllWork();
        }

    }

    public static void getUserFromDB(){
//        Lay toan bo user
        DataHelper.myData.child("User").addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                UserControl userControl = snapshot.getValue(UserControl.class);
                listUserControl.add(userControl);
                Log.i("key", String.valueOf(snapshot.getKey()));
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

    public static int getTotalLocalLink(){
        return sharedPreferences.getInt("localTotalProfile",0);
    };

    public static void setTotalLocalLink(int total){
        editor.putInt("localTotalProfile",total);
        editor.commit();
    }

    private void getTotalProfileFromDB(){

        DataHelper.myData.child("TotalProfile").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                totalProfileDB = snapshot.getValue(Integer.class);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    };

    public static boolean isStatusSwitch() {
        return statusSwitch;
    }
    
    private void getUidAdminFromDB(){
        DataHelper.myData.child("Admin").child("Uid").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                DataHelper.uidAdmin = snapshot.getValue(String.class);
                Log.i("UidAdmin",snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }


}