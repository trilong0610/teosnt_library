package com.example.teosutilities.data;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.google.android.material.switchmaterial.SwitchMaterial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static com.example.teosutilities.activity.WelcomeActivity.listUserControl;

public class UserControlAdapter extends RecyclerView.Adapter<UserControlAdapter.UserControlViewHolder> {
    private ArrayList<UserControl> listUsers;
    private Context context;

    public UserControlAdapter(ArrayList<UserControl> listUsers, Context context) {
        this.listUsers = listUsers;
        this.context = context;
    }

    @NonNull
    @Override
    public UserControlViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.items_user_control, parent,false);
        parent.scrollTo(0,0);
        return new UserControlViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull UserControlViewHolder holder, int position) {
        UserControl userControl = listUsers.get(position);
        boolean active = userControl.active;
        holder.tvUserControlEmailUser.setText(userControl.email);
        holder.tvUserControlIdNote.setText(userControl.idNote);
        holder.swUserControlBan.setChecked(!userControl.active);

        holder.swUserControlBan.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b){
                    updateUserControl(userControl.email,false,userControl.idNote);
                }
                else {
                    updateUserControl(userControl.email,true,userControl.idNote);
                }
            }
        });



    }

    @Override
    public int getItemCount() {
        return listUsers.size();
    }

    public static class UserControlViewHolder extends RecyclerView.ViewHolder{

        TextView tvUserControlEmailUser;
        TextView tvUserControlIdNote;
        SwitchMaterial swUserControlBan;


        public UserControlViewHolder(@NonNull View itemView) {
            super(itemView);
            tvUserControlEmailUser = itemView.findViewById(R.id.tv_user_control_email_user);
            tvUserControlIdNote = itemView.findViewById(R.id.tv_user_control_id_note);
            swUserControlBan  = itemView.findViewById(R.id.sw_user_control_ban);
        }
    }

    public boolean isExistUser(String email){

        for (UserControl userControlDb : listUserControl) {
            if (userControlDb.email.equals(email)){
                return true;
            }
        }
        return false;
    }

    private void updateUserControl(String email, boolean active, String key) {
        // Create new post at /user-posts/$userid/$postid and at
        // /posts/$postid simultaneously
        UserControl user = new UserControl(email, active, key);
        Map<String, Object> postValues = user.toMap();

        Map<String, Object> childUpdates = new HashMap<>();
        childUpdates.put("/User/" + key, postValues);

        DataHelper.myData.updateChildren(childUpdates);
    }
}
