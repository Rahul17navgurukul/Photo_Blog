package com.example.rk.photoblog;

import java.util.Date;

public class Comment extends BlogPostId {

    private String Massage, User_id;
    private Date timestamp;

    public Comment() {

    }

    public Comment(String massage, String userId, Date timestamp) {
        Massage = massage;
        User_id = userId;
        this.timestamp = timestamp;
    }

    public String getMassage() {

        return Massage;
    }

    public void setMassage(String massage) {
        Massage = massage;
    }

    public String getUserId() {
        return User_id;
    }

    public void setUserId(String userId) {
        User_id = userId;
    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}