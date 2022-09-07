package com.example.teosutilities.activity;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Toast;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener {

    public static int REQUEST_CODE_REGISTER = 0;


    private FirebaseAuth mAuth;

    private TextInputEditText edtEmailRegister;
    private TextInputEditText edtPassRegister;
    private TextInputEditText edtRePassRegister;
    private TextInputEditText edtFirstnameRegister;

    private TextInputLayout txtlayoutFirstnameRegister;
    private TextInputLayout txtlayoutEmailRegister;
    private TextInputLayout txtlayoutPassRegister;
    private TextInputLayout txtlayoutRepassRegister;

    private MaterialButton btnRegister;
    private MaterialButton btnGetLogin;

    String email ;
    String password ;
    String rePass ;
    Animation bottomAnim;

    View viewRegister;

    ArrayList<TextInputLayout> textInputLayout;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        AnhXa();

        mAuth = FirebaseAuth.getInstance();
    }

    private void AnhXa(){
        edtEmailRegister = findViewById(R.id.edt_email_register);
        edtPassRegister = findViewById(R.id.edt_pass_register);
        edtRePassRegister = findViewById(R.id.edt_repass_register);
        edtFirstnameRegister = findViewById(R.id.edt_firstname_register);

        txtlayoutFirstnameRegister = findViewById(R.id.txtlayout_firstname_register);
        txtlayoutEmailRegister = findViewById(R.id.txtlayout_email_register);
        txtlayoutPassRegister = findViewById(R.id.txtlayout_pass_register);
        txtlayoutRepassRegister = findViewById(R.id.txtlayout_repass_register);

        viewRegister = findViewById(R.id.activity_register);

        btnRegister = findViewById(R.id.btn_register);
        btnRegister.setOnClickListener(this::onClick);
        btnGetLogin = findViewById(R.id.btn_get_login);
        btnGetLogin.setOnClickListener(this::onClick);

        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.view_bottom_fast_animation);

        txtlayoutFirstnameRegister.setAnimation(bottomAnim);
        txtlayoutEmailRegister.setAnimation(bottomAnim);
        txtlayoutPassRegister.setAnimation(bottomAnim);
        txtlayoutRepassRegister.setAnimation(bottomAnim);

        btnRegister.setAnimation(bottomAnim);
        btnRegister.setAnimation(bottomAnim);

        textInputLayout = new ArrayList<>();

        textInputLayout.add(txtlayoutFirstnameRegister);
        textInputLayout.add(txtlayoutEmailRegister);
        textInputLayout.add(txtlayoutPassRegister);
        textInputLayout.add(txtlayoutRepassRegister);
    }

    @Override
    public void onClick(View view) {


        if(view == btnRegister){
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
                // All fields are valid!
                Snackbar.make(viewRegister,"Đang đăng ký...", BaseTransientBottomBar.LENGTH_SHORT).show();
                email = edtEmailRegister.getText().toString().trim();
                password = edtPassRegister.getText().toString().trim();
                rePass = edtRePassRegister.getText().toString().trim();
//            Kiem tra mat khau va nhap lai mk
                if (password.equals(rePass)){
                    mAuth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("RegisterTask", String.valueOf(task.getResult().getUser().getUid()));
                                        // Sign in success, update UI with the signed-in user's information
                                        Log.d("RegisterResult", "createUserWithEmail:success");
                                        FirebaseUser user = mAuth.getCurrentUser();
                                        //            -----Ghi log Register--------
                                        DataHelper dataHelper = new DataHelper();
                                        dataHelper.writeLogRegister(RegisterActivity.this,task.getResult().getUser().getUid(),task.getResult().getUser().getEmail());


//                                    Update Ten cho profile
                                        UserProfileChangeRequest userProfileChangeRequest = new UserProfileChangeRequest.Builder()
                                                .setDisplayName(edtFirstnameRegister.getText().toString()).build();

                                        user.updateProfile(userProfileChangeRequest).addOnSuccessListener(new OnSuccessListener<Void>() {
                                            @Override
                                            public void onSuccess(Void aVoid) { }
                                        });

                                        register(email,password);
                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Log.w("RegisterResult", "createUserWithEmail:failure", task.getException());
                                        Snackbar.make(viewRegister,"Đăng ký thất bại\n" + task.getException().getLocalizedMessage(), BaseTransientBottomBar.LENGTH_SHORT).show();
                                        register(null,null);
                                    }

                                    // ...
                                }
                            });
                }
                else {
                    Toast.makeText(RegisterActivity.this, "Mật khẩu không khớp, vui lòng nhập lại", Toast.LENGTH_SHORT).show();
                }

            }





        }
        if (view == btnGetLogin){
            finish();
        }
    }

    private void register(String email, String pass) {
        if (email != null && pass != null){
            Snackbar.make(viewRegister,"Đăng ký thành công", Snackbar.LENGTH_SHORT).show();

            Intent intent = new Intent();
            intent.putExtra("email", email);
            intent.putExtra("pass", pass);
            setResult(RESULT_OK, intent);
            finish();
        }
    }

    @Override
    public void finish() {
        Intent data = new Intent();
        data.putExtra("email", email);
        data.putExtra("password", password);
        // Activity finished ok, return the data
        setResult(RESULT_OK, data);
        super.finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            currentUser.reload();
        }
    }
}