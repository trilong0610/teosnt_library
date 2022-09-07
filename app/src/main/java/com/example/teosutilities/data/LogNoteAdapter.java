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

public class LogNoteAdapter extends RecyclerView.Adapter<LogNoteAdapter.LogNoteViewHolder> {

    private ArrayList<LogNote> listLogs;
    private Context context;

    public LogNoteAdapter(ArrayList<LogNote> listLogs, Context context) {
        this.listLogs = listLogs;
        this.context = context;
    }

    @NonNull
    @Override
    public LogNoteViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.items_log_activity_user, parent, false);
        parent.scrollTo(0,0);
        return new LogNoteViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(LogNoteAdapter.LogNoteViewHolder holder, int position) {
        LogNote logNote = listLogs.get(position);
        holder.tv_log_acti_email_user.setText(logNote.emailUser);
        holder.tv_log_acti_id_note.setText(logNote.idNote);
        holder.tv_log_acti_time.setText(logNote.timeCreated);

    }

    @Override
    public int getItemCount() {
        return listLogs.size();
    }

    public static class LogNoteViewHolder extends RecyclerView.ViewHolder{
        TextView tv_log_acti_email_user;
        TextView tv_log_acti_id_note;
        TextView tv_log_acti_time;

        //         Luu mau Items
        @SuppressLint("WrongViewCast")
        public LogNoteViewHolder(View itemView) {
            super(itemView);
            tv_log_acti_email_user = itemView.findViewById(R.id.tv_log_acti_email_user);
            tv_log_acti_id_note = itemView.findViewById(R.id.tv_log_acti_id_note);
            tv_log_acti_time = itemView.findViewById(R.id.tv_log_acti_time);



        }
    }
}
