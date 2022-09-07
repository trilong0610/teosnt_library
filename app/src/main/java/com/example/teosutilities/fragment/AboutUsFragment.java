package com.example.teosutilities.fragment;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.example.teosutilities.BuildConfig;
import com.example.teosutilities.R;
import com.example.teosutilities.activity.AdminActivity;
import com.example.teosutilities.data.DataHelper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

public class AboutUsFragment extends Fragment implements View.OnClickListener {

    ImageView contactFb;
    ImageView contactGmail;
    ImageView contactTelegram;
    ImageView avt_tac_gia;
    TextView tvContentVersion;
    TextView tv_about_us_version;

    int count = 0;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_about_us, container, false);
        contactFb = view.findViewById(R.id.contact_fb);
        contactGmail = view.findViewById(R.id.contact_gmail);
        contactTelegram = view.findViewById(R.id.contact_telegram);
        avt_tac_gia = view.findViewById(R.id.avt_tac_gia);
        tvContentVersion = view.findViewById(R.id.tv_about_us_content_version);
        tv_about_us_version = view.findViewById(R.id.tv_about_us_version);
        tv_about_us_version.setText("Tèo's Ultilities " + BuildConfig.VERSION_NAME);

        contactFb.setOnClickListener(this::onClick);
        contactGmail.setOnClickListener(this::onClick);
        contactTelegram.setOnClickListener(this::onClick);
        avt_tac_gia.setOnClickListener(this::onClick);
        setContentVersion();


        return view;
    }

    @Override
    public void onClick(View view) {
        if (view == contactFb){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://www.facebook.com/teooo.nt"));
            startActivity(i);
        }
        if (view == contactGmail){
            Intent emailIntent = new Intent(Intent.ACTION_SENDTO, Uri.fromParts(
                    "mailto", "trilong0610@gmail.com", null));
            emailIntent.putExtra(Intent.EXTRA_SUBJECT, "");
            emailIntent.putExtra(Intent.EXTRA_TEXT, "");
            startActivity(Intent.createChooser(emailIntent, "Send email..."));

        }
        if (view == contactTelegram){
            Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse("https://t.me/trilong0610"));
            startActivity(i);
        }
        if (view == avt_tac_gia){
            count = count + 1;
            if (count >= 10) {
                Intent intent = new Intent(getContext(), AdminActivity.class);
                startActivity(intent);
                getActivity().finish();
            }
        }
    }

    public void setContentVersion(){
        DataHelper.myData.child("Update").child("content").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                tvContentVersion.setText(snapshot.getValue(String.class));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}