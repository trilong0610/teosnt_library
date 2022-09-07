package com.example.teosutilities.activity;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.google.android.material.button.MaterialButton;

public class UpdateActivity extends AppCompatActivity {

    MaterialButton btnExitUpdate;
    MaterialButton btnUpdateUpdate;
    Button btnUpdateCoppyUrl;
    ImageView iv_logo_update;
    int count = 0;
    // declare the dialog as a member field of your activity
    ProgressDialog mProgressDialog;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update);

        btnExitUpdate = findViewById(R.id.btn_exit_update);
        btnUpdateCoppyUrl = findViewById(R.id.btn_update_coppy_url);
        btnUpdateUpdate = findViewById(R.id.btn_update_update);
        iv_logo_update = findViewById(R.id.iv_logo_update);

        mProgressDialog = new ProgressDialog(UpdateActivity.this);

        checkPermission();

        btnExitUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        btnUpdateUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = getIntent();
                String urlUpdate = intent.getStringExtra("urlUpdate");
//
//                intent.setAction(Intent.ACTION_VIEW);
//                intent.setData(Uri.parse(urlUpdate));
//                startActivity(intent);
                DataHelper dataHelper = new DataHelper();

                if(!urlUpdate.isEmpty() && urlUpdate != null)
                    dataHelper.downloadApkUpdate(UpdateActivity.this,urlUpdate);



            }
        });

        btnUpdateCoppyUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Gets a handle to the clipboard service.
                ClipboardManager clipboard = (ClipboardManager)
                        getSystemService(Context.CLIPBOARD_SERVICE);
                // Creates a new text clip to put on the clipboard
                Intent intent = getIntent();
                String urlUpdate = intent.getStringExtra("urlUpdate");
                if(!urlUpdate.isEmpty() && urlUpdate != null){
                    ClipData clip = ClipData.newPlainText("urlUpdate",urlUpdate);
                    // Set the clipboard's primary clip.
                    clipboard.setPrimaryClip(clip);
                    Toast.makeText(UpdateActivity.this,"Đã sao chép liên kết",Toast.LENGTH_SHORT).show();
                }

            }
        });

    }

    public void checkPermission() {

        // Check if the read Storage permission has been granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE)
                        != PackageManager.PERMISSION_GRANTED) {
            // Permission is missing and must be requested.
            Toast.makeText(UpdateActivity.this,"Vui lòng cấp quyền sử dụng bộ nhớ\nđể ứng dụng có thể tự động cập nhật",Toast.LENGTH_LONG).show();
            requestStoragePermission();
        }

    }

    public void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        ActivityCompat.requestPermissions(this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, LoginActivity.PERMISSION_REQUEST_READ_STORAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == LoginActivity.PERMISSION_REQUEST_READ_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(UpdateActivity.this,"Đã cấp quyền sử dụng bộ nhớ !",Toast.LENGTH_SHORT).show();
//                requestWriteStoragePermission();
            } else {
                // Permission request was denied.
                Toast.makeText(UpdateActivity.this,"Vui lòng cấp quyền sử dụng bộ nhớ\nđể ứng dụng có thể tự động cập nhật",Toast.LENGTH_LONG).show();
            }
        }
//        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
//            // Request for camera permission.
//            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
//                // Permission has been granted. Start camera preview Activity.
//                Toast.makeText(LoginActivity.this,"Đã cấp quyền ghi vào bộ nhớ !",Toast.LENGTH_SHORT).show();
//            } else {
//                // Permission request was denied.
//                Toast.makeText(LoginActivity.this,"Vui lòng cấp quyền ghi vào bộ nhớ !",Toast.LENGTH_SHORT).show();
//            }
//        }
        // END_INCLUDE(onRequestPermissionsResult)
    }
}

