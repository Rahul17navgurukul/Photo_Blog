package com.example.rk.photoblog;

import java.util.Date;


public class BlogPost extends BlogPostId {

    public String description;
    public String image_url;
    public String thumbs_url;
    public String current_user;

    public BlogPost() {

    }

    public Date timestamp;



    public BlogPost(String description, String image_url, String thumbs_url, String current_user, Date timestamp) {
        this.description = description;
        this.image_url = image_url;
        this.thumbs_url = thumbs_url;
        this.current_user = current_user;
        this.timestamp = timestamp;

    }



    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getThumbs_url() {
        return thumbs_url;
    }

    public void setThumbs_url(String thumbs_url) {
        this.thumbs_url = thumbs_url;
    }

    public String getCurrent_user() {
        return current_user;
    }

    public void setCurrent_user(String current_user) {
        this.current_user = current_user;

    }

    public Date getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(Date timestamp) {
        this.timestamp = timestamp;
    }




}
