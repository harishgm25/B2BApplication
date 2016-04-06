package com.example.harish.b2bapplication.model;

/**
 * Created by harish on 15/3/16.
 */

import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class FindConnectionStatusJSONParser {

    String ip ;
    public List<Profile> parse(JSONArray jsonArray,String ip){
        this.ip =ip;
        JSONArray jProfile = null;
            /** Retrieves all the elements in the 'product' array */
            jProfile = jsonArray;
        return getProfiles(jProfile);
    }

    private List<Profile> getProfiles(JSONArray jProfile){
        int profileCount = jProfile.length();
        List <Profile> profileList = new ArrayList<Profile>();
        //List<HashMap<String, String>> profileList = new ArrayList<HashMap<String,String>>();
        HashMap<String, String> product = null;

        for(int i=0; i<profileCount;i++){
            try {
                Profile profile;
                //HashMap<String, String> profile = null;
                Log.d("data++++++++++++++++++>", jProfile.get(i).toString());
                profile = getProfile((JSONArray)jProfile.get(i));
                profileList.add(profile);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }

        return profileList;
    }
    /** Parsing the Product JSON object */
    private Profile getProfile(JSONArray completeprofile){


        Profile profile = new Profile();
         String profileemail = "";
         String profiletmobile = "";


        try {
             for(int i = 0;i<completeprofile.length();i++)
             {

                 JSONObject obj = (JSONObject) completeprofile.get(i);
                 if(i==0)
                 {
                     profile.setFriendid(obj.getString("friend"));
                    profile.setUserid(obj.getString("user_id"));
                    profile.setConnectionstatus(obj.getString("status"));
                 }
                 if(i==1)
                 {
                     profile.setNameoffrim(obj.getString("nameoffirm"));
                     profile.setEstyear(obj.getString("estyear"));
                     profile.setBillingaddress(obj.getString("billingaddress"));
                     profile.setWebsite(obj.getString("website"));
                 }
                 if(i == 2) {

                     profile.setThumbnailUrl(ip + obj.getString("imagepath"));
                 }
                 if(i == 3) {

                     profile.setRoll(obj.getString("roll"));
                     profile.setEmail(obj.getString("email"));
                     profile.setMobile(obj.getString("mobile"));
                 }

             }
            return  profile;



        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }
}