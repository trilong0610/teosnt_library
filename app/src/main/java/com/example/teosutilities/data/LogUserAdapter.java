package com.example.teosutilities.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.ncorti.slidetoact.SlideToActView;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.ArrayList;

import static com.example.teosutilities.activity.WelcomeActivity.listUserControl;

public class LogUserAdapter extends RecyclerView.Adapter<LogUserAdapter.LogUserViewHolder>{

    private ArrayList<LogUser> listLogs;
    private Context context;

    public LogUserAdapter(ArrayList<LogUser> listLogs, Context context) {
        this.listLogs = listLogs;
        this.context = context;
    }

    @NonNull
    @Override
    public LogUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_log_user, parent, false);
        parent.scrollTo(0,0);
        return new LogUserAdapter.LogUserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogUserViewHolder holder, int position) {
        LogUser logUser = listLogs.get(position);
        holder.tv_log_user_email_user.setText(logUser.emailUser);
        holder.tv_log_user_id_user.setText(logUser.idUser);
        holder.tv_log_user_time.setText(logUser.timeCreated);

    }

    @Override
    public int getItemCount() {
        return listLogs.size();
    }

    public static class LogUserViewHolder extends RecyclerView.ViewHolder{
        TextView tv_log_user_email_user;
        TextView tv_log_user_id_user;
        TextView tv_log_user_time;

        //         Luu mau Items
        @SuppressLint("WrongViewCast")
        public LogUserViewHolder(View itemView) {
            super(itemView);
            tv_log_user_email_user = itemView.findViewById(R.id.tv_log_user_email_user);
            tv_log_user_id_user = itemView.findViewById(R.id.tv_log_user_id_user);
            tv_log_user_time = itemView.findViewById(R.id.tv_log_user_time);



        }
    }


}
