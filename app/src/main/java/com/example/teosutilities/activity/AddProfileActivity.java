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

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
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
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;
import java.util.HashMap;
import java.util.Map;

import static com.example.teosutilities.data.DataHelper.fbProfiles;

public class AddProfileActivity extends AppCompatActivity implements View.OnClickListener {

    DataHelper dataHelper;
    TextInputEditText inputAddLink;
    MaterialButton btnSave;
    MaterialButton btnPickImage;
    MaterialButton btnExit;
    MaterialTextView tvUrlImage;
    MaterialTextView tvAddUrl;
    View view;

    TextInputLayout inputLayout_add_link;

    ImageView ivAddAvatar;

    String imagePathEx;
    File imageFileEx;
    Bitmap imageBitmapEx;

    String imagePathIn;
    File imageFileIn;
    Bitmap imageBitmapIn;

    String UrlImageDB;

    Uri imageUriEx;

    int RESULT_LOAD_IMAGE = 101;
    int countProfile;


    LinearProgressIndicator loading;







//  Firebase
    FirebaseStorage storage;
    StorageReference storageRef;
    DatabaseReference myData;
//    private FirebaseAuth mAuth;
    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_profile);

        view = findViewById(R.id.activity_add_profile);
        inputAddLink = (TextInputEditText) findViewById(R.id.input_add_link);
        btnSave = (MaterialButton) findViewById(R.id.btn_save);
        btnPickImage = (MaterialButton) findViewById(R.id.btn_add_image);
        btnExit = (MaterialButton) findViewById(R.id.btn_exit_add_fb);
        ivAddAvatar = (ImageView) findViewById(R.id.iv_add_avatar);

        inputLayout_add_link = findViewById(R.id.inputLayout_add_link);

        myData = FirebaseDatabase.getInstance().getReference();
        storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        mAuth = FirebaseAuth.getInstance();
        dataHelper = new DataHelper();

        loading = findViewById(R.id.progress_bar_add_profile);

//        Event
        btnSave.setOnClickListener(this);
        btnPickImage.setOnClickListener(this);
        btnExit.setOnClickListener(this);

        inputAddLink.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                //                Kiem tra dinh dang link nhap vao co dung k
                if (!Patterns.WEB_URL.matcher(inputAddLink.getText()).matches()){
                    inputLayout_add_link.setError("Vui lòng nhập đúng định dạng https://www.facebook.com/.....");
                }
                else{
                    inputLayout_add_link.setError(null);
                }

                FbProfile isProfileFb = isUrlFbExist(inputAddLink.getText().toString());
                if ( isProfileFb != null){
                    inputLayout_add_link.setError("Profile đã tồn tại!");
                    Picasso.with(AddProfileActivity.this).load(isProfileFb.imageUrl).into(ivAddAvatar);
                }
            }

            @Override
            public void afterTextChanged(Editable editable) {



            }
        });



//        Set pro for view

        checkPermission();

    }

    //This is the part where data is transafeered from Your Android phone to Sheet by using HTTP Rest API calls
    private void   addItemToSheet(String id, String link, String imageUrl, String author) {

        StringRequest stringRequest = new StringRequest(Request.Method.POST, "https://script.google.com/macros/s/AKfycbxA34XnP_XIVIPt0Pqrr1VNRIX13Xyb1rTcmXgkDJ_auQ307O6f/exec",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
//                        Toast.makeText(AddProfileActivity.this,response,Toast.LENGTH_LONG).show();
                        Log.e("Response", response);

                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> parmas = new HashMap<>();

                //here we pass params
                parmas.put("action","addItem");
                parmas.put("id",id);
                parmas.put("link",link);
                parmas.put("imageUrl",imageUrl);
                parmas.put("author",author);

                return parmas;
            }
        };

        int socketTimeOut = 10000;

        RetryPolicy retryPolicy = new DefaultRetryPolicy(socketTimeOut, 0, DefaultRetryPolicy.DEFAULT_BACKOFF_MULT);
        stringRequest.setRetryPolicy(retryPolicy);

        RequestQueue queue = Volley.newRequestQueue(this);

        queue.add(stringRequest);


    }

    @Override
    public void onClick(View v) {

        if(v==btnSave){

            loading.setVisibility(View.VISIBLE);
//                Kiem tra da dien link chua
            if(inputAddLink.getText().toString().isEmpty()){
                inputLayout_add_link.setError("Vui lòng điền liên kết facebook");
            }
            else {
                //            Kiem tra da chon anh chua
                if (imageBitmapEx != null){

//                  ----------------Kiem tra link da dung dinh dang chua---------------------
                    if (Patterns.WEB_URL.matcher(inputAddLink.getText()).matches()){

                        //                 Kiem tra url da ton tai chua
                        FbProfile isProfileFb = isUrlFbExist(inputAddLink.getText().toString());
                        if ( isProfileFb == null){

                            // ---------------------Tien hanh luu-------------------------
                            inputLayout_add_link.setError(null);



                            loading.setProgress(10);

                            Snackbar.make(view,"Đang lưu, vui lòng đợi", BaseTransientBottomBar.LENGTH_SHORT).show();

                            Log.i("allProfileFbs", String.valueOf(fbProfiles.size()));

                            //                  Lay id cua profile cuoi cung + 1 de them vao profile moi
                            int id = Integer.parseInt(fbProfiles.get(fbProfiles.size() - 1).id) + 1;
                            addNewProfileFb(String.valueOf(id),inputAddLink.getText().toString().trim(), imageUriEx, mAuth.getCurrentUser().getEmail().trim());

                            loading.setVisibility(View.GONE);


                        }
//                    Thong bao fb ton tai, Load profile len activity AddProfile
                        else {
                            inputLayout_add_link.setError("Profile đã tồn tại!");
                            Picasso.with(AddProfileActivity.this).load(isProfileFb.imageUrl).into(ivAddAvatar);
                            inputAddLink.setText("");
                            inputAddLink.hasFocus();
                        }
                    }
                    else {
                        inputLayout_add_link.setError("Vui lòng nhập đúng định dạng https://www.facebook.com/.....");
                    }
                }
                else {
                    Toast.makeText(this,"Thêm thất bại, vui lòng chọn ảnh", Toast.LENGTH_SHORT).show();
                }
            }




            //Define what to do when button is clicked
        }

        if(v==btnPickImage){
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

        if(v==btnExit){
            finish();


            //Define what to do when button is clicked
        }
    }

//    private String saveToInternalStorage(Bitmap bitmapImage){
//
//        ContextWrapper cw = new ContextWrapper(getApplicationContext());
//        // path to /data/data/yourapp/app_data/imageDir
//        File directory = cw.getDir("imageDir", Context.MODE_PRIVATE);
//        // Create imageDir
//        File mypath=new File(directory, ProfileFb.count(ProfileFb.class) + 1 + ".jpg");
//
//        FileOutputStream fos = null;
//        try {
//            fos = new FileOutputStream(mypath);
//            // Use the compress method on the BitMap object to write image to the OutputStream
//            bitmapImage.compress(Bitmap.CompressFormat.PNG, 100, fos);
//        } catch (Exception e) {
//            e.printStackTrace();
//        } finally {
//            try {
//                fos.close();
//            } catch (IOException e) {
//                e.printStackTrace();
//            }
//        }
//        return directory.getAbsolutePath();
//    }

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
            ivAddAvatar.setImageBitmap(imageBitmapEx);


        }
        else {
            Toast.makeText(this, "Bạn chưa chọn ảnh", Toast.LENGTH_SHORT).show();
        }

    }

    public String getPath()  {
        return this.tvUrlImage.getText().toString();
    }

    public FbProfile isUrlFbExist(String url){
        for (int i = 0; i < fbProfiles.size(); i++){
            if (fbProfiles.get(i).link.equals(url)){
                return fbProfiles.get(i);
            }
        }
        return null;
    }

    public void addNewProfileFb(String id, String link,Uri imageUriEx, String author){

//        Luu anh len firebase
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
                .addOnCompleteListener(new OnCompleteListener<Uri>()
                {
                    @Override
                    public void onComplete(@NonNull Task<Uri> task) {
                        if (task.isSuccessful()) {

                            loading.setProgress(40);

                            Uri downloadUrl =  task.getResult(); // URL ảnh của profile
                            setUrlImage(String.valueOf(downloadUrl));
        //                  ------------------------------Bat dau luu profile len DB------------------------------------
                            Log.i("Save",id + "\n" + link + "\n" + downloadUrl + "\n" + author);

                            loading.setProgress(60);

//                          ---------Luu len DB---------------
                           String key =  writeNewFbProfile(id,link, String.valueOf(downloadUrl),author);
//                                + 1 vao totalServer
                            myData.child("TotalProfile").setValue(DataHelper.totalProfile + 1);
//                        Gan lai localTotalProfile = totalServer + 1 vua them
                            WelcomeActivity.editor.putInt("localTotalProfile",DataHelper.totalProfile + 1);
                            WelcomeActivity.editor.commit();
//                                Ghi lai log add profile
                            dataHelper.writeLogAddNote(AddProfileActivity.this,mAuth.getCurrentUser().getUid(),author, id, key);
//                                  Ghi vao GGSheet
                            addItemToSheet(id,link,String.valueOf(downloadUrl),author);

                            Toast.makeText(AddProfileActivity.this,"Lưu thành công",Toast.LENGTH_SHORT).show();

                            loading.setProgress(100);

                            finish();
                        }
                        else{
                            Toast.makeText(AddProfileActivity.this,"Lưu thất bại",Toast.LENGTH_SHORT).show();

                            finish();
                        }
                    }
                })
                .addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(AddProfileActivity.this,"Lưu thất bại",Toast.LENGTH_SHORT).show();

                        finish();
                    }
                });


        }

    public void setUrlImage(String Url){
        this.UrlImageDB = Url;
    }

    public void uploadImgToFirebase(Uri uri){
        StorageReference riversRef = storageRef.child("imagesFbProfile/"+uri.getLastPathSegment());
        UploadTask uploadTask = riversRef.putFile(uri);


        Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
            @Override
            public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                if (!task.isSuccessful()) {
                    throw task.getException();
                }
                // Continue with the task to get the download URL
                return riversRef.getDownloadUrl();

            }
        }).addOnCompleteListener(new OnCompleteListener<Uri>() {
            @Override
            public void onComplete(@NonNull Task<Uri> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(AddProfileActivity.this,"Lưu ảnh thành công",Toast.LENGTH_SHORT).show();
                    Log.i("downloadUri", String.valueOf(task.getResult()));
                } else {
                    // Handle failures
                    // ...
                }
            }
        });
    }


    private void checkPermission() {

        // Check if the Storage permission has been granted
        if (ActivityCompat.checkSelfPermission(AddProfileActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)
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
            ActivityCompat.requestPermissions(AddProfileActivity.this,
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
        String key = DataHelper.myData.child("FbProfile").push().getKey();

        FbProfile user = new FbProfile(id,link,imgUrl,author, key);

        Map<String, Object> postValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/ProfileFb/" +key, postValues);

        DataHelper.myData.updateChildren(childUpdates);
        return key;
    }




}