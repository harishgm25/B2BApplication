package com.example.harish.b2bapplication.model;

/**
 * Created by harish on 30/3/16.
 */
import java.util.ArrayList;

public class Profile {
    private String email,mobile, thumbnailUrl;


    public Profile() {
    }

    public Profile(String email,String mobile, String thumbnailUrl){
        this.email=email;
        this.thumbnailUrl = thumbnailUrl;
        this.mobile=mobile;
            }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String name) {
        this.mobile = name;
    }


    public String getEmail() {
        return email;
    }

    public void setEmail(String name) {
        this.email = name;
    }

    public String getThumbnailUrl() {
        return thumbnailUrl;
    }

    public void setThumbnailUrl(String thumbnailUrl) {
        this.thumbnailUrl = thumbnailUrl;
    }


}