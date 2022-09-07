package com.example.teosutilities.data;

import java.time.LocalDateTime;

public class LogUser {
    public String idUser;
    public String emailUser;
    public String timeCreated;
    public String action;
    public String ipWf;
    public String ipMobile;

    public LogUser() {
    }

    public LogUser(String idUser, String emailUser, String timeCreated, String action, String ipWf, String ipMobile) {
        this.idUser = idUser;
        this.emailUser = emailUser;
        this.timeCreated = timeCreated;
        this.action = action;
        this.ipWf = ipWf;
        this.ipMobile = ipMobile;
    }
}
