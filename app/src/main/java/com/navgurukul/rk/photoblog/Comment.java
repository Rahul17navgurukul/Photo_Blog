package com.navgurukul.rk.photoblog;

import java.util.Date;

public class Comment extends BlogPostId {

    private String Massage, User_id;
    private Date timestamp;
    public String image_url;
    public String current_user;


    public Comment() {

    }

    public Comment(String massage, String userId, Date timestamp,String current_user) {
        Massage = massage;
        User_id = userId;
//        this.image_url = image_url;
//        this.current_user = current_user;

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

    public String getCurrent_user() {
        return current_user;
    }

//    public void setCurrent_user(String current_user) {
//        this.current_user = current_user;
//
//    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }

}