package com.example.teosutilities.data;

public class LogNote {

    public String idUser;
    public String emailUser;
    public String timeCreated;
    public String action;
    public String keyNote;
    public String idNote;
    public String ipWf;
    public String ipMobile;

    public LogNote() {
    }

    public LogNote(String idUser, String emailUser, String timeCreated, String action, String idNote,String keyNote, String ipWf, String ipMobile ) {
        this.keyNote = keyNote;
        this.idNote = idNote;
        this.idUser = idUser;
        this.emailUser = emailUser;
        this.timeCreated = timeCreated;
        this.action = action;
        this.ipWf = ipWf;
        this.ipMobile = ipMobile;
    }
}
