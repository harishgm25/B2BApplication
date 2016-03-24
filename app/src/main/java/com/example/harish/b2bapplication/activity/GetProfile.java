package com.example.harish.b2bapplication.activity;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.StrictMode;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

/**
 * Created by harish on 14/3/16.
 */
public class GetProfile {

    private ProgressDialog progressDialog;
    private Context context;
    private String ack;
    private JSONObject profile;
    ProfileFragment profileFragment;


    public JSONObject getProfile(String a, final String userid, final Context c , ProfileFragment p)
    {
        context = c;
        ack =a;
        profileFragment =p;
        progressDialog = new ProgressDialog(context);
        progressDialog.setIndeterminate(false);
        progressDialog.setMessage("Getting Profile");
        progressDialog.show();
        profile =getProfileData(userid);
        return  profile;

    }



    public JSONObject getProfileData(final String userid)
    {


        new android.os.Handler().postDelayed(
                new Runnable() {


                    public void run() {
                        try {
                            JSONObject holder = new JSONObject();
                            JSONObject userObj = new JSONObject();
                            try {

                                holder.put("id",userid);
                                userObj.put("profile", holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Http Post for sign_up and receving token and wirting in internal storage
                            String[] ip = context.getResources().getStringArray(R.array.ip_address);
                            HttpPost httpPost = new HttpPost(ip[0] + "api/v1/profiles/getprofile");
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.addHeader("Authorization", "Token token=\"" + ack + "\"");
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            HttpResponse response = new DefaultHttpClient().execute(httpPost);
                            Log.d("Http Post Response:", response.toString());
                            String json = EntityUtils.toString(response.getEntity());
                            profile = new JSONObject(json);
                            Log.d("Response status >>>>>>>", profile.toString());
                            if (profile.has("success")) {
                                if (profile.getString("success").equals("true")) {
                                    onSuccess();
                                    progressDialog.dismiss();
                                }
                            } else {
                                progressDialog.dismiss();
                                onFailed("Failed Getting Updates");
                            }
                            progressDialog.dismiss();
                        } catch (IOException e) {
                            e.printStackTrace();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },3000);

        return profile;

    }

    public void onSuccess()
    {

        try {
            profileFragment._lastname.setText(profile.getJSONObject("profile").getString("lastname"));
            profileFragment._firstname.setText(profile.getJSONObject("profile").getString("firstname"));
            profileFragment._nameoffirm.setText(profile.getJSONObject("profile").getString("nameoffirm"));
            profileFragment._estyear.setText(profile.getJSONObject("profile").getString("estyear"));
            profileFragment._tanvat.setText(profile.getJSONObject("profile").getString("tanvat"));
            profileFragment._website.setText(profile.getJSONObject("profile").getString("website"));
            profileFragment._pan.setText(profile.getJSONObject("profile").getString("pan"));
            profileFragment._bankacc.setText(profile.getJSONObject("profile").getString("bankacc"));
            profileFragment._billingaddress.setText(profile.getJSONObject("profile").getString("billingaddress"));
            profileFragment._deliveryaddress.setText(profile.getJSONObject("profile").getString("deliveryaddress"));

        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void onFailed(String msg)
    {
        profile = null;
        Toast.makeText(context, msg, Toast.LENGTH_LONG).show();

    }




}
