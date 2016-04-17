package com.example.harish.b2bapplication.model;

/**
 * Created by harish on 15/3/16.
 */
import android.util.Log;

import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.activity.MainActivity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class FindConnectionJSONParser {

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
                     profile.setNameoffrim(obj.getString("nameoffirm"));
                     profile.setEstyear(obj.getString("estyear"));
                     profile.setBillingaddress(obj.getString("billingaddress"));
                     profile.setPan(obj.getString("pan"));
                     profile.setTanvat(obj.getString("tanvat"));
                     profile.setBankAcc(obj.getString("bankacc"));

                 }
                 if(i == 1) {

                     profile.setThumbnailUrl(ip+obj.getString("imagepath"));
                 }
                 if(i == 2) {
                     profileemail = obj.getString("email");
                     profiletmobile = obj.getString("mobile");
                     profile.setEmail(profileemail);
                     profile.setMobile(profiletmobile);
                     profile.setRoll(obj.getString("roll"));
                     profile.setUserid(obj.getString("_id"));

                 }
                 Log.d("data------------>", profile.getEmail() + " " + profile.getMobile() + " " + profile.getThumbnailUrl());


             }
            return  profile;



        } catch (Exception e) {
            e.printStackTrace();
        }
        return profile;
    }
}