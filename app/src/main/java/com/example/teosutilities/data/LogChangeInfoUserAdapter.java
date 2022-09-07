package com.example.teosutilities.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;

import java.util.ArrayList;

public class LogChangeInfoUserAdapter extends RecyclerView.Adapter<LogChangeInfoUserAdapter.LogChangeInfoUserViewHolder> {

    private ArrayList<LogUser> listLogs;
    private Context context;

    public LogChangeInfoUserAdapter(ArrayList<LogUser> listLogs, Context context) {
        this.listLogs = listLogs;
        this.context = context;
    }

    @NonNull
    @Override
    public LogChangeInfoUserAdapter.LogChangeInfoUserViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_log_change_info_user, parent, false);
        parent.scrollTo(0, 0);
        return new LogChangeInfoUserAdapter.LogChangeInfoUserViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogChangeInfoUserAdapter.LogChangeInfoUserViewHolder holder, int position) {
        LogUser logUser = listLogs.get(position);
        holder.tv_log_change_info_email_user.setText(logUser.emailUser);
        holder.tv_log_change_info_id_user.setText(logUser.idUser);
        holder.tv_log_change_info_time.setText(logUser.timeCreated);
        holder.tv_log_change_info_action.setText(logUser.action);

    }

    @Override
    public int getItemCount() {
        return listLogs.size();
    }

    public static class LogChangeInfoUserViewHolder extends RecyclerView.ViewHolder {
        TextView tv_log_change_info_email_user;
        TextView tv_log_change_info_id_user;
        TextView tv_log_change_info_time;
        TextView tv_log_change_info_action;

        //         Luu mau Items
        @SuppressLint("WrongViewCast")
        public LogChangeInfoUserViewHolder(View itemView) {
            super(itemView);
            tv_log_change_info_email_user = itemView.findViewById(R.id.tv_log_change_info_email_user);
            tv_log_change_info_id_user = itemView.findViewById(R.id.tv_log_change_info_id_user);
            tv_log_change_info_time = itemView.findViewById(R.id.tv_log_change_info_time);
            tv_log_change_info_action = itemView.findViewById(R.id.tv_log_change_info_action);


        }
    }
}
