package com.example.teosutilities.fragment;

import android.Manifest;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.work.PeriodicWorkRequest;
import androidx.work.WorkManager;

import android.os.PowerManager;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.CompoundButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.teosutilities.R;
import com.example.teosutilities.activity.HomeActivity;
import com.example.teosutilities.activity.LoginActivity;
import com.example.teosutilities.activity.WelcomeActivity;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.data.NotiNewLinkWork;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.switchmaterial.SwitchMaterial;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import static com.example.teosutilities.activity.WelcomeActivity.checkTotalProfileRequest;
import static com.example.teosutilities.activity.WelcomeActivity.workManager;

public class MyUserFragment extends Fragment implements View.OnClickListener {

    ImageView iv_avatar_my_user;

    private final int BATTERY_OPTIMIZATION = 104;

    MaterialTextView tv_email_my_user;
    MaterialTextView tv_name_user;

    TextInputEditText edt_firstname_my_user;
    TextInputEditText edt_passnew_my_user;
    TextInputEditText edt_repassnew_my_user;

    TextInputLayout textInputLayout_firstname_my_user;
    TextInputLayout textInputLayout_passnew_my_user;
    TextInputLayout textInputLayout_repassnew_my_user;

    MaterialButton btn_change_info_my_user;
    MaterialButton btn_logout_my_user;

    SwitchMaterial switch_new_link;

    View fragment_my_user;
    LinearLayout layoutChangeInfo;

    Animation bottomAnim;

    FirebaseAuth mAuth;

    FirebaseUser user;



    ArrayList<TextInputLayout> textInputLayout;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_my_user, container, false);
        Mapping(view);
        loadStatusSwitchAddNewLink();



        return view;
    }


    private void Mapping(View view){
        iv_avatar_my_user = view.findViewById(R.id.iv_avatar_my_user);

        mAuth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        bottomAnim = AnimationUtils.loadAnimation(getContext(),R.anim.view_bottom_fast_animation);

        tv_email_my_user = view.findViewById(R.id.tv_email_my_user);
        tv_name_user = view.findViewById(R.id.tv_name_user);

        edt_firstname_my_user = view.findViewById(R.id.edt_firstname_my_user);
        edt_passnew_my_user = view.findViewById(R.id.edt_passnew_my_user);
        edt_repassnew_my_user = view.findViewById(R.id.edt_repassnew_my_user);

        textInputLayout_firstname_my_user = view.findViewById(R.id.textInputLayout_firstname_my_user);
        textInputLayout_passnew_my_user = view.findViewById(R.id.textInputLayout_passnew_my_user);
        textInputLayout_repassnew_my_user = view.findViewById(R.id.textInputLayout_repassnew_my_user);

        btn_logout_my_user = view.findViewById(R.id.btn_logout_my_user);
        btn_change_info_my_user = view.findViewById(R.id.btn_change_info_my_user);

        fragment_my_user = view.findViewById(R.id.fragment_my_user);

        layoutChangeInfo = view.findViewById(R.id.layout_change_info);

        switch_new_link = view.findViewById(R.id.switch_new_link);



        textInputLayout_firstname_my_user.setAnimation(bottomAnim);
        textInputLayout_passnew_my_user.setAnimation(bottomAnim);
        textInputLayout_repassnew_my_user.setAnimation(bottomAnim);
        btn_change_info_my_user.setAnimation(bottomAnim);
        btn_logout_my_user.setAnimation(bottomAnim);

        tv_name_user.setText(mAuth.getCurrentUser().getDisplayName());
        tv_email_my_user.setText(mAuth.getCurrentUser().getEmail());
//        iv_avatar_my_user.setImageURI(mAuth.getCurrentUser().getPhotoUrl());


        btn_change_info_my_user = view.findViewById(R.id.btn_change_info_my_user);
        btn_logout_my_user = view.findViewById(R.id.btn_logout_my_user);



        textInputLayout = new ArrayList<>();

        textInputLayout.add(textInputLayout_passnew_my_user);
        textInputLayout.add(textInputLayout_repassnew_my_user);

        edt_firstname_my_user.setText(mAuth.getCurrentUser().getDisplayName());

        btn_logout_my_user.setOnClickListener(this::onClick);
        btn_change_info_my_user.setOnClickListener(this::onClick);



//        ---------Thong bao khi co link moi-------------
        switch_new_link.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
//               Neu da dong y nhan thong bao link moi
//               - Xin cap quyen tu khoi chay
//               - Khoi chay work manager
//                - Nguoc lai huy work manager
                if (switch_new_link.isChecked()){
//                  Xin cap quyen tu khoi chay tu cac thiet bi ROM trung quoc

                    if (!loadStatusSwitchAddNewLink())
                        requestAutoStartupPermission();
                        switch_new_link.setChecked(true);

                    setStatusSwitchAddNewLink(true);
//                  Khoi chay work Notification New Link
                    workManager.enqueue(checkTotalProfileRequest);
                }
                else {
                    setStatusSwitchAddNewLink(false);

                    workManager.cancelAllWork();
                }
            }
        });


        // Check for existing Google Sign In account, if the user is already signed in
// the GoogleSignInAccount will be non-null.
//        Kiểm tra nếu đăng nhập bằng Google thì ẩn chức năng đổi thông tin
        if (HomeActivity.methodLogin == 1){
            edt_firstname_my_user.setVisibility(View.GONE);
            edt_passnew_my_user.setVisibility(View.GONE);
            edt_repassnew_my_user.setVisibility(View.GONE);
            btn_change_info_my_user.setVisibility(View.GONE);
            TextView noti = new TextView(view.getContext());
            MaterialButton btnChangeInfoGG = new MaterialButton(view.getContext());
            btnChangeInfoGG.setText("Đổi thông tin tại Google Account");
            btnChangeInfoGG.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://myaccount.google.com/"));
                    startActivity(i);
                }
            });
            btnChangeInfoGG.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            layoutChangeInfo.addView(btnChangeInfoGG);
        }

    }

    @Override
    public void onClick(View view) {
        if (view == btn_logout_my_user){
            FirebaseAuth.getInstance().signOut();
            Intent intent = new Intent(getContext(), LoginActivity.class);
            startActivity(intent);
            getActivity().finish();
        }

        if (view == btn_change_info_my_user){
            changeInfoUser();
        }
    }



    private void changeInfoUser(){


//        Neu nguoi dung nhap pass cu moi doi mat khau
        if(!textInputLayout_passnew_my_user.getEditText().getText().toString().isEmpty()) {
            boolean noErrors = true;
            for (TextInputLayout textInputLayout : textInputLayout) {
                String editTextString = textInputLayout.getEditText().getText().toString();
                if (editTextString.isEmpty()) {
                    textInputLayout.setError(getResources().getString(R.string.error_empty_text));
                } else {
                    textInputLayout.setError(null);
                }
            }

            if (noErrors) {
                String newPass = edt_passnew_my_user.getText().toString().trim();
                String reNewPass = edt_repassnew_my_user.getText().toString().trim();
                changePass(newPass,reNewPass);
            }

        }
//        Doi ten nguoi dung
        else {
            Snackbar.make(fragment_my_user,"Đang thay đổi thông tin...",Snackbar.LENGTH_SHORT).show();
            UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                    .setDisplayName(edt_firstname_my_user.getText().toString()).build();
            mAuth.getCurrentUser().updateProfile(userProfileChangeRequest).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {
                    mAuth.getCurrentUser().reload();
                    tv_name_user.setText(mAuth.getCurrentUser().getDisplayName());
                    Snackbar.make(fragment_my_user,"Đổi thông tin thành công",Snackbar.LENGTH_SHORT).show();
//                    ------Ghi log thay thong tin
                    DataHelper dataHelper = new DataHelper();
                    dataHelper.writeLogChangeInfo(getContext(),mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getEmail(),"Change Name");

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(fragment_my_user,"Đổi thông tin thất bại",Snackbar.LENGTH_SHORT).show();
                    Log.e("ChangePass", String.valueOf(e));
                }
            });
        }

    }


    private void changePass(String newPass, String reNewPass){
        //                Tiến hành đổi mật khẩu
        Snackbar.make(fragment_my_user,"Đang thay đổi mật khẩu...",Snackbar.LENGTH_SHORT).show();
        if (newPass.equals(reNewPass)) {
            user.updatePassword(newPass).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    mAuth.getCurrentUser().reload();
                    Snackbar.make(fragment_my_user,"Đổi mật khẩu thành công",Snackbar.LENGTH_SHORT).show();
                    //                    ------Ghi log thay thong tin
                    DataHelper dataHelper = new DataHelper();
                    dataHelper.writeLogChangeInfo(getContext(),mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getEmail(),"Change Password");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Snackbar.make(fragment_my_user,"Đổi mật khẩu thất bại, vui lòng đăng xuất và thử lại",Snackbar.LENGTH_SHORT).show();
                    Log.e("ChangePass", String.valueOf(e));
                }
            });
        }
    }

    private boolean loadStatusSwitchAddNewLink(){

        boolean statusSwitch = WelcomeActivity.sharedPreferences.getBoolean("addNewLink", false);
        switch_new_link.setChecked(statusSwitch);
        return statusSwitch;
    }

    private void setStatusSwitchAddNewLink(boolean status){
        WelcomeActivity.editor.putBoolean("addNewLink",status);
        WelcomeActivity.editor.commit();
    }

    public static void BatteryOptimization(Context context){
        if (ContextCompat.checkSelfPermission(context, Manifest.permission.REQUEST_IGNORE_BATTERY_OPTIMIZATIONS) == PackageManager.PERMISSION_DENIED){
            Intent intent = new Intent();
            intent.setAction(Settings.ACTION_IGNORE_BATTERY_OPTIMIZATION_SETTINGS);
            context.startActivity(intent);
        }
    }

    private void requestAutoStartupPermission() {
        try {
            Intent intent = new Intent();
            String manufacturer = android.os.Build.MANUFACTURER;
            Log.i("manufacturer",manufacturer);
            if ("xiaomi".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.miui.securitycenter", "com.miui.permcenter.autostart.AutoStartManagementActivity"));
            } else if ("oppo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.coloros.safecenter", "com.coloros.safecenter.permission.startup.StartupAppListActivity"));
            } else if ("vivo".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.vivo.permissionmanager", "com.vivo.permissionmanager.activity.BgStartUpManagerActivity"));
            } else if ("Letv".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.letv.android.letvsafe", "com.letv.android.letvsafe.AutobootManageActivity"));
            } else if ("Honor".equalsIgnoreCase(manufacturer)) {
                intent.setComponent(new ComponentName("com.huawei.systemmanager", "com.huawei.systemmanager.optimize.process.ProtectActivity"));
            }

            List<ResolveInfo> list = getActivity().getPackageManager().queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
            Log.i("listIntentAutoStartup", String.valueOf(list));
            if  (list.size() > 0) {
                MaterialAlertDialogBuilder dialogrequestAutoStartupPermission =  new MaterialAlertDialogBuilder(getContext(), R.style.MaterialAlertDialog_MaterialComponents)
                        .setTitle("Lưu ý")
                        .setMessage("Những ROM Trung Quốc(Xiaomi, Oppo, Vivo, Letv, Honor,..vv..) sẽ ngăn việc chạy ngầm để tiết kiệm pin" +
                                "\nDo đó sẽ không thể nhận được thông báo mới" +
                                "\nVui lòng cho phép ứng dụng quyền Tự Khởi Chạy để ứng dụng có thể cập nhật thông báo");

                dialogrequestAutoStartupPermission.setPositiveButton("Bật Tự Khởi Chạy", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        startActivity(intent);
                        switch_new_link.setChecked(true);
                    }
                });
                dialogrequestAutoStartupPermission.setNegativeButton("Từ chối", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        switch_new_link.setChecked(false);
                        setStatusSwitchAddNewLink(false);
                        dialogInterface.dismiss();

                    }
                });
                dialogrequestAutoStartupPermission.show();

            }
        } catch (Exception e) {
            Log.e("exc" , String.valueOf(e));
        }


    }
}