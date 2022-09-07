package com.example.teosutilities.data;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

public class FbProfile {
    public String id;
    public String link;
    public String imageUrl;
    public String author;
    public String keyNote;

    public FbProfile() {
    }

    public FbProfile(String id, String link, String imageUrl, String author, String keyNote) {
        this.id = id;
        this.link = link;
        this.imageUrl = imageUrl;
        this.author = author;
        this.keyNote = keyNote;
    }

    @Exclude
    public Map<String, Object> toMap() {
        HashMap<String, Object> result = new HashMap<>();
        result.put("id", id);
        result.put("link", link);
        result.put("imageUrl", imageUrl);
        result.put("author", author);
        result.put("keyNote", keyNote);
        return result;
    }
}
