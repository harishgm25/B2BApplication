package com.example.harish.b2bapplication.model;

/**
 * Created by harish on 30/3/16.
 */
import java.io.Serializable;
import java.util.ArrayList;

public class Profile implements Serializable {
    private String email,mobile, thumbnailUrl,billingaddress,nameoffrim,estyear,roll;
    private String userid,friendid,connectionstatus,website;


    public Profile() {
    }

    public Profile(String website,String connectionstatus,String userid,String email,String mobile,String roll, String thumbnailUrl, String billingaddress,String nameoffrim, String estyear){
        this.email=email;
        this.thumbnailUrl = thumbnailUrl;
        this.mobile=mobile;
        this.roll=roll;
        this.billingaddress=billingaddress;
        this.nameoffrim=nameoffrim;
        this.estyear= estyear;
        this.userid=userid;
        this.friendid=friendid;
        this.website=website;

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

    public String getBillingaddress() {
        return billingaddress;
    }
    public void setBillingaddress(String name) {
        this.billingaddress = name;
    }

    public String getEstyear() {
        return estyear;
    }
    public void setEstyear(String name) {
        this.estyear = name;
    }

    public String getNameoffrim() {
        return nameoffrim;
    }
    public void setNameoffrim(String name) {
        this.nameoffrim = name;
    }

    public void  setRoll(String name)
    {
        this.roll=name;
    }
    public String getRoll()
    {
        return roll;
    }

    public void setUserid(String userid){ this.userid = userid; }
    public String getUserid() { return  userid; }

    public void setFriendid(String friendidid){ this.friendid = friendidid; }
    public String getFriendid() { return  friendid; }

    public void setConnectionstatus(String status){this.connectionstatus=status;}
    public String getConnectionstatus(){return connectionstatus;}

    public void setWebsite(String website){this.website=website;}
    public String getWebsite(){return  website;}

    public void setStatus(String status){this.connectionstatus=status;}
    public String getStatus(){return  connectionstatus;}







}