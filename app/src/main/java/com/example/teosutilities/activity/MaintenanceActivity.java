package com.example.teosutilities.activity;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;

import com.example.teosutilities.R;
import com.google.android.material.button.MaterialButton;

public class MaintenanceActivity extends AppCompatActivity {

    MaterialButton  btnExitMaintenance;
    ImageView iv_logo_maintenance;
    int count = 0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maintenance);
        btnExitMaintenance = findViewById(R.id.btn_exit_maintenance);
        iv_logo_maintenance = findViewById(R.id.iv_logo_maintenance);

        btnExitMaintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        iv_logo_maintenance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                count = count + 1;
                if (count >= 10){
                    Intent intent = new Intent(MaintenanceActivity.this, AdminActivity.class);
                    startActivity(intent);
                    finish();
                }

            }
        });
    }
}