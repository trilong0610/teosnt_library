package com.example.teosutilities.activity;

import android.Manifest;
import android.app.ActivityOptions;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.example.teosutilities.data.UserControl;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.teosutilities.activity.WelcomeActivity.listUserControl;


public class LoginActivity extends AppCompatActivity implements View.OnClickListener {
    private static final int RC_SIGN_IN = 10;
    private FirebaseAuth mAuth;

    public static final int PERMISSION_REQUEST_READ_STORAGE = 101;

    private TextInputEditText edtEmailLogin;
    private TextInputEditText edtPassLogin;

    private TextInputLayout textInputLayoutEmail;
    private TextInputLayout textInputLayoutPassword;


    private MaterialButton btnLogin;
    private MaterialButton btnGetRegister;
    private MaterialButton btnForgetPass;

    private Button btnLoginGG;

    private ImageView ivLogoLogin;
    private MaterialTextView tvWelcomeLogin;

    Animation bottomAnim;

    ArrayList<TextInputLayout> textInputLayout;



    private GoogleSignInClient mGoogleSignInClient;

    View mainView;



    DatabaseReference mData;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
//      Anh xa view
        AnhXa();

        mAuth = FirebaseAuth.getInstance();

        createRequestLoginGG();


    }

    private void AnhXa(){
        edtEmailLogin = findViewById(R.id.edt_email_login);
        edtPassLogin = findViewById(R.id.edt_pass_login);

        textInputLayoutEmail = findViewById(R.id.textInputLayout_email);
        textInputLayoutPassword = findViewById(R.id.textInputLayout_password);


        ivLogoLogin = findViewById(R.id.iv_logo_login);
        tvWelcomeLogin = findViewById(R.id.tv_welcome_login);

        btnLogin = findViewById(R.id.btn_login);
        btnGetRegister = findViewById(R.id.btn_get_register);
        btnForgetPass = findViewById(R.id.btn_forget_pass);

        btnLoginGG = findViewById(R.id.btn_login_gg);

        mainView = findViewById(R.id.activity_login);

        btnLogin.setOnClickListener(this::onClick);
        btnLoginGG.setOnClickListener(this::onClick);
        btnGetRegister.setOnClickListener(this::onClick);
        btnForgetPass.setOnClickListener(this::onClick);

        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.view_bottom_fast_animation);

        textInputLayout = new ArrayList<>();

        
        mData = FirebaseDatabase.getInstance().getReference();

        textInputLayout.add(textInputLayoutEmail);
        textInputLayout.add(textInputLayoutPassword);

        textInputLayoutEmail.setAnimation(bottomAnim);
        textInputLayoutPassword.setAnimation(bottomAnim);

        btnLogin.setAnimation(bottomAnim);
        btnGetRegister.setAnimation(bottomAnim);
        btnForgetPass.setAnimation(bottomAnim);
        btnLoginGG.setAnimation(bottomAnim);
        checkPermission();



    }

    @Override
    public void onClick(View view) {
        if(view == btnLogin){

            boolean noErrors = true;
            for (TextInputLayout textInputLayout : textInputLayout) {
                String editTextString = textInputLayout.getEditText().getText().toString();
                if (editTextString.isEmpty()) {
                    textInputLayout.setError(getResources().getString(R.string.error_empty_text));
                    noErrors = false;
                } else {
                    textInputLayout.setError(null);
                }
            }

            if (noErrors) {
                View viewLogin = findViewById(R.id.activity_login);
                Snackbar.make(viewLogin,"Đang đăng nhập...", BaseTransientBottomBar.LENGTH_SHORT).show();
                login();
            }




        }

        if (view == btnGetRegister){

            // set the request code to any code you like,
            // you can identify the callback via this code
            Pair[] pairs = new Pair[2];
            pairs[0] = new Pair<View, String>(ivLogoLogin, "logo_image");
            pairs[1] = new Pair<View, String>(tvWelcomeLogin, "logo_text");
            Intent i = new Intent(this,RegisterActivity.class);
            i.putExtra("value1","This value one for activityTow");
            i.putExtra("Value2", "This value two ActivityTwo");

            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                ActivityOptions options = ActivityOptions.makeSceneTransitionAnimation(LoginActivity.this, pairs);
                startActivityForResult(i, RegisterActivity.REQUEST_CODE_REGISTER,options.toBundle());
            }

        }

        if (view == btnForgetPass){
            Intent intent = new Intent(LoginActivity.this, ForgetPasswordActivity.class);
            intent.putExtra("email", edtEmailLogin.getText().toString());
            startActivity(intent);
        }

        if(view == btnLoginGG){
            signInGG();
        }
    }

    public void login(){
        mAuth.signInWithEmailAndPassword(edtEmailLogin.getText().toString().trim(), edtPassLogin.getText().toString().trim())
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("LoginResult", "signInWithEmail:success");
                            HomeActivity.methodLogin = 0;
                            FirebaseUser user = mAuth.getCurrentUser();
//                            ----Ghi LogUser Login----
                            DataHelper dataHelper = new DataHelper();
                            dataHelper.writeLogLogin(LoginActivity.this,user.getUid(),user.getEmail());

//                            -- Kiem tra neu tai khoan chua ton tai trong DB UserControl thi them vao
                            if (!isExistUser(user.getEmail())){
//                                DataHelper.myData.child("User").push().setValue(new UserControl(user.getEmail(),true,"a"));
                                writeNewUserControl(user.getEmail(),true);
                            }

//                            -- Chuyen den HomeActivity neu login thanh cong
                            Intent goToHome = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(goToHome);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("LoginResult", "signInWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Đăng nhập thất bại. Vui lòng kiểm tra lại!\n" + task.getException().getLocalizedMessage(),
                                    Toast.LENGTH_SHORT).show();
                            // ...
                        }

                        // ...
                    }

                });
    }

    public void signInGG() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    public void createRequestLoginGG(){
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && requestCode == RegisterActivity.REQUEST_CODE_REGISTER) {
            if (data.hasExtra("email")) {
                edtEmailLogin.setText(data.getExtras().getString("email"));
            }
            if (data.hasExtra("password")) {
                edtPassLogin.setText(data.getExtras().getString("password"));
            }
        }
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                Log.d("signInGG", "firebaseAuthWithGoogle:" + account.getId());
                firebaseAuthWithGoogle(account.getIdToken());
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Log.w("signInGG", "Google sign in failed", e);
                // ...
            }
        }
    }

    public void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        Snackbar.make(mainView, "Đang đăng nhập với Google...", Snackbar.LENGTH_SHORT).show();
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("signInGG", "signInWithCredential:success");
                            HomeActivity.methodLogin = 1;
                            //                            ----Ghi LogUser Login----
                            DataHelper dataHelper = new DataHelper();
                            dataHelper.writeLogLogin(LoginActivity.this,mAuth.getCurrentUser().getUid(),mAuth.getCurrentUser().getEmail());

                            FirebaseUser user = mAuth.getCurrentUser();

//                            -- Kiem tra neu tai khoan chua ton tai trong DB UserControl thi them vao
                                if (!isExistUser(user.getEmail())){
//                                    DataHelper.myData.child("User").push().setValue(new UserControl(user.getEmail(),true));
                                    writeNewUserControl(user.getEmail(),true);
                                }

//                            Chuyen den man hinh Home neu dang nhap thanh cong
                            Intent goToHome = new Intent(LoginActivity.this, HomeActivity.class);
                            startActivity(goToHome);
                            finish();
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("signInGG", "signInWithCredential:failure", task.getException());
                            Snackbar.make(mainView, "Đăng nhập với Google thất bại\n" + task.getException().getLocalizedMessage(), Snackbar.LENGTH_SHORT).show();
                        }

                        // ...
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
            Toast.makeText(LoginActivity.this,"Vui lòng cấp quyền sử dụng bộ nhớ\nđể ứng dụng có thể tự động cập nhật",Toast.LENGTH_LONG).show();
            requestStoragePermission();
        }

    }

    public void requestStoragePermission() {
        // Permission has not been granted and must be requested.
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.WRITE_EXTERNAL_STORAGE}, PERMISSION_REQUEST_READ_STORAGE);

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        // BEGIN_INCLUDE(onRequestPermissionsResult)
        if (requestCode == PERMISSION_REQUEST_READ_STORAGE) {
            // Request for camera permission.
            if (grantResults.length == 2 && grantResults[0] == PackageManager.PERMISSION_GRANTED && grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                // Permission has been granted. Start camera preview Activity.
                Toast.makeText(LoginActivity.this,"Đã cấp quyền sử dụng bộ nhớ !",Toast.LENGTH_SHORT).show();
//                requestWriteStoragePermission();
            } else {
                // Permission request was denied.
                Toast.makeText(LoginActivity.this,"Vui lòng cấp quyền sử dụng bộ nhớ\nđể ứng dụng có thể tự động cập nhật",Toast.LENGTH_LONG).show();
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

    public boolean isExistUser(String email){

        for (UserControl userControlDb : listUserControl) {
            Log.i("listUser", String.valueOf(email));
            if (userControlDb.email.equals(email)){
                return true;
            }
        }
        return false;
    }

    private void writeNewUserControl(String email, boolean active) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        String key = mData.child("User").push().getKey();
        UserControl user = new UserControl(email, active, key);

        Map<String, Object> postValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();

        childUpdates.put("/User/" +key, postValues);

        mData.updateChildren(childUpdates);
    }

}