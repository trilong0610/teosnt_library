package com.example.teosutilities.activity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.example.teosutilities.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class ForgetPasswordActivity extends AppCompatActivity implements View.OnClickListener {

    private FirebaseAuth mAuth;

    private TextInputEditText edtEmailForget;

    private MaterialButton btnForget;
    private MaterialButton btnGetLoginForget;

    Animation bottomAnim;

    ArrayList<TextInputLayout> textInputLayout;

    View view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forget_password);
        mAuth = FirebaseAuth.getInstance();
        AnhXa();

        Intent intent = getIntent();
        String emailLogin = intent.getStringExtra("email");
        edtEmailForget.setText(emailLogin);

    }

    private void AnhXa(){
        view = findViewById(R.id.view_forget);
        edtEmailForget = findViewById(R.id.edt_email_forget);

        TextInputLayout textInputLayoutEmailForget = findViewById(R.id.textInputLayout_email_forget);


        ImageView ivLogoForget = findViewById(R.id.iv_logo_forget);
        MaterialTextView tvWelcomeForget = findViewById(R.id.tv_welcome_forget);

        btnForget = findViewById(R.id.btn_forget);
        btnGetLoginForget = findViewById(R.id.btn_get_login_forget);

        btnForget.setOnClickListener(this::onClick);
        btnGetLoginForget.setOnClickListener(this::onClick);

        bottomAnim = AnimationUtils.loadAnimation(this,R.anim.view_bottom_fast_animation);

        textInputLayout = new ArrayList<>();

        textInputLayout.add(textInputLayoutEmailForget);

        textInputLayoutEmailForget.setAnimation(bottomAnim);

        btnForget.setAnimation(bottomAnim);
        btnGetLoginForget.setAnimation(bottomAnim);




    }

    @Override
    public void onClick(View view) {
        if (view == btnForget){
//            Kiem tra email da duoc dien chua
            boolean noErrors = true;
            for (TextInputLayout textInputLayout : textInputLayout){
                if (textInputLayout.getEditText().getText().toString().isEmpty()){
                    textInputLayout.setError(getResources().getString(R.string.error_empty_text));
                    noErrors = false;
                }
            }
//          Email da dien -> Gui email reset
            if (noErrors){
                mAuth.sendPasswordResetEmail(edtEmailForget.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            Snackbar.make(view,"Đã gửi Email khôi phục mật khẩu", Snackbar.LENGTH_SHORT).show();
                        }
                        else {
                            Snackbar.make(view,"Gửi Email thất bại", Snackbar.LENGTH_SHORT).show();
                        }
                    }
                });
            }

        }

        if (view == btnGetLoginForget){
            finish();
        }

    }
}