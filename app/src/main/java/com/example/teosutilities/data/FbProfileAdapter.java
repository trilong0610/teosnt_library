package com.example.teosutilities.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.example.teosutilities.activity.ChangeFbProfileActivity;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

import static com.example.teosutilities.R.drawable.*;

public class FbProfileAdapter extends RecyclerView.Adapter<FbProfileAdapter.UserItemViewHolder> {
    private ArrayList<FbProfile> fbProfiles;
    private Context context;
//    private static ItemClickListener mClickListener;

    public FbProfileAdapter(ArrayList<FbProfile> fbProfiles, Context c) {
        this.fbProfiles = fbProfiles;
        this.context = c;
    }

    @Override
    public int getItemCount() {
        return fbProfiles.size();
    }

    @Override
    public UserItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user, parent, false);
        parent.scrollTo(0,0);
        return new UserItemViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(UserItemViewHolder holder, int position) {
        FbProfile fbProfile = fbProfiles.get(position);
        Picasso.with(context)
                .load(fbProfile.imageUrl)
                .into(holder.ivAvatar);
//        Nếu không có dòng này, khi người dùng lật back ở vị trí x
//        Thì kéo xuống vị trí 6x sẽ bị tình trạng vẫn đang lật back
//        "Có lẽ" do RecycleView sẽ dùng lại item x để gán thông tin 6x vào
//        Nên khi load lại itemview nếu đang bị lật back thì lật lại về front
        if (holder.flipItemUser.getCurrentFlipState() == EasyFlipView.FlipState.BACK_SIDE){
            holder.flipItemUser.flipTheView();
        }
        holder.btn_change_profile.setVisibility(View.INVISIBLE);
        holder.tvIdProfile.setText("ID: " + fbProfile.id);
        holder.tvIdProfileFront.setText("ID: " + fbProfile.id);
        holder.tvAuthorProfile.setText(fbProfile.author);
        holder.tvUrlProfile.setText(fbProfile.link);
//        -------------set username + icon social--------------
        switch (DataHelper.getSocialFromUrl(fbProfile.link)){
//            ------fb------
            case 1:
                holder.tv_username_front_item_user.setText("@" + DataHelper.getUserNameFromUrl(1,fbProfile.link));
                holder.ivSocial.setImageDrawable(context.getResources().getDrawable(icons8_facebook_48));
                break;
            case 2:
                holder.tv_username_front_item_user.setText("@" + DataHelper.getUserNameFromUrl(2,fbProfile.link));
                holder.ivSocial.setImageDrawable(context.getResources().getDrawable(icons8_instagram_48));
                break;
            default:
                holder.tv_username_front_item_user.setText("@" + DataHelper.getUserNameFromUrl(3,fbProfile.link));
                holder.ivSocial.setImageDrawable(context.getResources().getDrawable(icons8_link_52));
                break;
        }

//      -------Chỉ cho người đăng chỉnh sửa-------------
        if (fbProfile.author.trim().equals(DataHelper.mAuth.getCurrentUser().getEmail()) ||
            DataHelper.mAuth.getCurrentUser().getUid().equals(DataHelper.uidAdmin)){
            holder.btn_change_profile.setVisibility(View.VISIBLE);
        }
        else
            holder.btn_change_profile.setVisibility(View.INVISIBLE);
//        Chỉ cho admin xóa
        if (DataHelper.mAuth.getCurrentUser().getUid().equals(DataHelper.uidAdmin))
            holder.btn_delete_profile.setVisibility(View.VISIBLE);
        else
            holder.btn_delete_profile.setVisibility(View.INVISIBLE);
//        Chỉ cho admin update id
        if (DataHelper.mAuth.getCurrentUser().getUid().equals(DataHelper.uidAdmin))
            holder.btn_update_id_profile.setVisibility(View.VISIBLE);
        else
            holder.btn_update_id_profile.setVisibility(View.INVISIBLE);

        holder.sliceToFb.setText("                Trượt để truy cập");
//        Truy cap Lien ket khi truot
        holder.sliceToFb.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(fbProfile.link));
                context.startActivity(i);
                holder.sliceToFb.resetSlider();
                holder.flipItemUser.flipTheView();
            }
        });

        holder.flipItemUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.flipItemUser.flipTheView();
            }
        });

        holder.btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, ChangeFbProfileActivity.class);
                intent.putExtra("keyNote", fbProfile.keyNote);
                context.startActivity(intent);
            }
        });

        holder.btn_delete_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataHelper.deleteFbProfile(context,fbProfile.keyNote);
                DataHelper.fbProfiles.remove(position);
                DataHelper.myData.child("TotalProfile").setValue(fbProfiles.size());
                DataHelper.updateIdFbProfile(context,position);

            }
        });

        holder.btn_update_id_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DataHelper.updateIdFbProfile(context,position);
            }
        });
//        Show menu

    }



    public static class UserItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SlideToActView sliceToFb;
        public EasyFlipView flipItemUser;

        private MaterialButton btn_change_profile;
        private MaterialButton btn_delete_profile;
        private MaterialButton btn_update_id_profile;

        private TextView tv_username_front_item_user;

        public MaterialTextView tvIdProfile;
        public MaterialTextView tvAuthorProfile;
        private MaterialTextView tvUrlProfile;
        private MaterialTextView tvIdProfileFront;

        FirebaseAuth mAuth;

        public ImageView ivAvatar;
        private ImageView ivSocial;

        //         Luu mau Items
        @SuppressLint("WrongViewCast")
        public UserItemViewHolder(View itemView) {
            super(itemView);
            sliceToFb = (SlideToActView) itemView.findViewById(R.id.stav_login_name);
//            tvUrl = (MaterialTextView) itemView.findViewById(R.id.tv_url);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);
            ivSocial = itemView.findViewById(R.id.iv_social);

            tv_username_front_item_user = itemView.findViewById(R.id.tv_username_front_item_user);

            tvIdProfile = (MaterialTextView) itemView.findViewById(R.id.textView_id_profile);
            tvIdProfileFront = (MaterialTextView) itemView.findViewById(R.id.textView_id_profile_front);
            tvAuthorProfile = (MaterialTextView) itemView.findViewById(R.id.textView_author_profile);

            btn_change_profile = (MaterialButton) itemView.findViewById(R.id.btn_change_profile);
            btn_delete_profile = (MaterialButton) itemView.findViewById(R.id.btn_delete_profile);
            btn_update_id_profile = itemView.findViewById(R.id.btn_update_id_profile);

            tvUrlProfile = itemView.findViewById(R.id.textView_url_profile);
//            menuView.setVisibility(View.INVISIBLE);
            flipItemUser = itemView.findViewById(R.id.flip_item_user);





            mAuth = FirebaseAuth.getInstance();

//            itemView.setOnClickListener(this);


        }

        @Override
        public void onClick(View view) {
            flipItemUser.flipTheView();
//            if (mClickListener != null) {
//                mClickListener.onItemClick(view, getAdapterPosition());

//            }
        }
    }

    public static Bitmap loadImageFromStorage(String path, long id) {

        try {
            File f=new File(path, id + ".jpg");
            //            ImageView img=(ImageView)findViewById(R.id.imgPicker);
//            img.setImageBitmap(b);
            return BitmapFactory.decodeStream(new FileInputStream(f));
        }
        catch (FileNotFoundException e)
        {
            e.printStackTrace();
        }
        return null;
    }

//    // allows clicks events to be caught
//    void setClickListener(ItemClickListener itemClickListener) {
//        this.mClickListener = itemClickListener;
//    }
//    // parent activity will implement this method to respond to click events
//    public interface ItemClickListener {
//        void onItemClick(View view, int position);
//    }





}
