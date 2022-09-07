package com.example.teosutilities.activity;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.data.FbProfile;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.progressindicator.LinearProgressIndicator;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.teosutilities.data.DataHelper.mAuth;
import static com.example.teosutilities.data.DataHelper.myData;
import static com.example.teosutilities.data.DataHelper.storageRef;

public class ChangeFbProfileActivity extends AppCompatActivity implements View.OnClickListener {

    TextInputEditText inputChangeLink;
    MaterialButton btnChangeSave;
    MaterialButton btnChangePickImage;
    MaterialButton btnChangeExit;
    View view;

    TextInputLayout inputLayoutChangeLink;

    ImageView ivChangeAvatar;

    String imagePathEx;
    Bitmap imageBitmapEx;

    String UrlImageDB;

    Uri imageUriEx;

    DataHelper dataHelper;

    String keyNote;
    FbProfile fbProfile;

    int RESULT_LOAD_IMAGE = 102;
    LinearProgressIndicator loading;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_change_fb_profile);

        view = findViewById(R.id.activity_change_profile);
        inputChangeLink = (TextInputEditText) findViewById(R.id.input_change_link);
        btnChangeSave = (MaterialButton) findViewById(R.id.btn_change_save);
        btnChangePickImage = (MaterialButton) findViewById(R.id.btn_change_image);
        btnChangeExit = (MaterialButton) findViewById(R.id.btn_change_exit);
        ivChangeAvatar = (ImageView) findViewById(R.id.iv_change_avatar);

        inputLayoutChangeLink = findViewById(R.id.inputLayout_change_link);

        loading = findViewById(R.id.progress_bar_change_profile);

        getProfileChange();

        setDataForView();

        dataHelper = new DataHelper();

//        Event
        btnChangeSave.setOnClickListener(this);
        btnChangePickImage.setOnClickListener(this);
        btnChangeExit.setOnClickListener(this);

        inputChangeLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //                Kiem tra dinh dang link nhap vao co dung k
                if (!Patterns.WEB_URL.matcher(inputChangeLink.getText()).matches()){
                    inputLayoutChangeLink.setError("Vui lòng nhập đúng định dạng https://www.facebook.com/.....");
                }
                else{
                    inputLayoutChangeLink.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });

//        Set pro for view

        checkPermission();
    }

//    Gan thong tin cua profile can sua vao cac view
    private void setDataForView() {
        inputChangeLink.setText(fbProfile.link);
        Picasso.with(this).load(fbProfile.imageUrl).into(ivChangeAvatar);

    }

    @Override
    public void onClick(View v) {

        if(v==btnChangeSave){

            loading.setVisibility(View.VISIBLE);
//                Kiem tra da dien link chua
            if(inputChangeLink.getText().toString().isEmpty()){
                inputLayoutChangeLink.setError("Vui lòng điền liên kết facebook");
            }
            else {
//                  ----------------Kiem tra link da dung dinh dang chua---------------------
                    if (Patterns.WEB_URL.matcher(inputChangeLink.getText()).matches()){


                            // ---------------------Tien hanh luu-------------------------
                            inputLayoutChangeLink.setError(null);



                            loading.setProgress(10);

                            Snackbar.make(view,"Đang lưu, vui lòng đợi", BaseTransientBottomBar.LENGTH_SHORT).show();

                            changeProfileFb(fbProfile.id,inputChangeLink.getText().toString().trim(), mAuth.getCurrentUser().getEmail().trim(),fbProfile.keyNote);

                            loading.setVisibility(View.GONE);

                    }
                    else {
                        inputLayoutChangeLink.setError("Vui lòng nhập đúng định dạng https://www.facebook.com/.....");
                    }
            }


            //Define what to do when button is clicked
        }

        if(v==btnChangePickImage){
//            Chọn ảnh
            Intent i = new Intent(
                    Intent.ACTION_PICK,
                    android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(i, RESULT_LOAD_IMAGE);
            // start picker to get image for cropping and then use the image in cropping activity

//            Intent intent = new Intent();
//            intent.setType("image/*");
//            intent.setAction(Intent.ACTION_GET_CONTENT);
//            startActivityForResult(Intent.createChooser(intent,"Select Picture"), RESULT_LOAD_IMAGE);
            //Define what to do when button is clicked
        }

        if(v==btnChangeExit){
            finish();


            //Define what to do when button is clicked
        }

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data){
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RESULT_LOAD_IMAGE && resultCode == RESULT_OK && null !=data){

//            Code old
            imageUriEx = data.getData(); //URI
            String[] filePathColumn = { MediaStore.Images.Media.DATA };
            Cursor cursor = getContentResolver().query(imageUriEx,
                    filePathColumn, null, null, null);
            cursor.moveToFirst();

            int columnIndex = cursor.getColumnIndex(filePathColumn[0]);

            this.imagePathEx = cursor.getString(columnIndex);
            cursor.close();


            this.imageBitmapEx = BitmapFactory.decodeFile(imagePathEx); //Bitmap

//            ivAddAvatar.setImageURI(selectedImage);
//            //                    Code new
//
//            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            ivChangeAvatar.setImageBitmap(imageBitmapEx);


        }
        else {
            Toast.makeText(this, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show();
        }

    }

    public void changeProfileFb(String id, String link, String author,String key){

//        Neu nguoi dung thay doi anh thi upload anh len storage
//        Tiep theo luu imageUrl va urlProfile moi len Db
//        Neu chi thay doi ten thi khong upload anh va su dung imageurl cu

        if(imageBitmapEx != null){
            //        ------------Luu anh len firebase -----------------
            StorageReference riversRef = storageRef.child("imagesFbProfile/"+ Calendar.getInstance().getTimeInMillis());
            UploadTask uploadTask = riversRef.putFile(imageUriEx);

            loading.setProgress(20);

            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                @Override
                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                    if (!task.isSuccessful()) {
                        throw task.getException();
                    }
                    // Continue with the task to get the download URL
                    return riversRef.getDownloadUrl();

                }
            })
                    .addOnCompleteListener(new OnCompleteListener<Uri>() {
                        @Override
                        public void onComplete(@NonNull Task<Uri> task) {
                            if (task.isSuccessful()) {
                                Toast.makeText(ChangeFbProfileActivity.this,"Lưu ảnh thành công",Toast.LENGTH_SHORT).show();
//                  ---------------Luu thanh cong -> Lay imageUrl de luu len DB------------------
                                loading.setProgress(40);

                                Uri downloadUrl =  task.getResult(); // URL ảnh của profile
                                setUrlImage(String.valueOf(downloadUrl));
                                //                  ------------------------------Bat dau luu profile len DB------------------------------------
                                Log.i("Save",id + "\n" + link + "\n" + downloadUrl + "\n" + author);

                                loading.setProgress(60);

//                          ---------Luu len DB---------------
                                DataHelper.updateFbProfile(ChangeFbProfileActivity.this,
                                        fbProfile.id,
                                        inputChangeLink.getText().toString().trim(),
                                        String.valueOf(downloadUrl),
                                        mAuth.getCurrentUser().getEmail(),
                                        fbProfile.keyNote);

//                                Ghi lai log change profile
                                dataHelper.writeLogChangeNote(ChangeFbProfileActivity.this,mAuth.getCurrentUser().getUid(),author, id, key);

                                loading.setProgress(100);
                                Intent intent = new Intent(ChangeFbProfileActivity.this, HomeActivity.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    })
                    .addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(ChangeFbProfileActivity.this,"Lưu ảnh thất bại",Toast.LENGTH_SHORT).show();
                            Intent intent = new Intent(ChangeFbProfileActivity.this, HomeActivity.class);
                            startActivity(intent);
                            finish();
                        }
                    });

        }
        else {
            DataHelper.updateFbProfile(ChangeFbProfileActivity.this,
                    fbProfile.id,inputChangeLink.getText().toString().trim(),
                    fbProfile.imageUrl,
                    mAuth.getCurrentUser().getEmail(),
                    fbProfile.keyNote);
            Intent intent = new Intent(ChangeFbProfileActivity.this, HomeActivity.class);
            startActivity(intent);
            finish();
        }

    }

    public void setUrlImage(String Url){
        this.UrlImageDB = Url;
    }

    private void checkPermission() {

        // Check if the Storage permission has been granted
        if (ActivityCompat.checkSelfPermission(ChangeFbProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED) {
            // Permission is already available, start camera preview
        } else {
            // Permission is missing and must be requested.
            requestStoragePermission();
        }
        // END_INCLUDE(startCamera)
    }

    private void requestStoragePermission() {
        // Permission has not been granted and must be requested.
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE)) {
            // Provide an additional rationale to the profileFb if the permission was not granted
            // and the profileFb would benefit from additional context for the use of the permission.
            // Display a SnackBar with cda button to request the missing permission.
            // Request the permission
            ActivityCompat.requestPermissions(ChangeFbProfileActivity.this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},
                    1);

        } else {
            Toast.makeText(this, "Cấp quyền sử dụng Bộ nhớ cho em đi anh zai ơi !", Toast.LENGTH_SHORT).show();
            // Request the permission. The result will be received in onRequestPermissionResult().
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == 1) {
            // Request for camera permission.
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(this, "Đã được cấp quyền Bộ Nhớ",
                        Toast.LENGTH_SHORT)
                        .show();
            } else {
                // Permission request was denied.
                Toast.makeText(this, "Từ chối cấp quyền sử dụng Bộ Nhớ !",
                        Toast.LENGTH_SHORT)
                        .show();
            }
        }
        // END_INCLUDE(onRequestPermissionsResult)
    }

    public static String writeNewFbProfile(String id,String link, String imgUrl, String author) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = myData.child("FbProfile").push().getKey();

        FbProfile user = new FbProfile(id,link,imgUrl,author, key);

        Map<String, Object> postValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/ProfileFb/" +key, postValues);

        myData.updateChildren(childUpdates);
        return key;
    }

    public void getProfileChange(){
        Intent intent = this.getIntent();
        keyNote = intent.getStringExtra("keyNote");

        for (FbProfile item : DataHelper.fbProfiles) {
            if(keyNote.trim().equals(item.keyNote.trim()))
                fbProfile = item;
        }

    }
}