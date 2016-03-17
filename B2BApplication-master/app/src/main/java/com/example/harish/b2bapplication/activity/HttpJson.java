package com.example.harish.b2bapplication.activity;

/**
 * Created by harish on 5/3/16.
 */
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.json.JSONException;
import org.json.JSONObject;

public class HttpJson {



    public JSONObject createSignupUrl(String email, String mobile, String password, String passconfrm)
    {
        JSONObject jsonObject = new JSONObject();
        try {

            jsonObject.accumulate("email", email);
            jsonObject.accumulate("mobile", mobile);
            jsonObject.accumulate("password", password);
            jsonObject.accumulate("password_confirmation", passconfrm);
        }catch (JSONException e){}


        return  jsonObject;


    }


}
