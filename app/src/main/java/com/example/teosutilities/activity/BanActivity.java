package com.example.teosutilities.activity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.teosutilities.R;
import com.example.teosutilities.data.DataHelper;
import com.google.android.material.button.MaterialButton;

public class BanActivity extends AppCompatActivity {

    MaterialButton btn_exit_ban;

    ImageView contact_fb_ban;
    ImageView contact_gmail_ban;
    ImageView contact_telegram_ban;
    ImageView iv_logo_ban;

    int count = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_ban);

        btn_exit_ban = findViewById(R.id.btn_exit_ban);
        iv_logo_ban = findViewById(R.id.iv_logo_ban);

        contact_fb_ban = findViewById(R.id.contact_fb_ban);
        contact_gmail_ban = findViewById(R.id.contact_gmail_ban);
        contact_telegram_ban = findViewById(R.id.contact_telegram_ban);

        btn_exit_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataHelper.mAuth.signOut();
                finish();
            }
        });

        contact_fb_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/teooo.nt"));
                startActivity(i);
            }
        });

        contact_gmail_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                        "mailto", "trilong0610@gmail.com", null));
                emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
                emailIntent.putExtra(Intent.EXTRA_TEXT, "");
                startActivity(Intent.createChooser(emailIntent, "Send email..."));
            }
        });

        contact_telegram_ban.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/trilong0610"));
                startActivity(i);
            }
        });

    }
}