package com.example.teosutilities.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class UserControl {
    public String email;
    public boolean active;
    public String idNote;

    public UserControl() {

    }

    public UserControl(String email, boolean active, String idNote) {
        this.email = email;
        this.active = active;
        this.idNote = idNote;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("email", email);
        result.put("active", active);
        result.put("idNote", idNote);
        return result;
    }
}
