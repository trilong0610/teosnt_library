package com.example.teosutilities.data;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.RecyclerView;

import com.example.teosutilities.R;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textview.MaterialTextView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.ncorti.slidetoact.SlideToActView;
import com.squareup.picasso.Picasso;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;

public class FbProfileForAdminAdapter extends RecyclerView.Adapter<FbProfileForAdminAdapter.UserItemViewHolder> {
    private ArrayList<FbProfile> fbProfiles;
    private Context context;
//    private static ItemClickListener mClickListener;

    public FbProfileForAdminAdapter(ArrayList<FbProfile> fbProfiles, Context c) {
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
        FbProfile profileFb = fbProfiles.get(position);
        Picasso.with(context)
                .load(profileFb.imageUrl)
                .into(holder.ivAvatar);
//        Nếu không có dòng này, khi người dùng lật back ở vị trí x
//        Thì kéo xuống vị trí 6x sẽ bị tình trạng vẫn đang lật back
//        "Có lẽ" do RecycleView sẽ dùng lại item x để gán thông tin 6x vào
//        Nên khi load lại itemview nếu đang bị lật back thì lật lại về front
        if (holder.flipItemUser.getCurrentFlipState() == EasyFlipView.FlipState.BACK_SIDE){
            holder.flipItemUser.flipTheView();
        }
        holder.btn_change_profile.setVisibility(View.INVISIBLE);
        holder.tvIdProfile.setText("ID: " + profileFb.id);
        holder.tvIdProfileFront.setText("ID: " + profileFb.id);
        holder.tvAuthorProfile.setText(profileFb.author);
        holder.tvUrlProfile.setText(profileFb.link);

        if (profileFb.author.trim().equals(DataHelper.mAuth.getCurrentUser().getEmail()) ||
                DataHelper.mAuth.getCurrentUser().getUid().equals("V3UqbUKGSyU8AkisHCKajb4IlDU2")){
            holder.btn_change_profile.setVisibility(View.VISIBLE);
        }
        else
            holder.btn_change_profile.setVisibility(View.INVISIBLE);


        holder.sliceToFb.setText("                Trượt để truy cập");
//        Truy cap facebook khi truot
        holder.sliceToFb.setOnSlideCompleteListener(new SlideToActView.OnSlideCompleteListener() {
            @Override
            public void onSlideComplete(SlideToActView slideToActView) {
                Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(profileFb.link));
                context.startActivity(i);
                holder.sliceToFb.resetSlider();
                holder.flipItemUser.flipTheView();
            }
        });

        holder.btn_change_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
                Query applesQuery = ref.child("ProfileFb").orderByChild("id").equalTo(profileFb.id);

                // AlertDialog Builder class
                AlertDialog.Builder builder
                        = new AlertDialog
                        .Builder(context);

                // Set the message show for the Alert time
                builder.setMessage("Bạn muốn xóa ?");

                // Set Cancelable false
                // for when the user clicks on the outside
                // the Dialog Box then it will remain show
                builder.setCancelable(false);

                // Set the positive button with yes name
                // OnClickListener method is use of
                // DialogInterface interface.
                builder
                        .setPositiveButton(
                                "Xóa",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {
                                        Log.i("deleteItem", String.valueOf(holder));
                                        deleteFbProfile(holder.getPosition(),profileFb.keyNote);
//                                        applesQuery.addListenerForSingleValueEvent(new ValueEventListener() {
//                                            @Override
//                                            public void onDataChange(DataSnapshot dataSnapshot) {
//                                                for (DataSnapshot appleSnapshot: dataSnapshot.getChildren()) {
////                                                    -----Ghi LogUser xoa-----
//                                                    DataHelper dataHelper = new DataHelper();
//                                                    FirebaseUser mUser = DataHelper.mAuth.getCurrentUser();
//                                                    ProfileFb profileDelete = appleSnapshot.getValue(ProfileFb.class);
//                                                    dataHelper.writeLogDeleteNote(context,mUser.getUid(), mUser.getEmail(),profileDelete.id, appleSnapshot.getKey() );
////                                                    ----Xoa profile----
//                                                    appleSnapshot.getRef().removeValue();
//                                                    WelcomeActivity.fbProfiles.remove(holder.getPosition());
//
//
//                                                }
//                                            }
//
//                                            @Override
//                                            public void onCancelled(DatabaseError databaseError) {
//                                                Log.e("deleteItem", "onCancelled", databaseError.toException());
//                                            }
//                                        });

                                    }
                                });
                // Set the Negative button with No name
                // OnClickListener method is use
                // of DialogInterface interface.
                builder
                        .setNegativeButton(
                                "Hủy",
                                new DialogInterface
                                        .OnClickListener() {

                                    @Override
                                    public void onClick(DialogInterface dialog,
                                                        int which)
                                    {

                                        // If user click no
                                        // then dialog box is canceled.
                                        dialog.cancel();
                                    }
                                });

                // Create the Alert dialog
                AlertDialog alertDialog = builder.create();

                // Show the Alert Dialog box
                alertDialog.show();
            }
        });

        holder.flipItemUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                holder.flipItemUser.flipTheView();
            }
        });

//        Show menu

    }


    public static class UserItemViewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {
        public SlideToActView sliceToFb;
        public EasyFlipView flipItemUser;

        private MaterialButton btn_change_profile;

        public MaterialTextView tvIdProfile;
        public MaterialTextView tvAuthorProfile;
        private MaterialTextView tvUrlProfile;
        private MaterialTextView tvIdProfileFront;

        FirebaseAuth mAuth;

        public ImageView ivAvatar;

        //         Luu mau Items
        @SuppressLint("WrongViewCast")
        public UserItemViewHolder(View itemView) {
            super(itemView);
            sliceToFb = (SlideToActView) itemView.findViewById(R.id.stav_login_name);
//            tvUrl = (MaterialTextView) itemView.findViewById(R.id.tv_url);
            ivAvatar = (ImageView) itemView.findViewById(R.id.iv_avatar);

            tvIdProfile = (MaterialTextView) itemView.findViewById(R.id.textView_id_profile);
            tvIdProfileFront = (MaterialTextView) itemView.findViewById(R.id.textView_id_profile_front);
            tvAuthorProfile = (MaterialTextView) itemView.findViewById(R.id.textView_author_profile);

            btn_change_profile = (MaterialButton) itemView.findViewById(R.id.btn_change_profile);

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

    private void deleteFbProfile(int index, String key){
        DataHelper.myData.child("ProfileFb").child(key).removeValue();
    }


}
