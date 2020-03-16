package com.tetravalstartups.scout99.common.auth;

import com.google.firebase.firestore.IgnoreExtraProperties;
import com.google.firebase.firestore.ServerTimestamp;

import java.util.Date;
@IgnoreExtraProperties
public class User {
    @ServerTimestamp
    private Date user_created_at;
    private String user_id;
    private String user_full_name;
    private String user_email;
    private String user_phone_number;
    private String user_is;
    private String user_heard_from;
    private String user_status;

    public User() {
    }

    public User(Date user_created_at, String user_id, String user_full_name, String user_email, String user_phone_number, String user_is, String user_heard_from, String user_status) {
        this.user_created_at = user_created_at;
        this.user_id = user_id;
        this.user_full_name = user_full_name;
        this.user_email = user_email;
        this.user_phone_number = user_phone_number;
        this.user_is = user_is;
        this.user_heard_from = user_heard_from;
        this.user_status = user_status;
    }

    public Date getUser_created_at() {
        return user_created_at;
    }

    public void setUser_created_at(Date user_created_at) {
        this.user_created_at = user_created_at;
    }

    public String getUser_id() {
        return user_id;
    }

    public void setUser_id(String user_id) {
        this.user_id = user_id;
    }

    public String getUser_full_name() {
        return user_full_name;
    }

    public void setUser_full_name(String user_full_name) {
        this.user_full_name = user_full_name;
    }

    public String getUser_email() {
        return user_email;
    }

    public void setUser_email(String user_email) {
        this.user_email = user_email;
    }

    public String getUser_phone_number() {
        return user_phone_number;
    }

    public void setUser_phone_number(String user_phone_number) {
        this.user_phone_number = user_phone_number;
    }

    public String getUser_is() {
        return user_is;
    }

    public void setUser_is(String user_is) {
        this.user_is = user_is;
    }

    public String getUser_heard_from() {
        return user_heard_from;
    }

    public void setUser_heard_from(String user_heard_from) {
        this.user_heard_from = user_heard_from;
    }

    public String getUser_status() {
        return user_status;
    }

    public void setUser_status(String user_status) {
        this.user_status = user_status;
    }

}
