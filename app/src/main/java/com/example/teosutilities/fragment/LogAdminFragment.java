package com.example.teosutilities.fragment;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RadioGroup;

import com.example.teosutilities.R;
import com.example.teosutilities.data.LogChangeInfoUserAdapter;
import com.example.teosutilities.data.LogNote;
import com.example.teosutilities.data.LogNoteAdapter;
import com.example.teosutilities.data.LogUser;
import com.example.teosutilities.data.LogUserAdapter;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;


public class LogAdminFragment extends Fragment {
    private RadioGroup radioGroup;
    private RecyclerView recyclerView;

    DatabaseReference mData;



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_log_admin, container, false);
        Mapping(view);
        return view;
    }

    private void Mapping(View view){
        radioGroup = view.findViewById(R.id.radioGroup);
        recyclerView = view.findViewById(R.id.recview_fragment_log_admin);

        mData = FirebaseDatabase.getInstance().getReference();

        //        Set layout de hien thi thong tin trong recycle view
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());

//        Đảo ngược chiều thêm item từ dưới lên
        linearLayoutManager.setReverseLayout(true);

//        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setOrientation(RecyclerView.VERTICAL);
//        rvUsers.setLayoutManager(new GridLayoutManager(getContext(), 1));
        recyclerView.setLayoutManager(linearLayoutManager);

        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup radioGroup, int i) {

                    if(R.id.rb_login == i) {

                        ArrayList<LogUser> listLoginUser = new ArrayList<LogUser>();

                        LogUserAdapter logLoginAdapter = new LogUserAdapter(listLoginUser, getContext()); // Tao adapter de gan du lieu

                        recyclerView.setAdapter(logLoginAdapter);

                        mData.child("LogUser").child("Login").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                LogUser logUser = snapshot.getValue(LogUser.class);
                                listLoginUser.add(logUser);
                                logLoginAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(listLoginUser.size() - 1);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if(R.id.rb_register == i) {

                        ArrayList<LogUser> listRegisterUser = new ArrayList<LogUser>();

                        LogUserAdapter logRegisterAdapter = new LogUserAdapter(listRegisterUser, getContext()); // Tao adapter de gan du lieu

                        recyclerView.setAdapter(logRegisterAdapter);

                        mData.child("LogUser").child("Register").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                LogUser logUser = snapshot.getValue(LogUser.class);
                                listRegisterUser.add(logUser);
                                logRegisterAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(listRegisterUser.size() - 1);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if(R.id.rb_add == i) {

                        ArrayList<LogNote> listAddNote = new ArrayList<LogNote>();

                        LogNoteAdapter logAddAdapter = new LogNoteAdapter(listAddNote, getContext()); // Tao adapter de gan du lieu

                        recyclerView.setAdapter(logAddAdapter);

                        mData.child("LogUser").child("AddNote").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                LogNote logNote = snapshot.getValue(LogNote.class);
                                listAddNote.add(logNote);
                                logAddAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(listAddNote.size() - 1);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if(R.id.rb_delete == i) {

                        ArrayList<LogNote> listAddNote = new ArrayList<LogNote>();

                        LogNoteAdapter logAddAdapter = new LogNoteAdapter(listAddNote, getContext()); // Tao adapter de gan du lieu

                        recyclerView.setAdapter(logAddAdapter);

                        mData.child("LogUser").child("DeleteNote").addChildEventListener(new ChildEventListener() {
                            @Override
                            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                                LogNote logNote = snapshot.getValue(LogNote.class);
                                listAddNote.add(logNote);
                                logAddAdapter.notifyDataSetChanged();
                                recyclerView.scrollToPosition(listAddNote.size() - 1);
                            }

                            @Override
                            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                            }

                            @Override
                            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {

                            }
                        });
                    }

                    if(R.id.rb_change_info == i) {

                    ArrayList<LogUser> listAddNote = new ArrayList<LogUser>();

                        LogChangeInfoUserAdapter logChangeInfoUserAdapter = new LogChangeInfoUserAdapter(listAddNote, getContext()); // Tao adapter de gan du lieu

                    recyclerView.setAdapter(logChangeInfoUserAdapter);

                    mData.child("LogUser").child("ChangeInfo").addChildEventListener(new ChildEventListener() {
                        @Override
                        public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {
                            LogUser logNote = snapshot.getValue(LogUser.class);
                            listAddNote.add(logNote);
                            logChangeInfoUserAdapter.notifyDataSetChanged();
                            recyclerView.scrollToPosition(listAddNote.size() - 1);
                        }

                        @Override
                        public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onChildRemoved(@NonNull DataSnapshot snapshot) {

                        }

                        @Override
                        public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }

            }
        });



    }



}