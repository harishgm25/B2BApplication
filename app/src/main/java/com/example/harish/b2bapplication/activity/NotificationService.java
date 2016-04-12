package com.example.harish.b2bapplication.activity;
import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.NotificationCompat;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.ConnectionRequestListAdapter;
import com.example.harish.b2bapplication.model.FindConnectionStatusJSONParser;
import com.example.harish.b2bapplication.model.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class NotificationService extends IntentService {

    private String usertokens [];
    private String ack;
    private String userid;
    private List <Profile>profileList;
    private int count;

    public NotificationService() {
        super("MyServiceName");
    }
    @Override
    protected void onHandleIntent(Intent intent) {
        Log.d("MyService", "-------------------------------About to execute MyTask");
        Bundle extras = intent.getExtras();
        ack = extras.getString("ack");
        userid =extras.getString("userid");


        String[] ip = getApplicationContext().getResources().getStringArray(R.array.ip_address);
        JSONObject holderData = new JSONObject();
        JSONObject childData = new JSONObject();
        try {
            childData.put("user_id",userid);
            holderData.put("findconnection", childData);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,ip[0]+"api/v1/findconnections/getotherfriendrequeststatus",holderData,
                new Response.Listener<JSONObject>()
                {

                    String[] ip = getApplicationContext().getResources().getStringArray(R.array.ip_address);

                    @Override
                    public void onResponse(JSONObject response) {
                        System.out.println(response);
                        JSONObject jsnobject = null;
                        try {
                            jsnobject = response;
                            JSONArray profileArray = jsnobject.getJSONArray("otherfriendrequest");
                            FindConnectionStatusJSONParser profileJSONParser = new FindConnectionStatusJSONParser();
                            profileList = profileJSONParser.parse(profileArray,ip[0]);
                            Log.d("---------------Profile", profileList.toString());

                            sendNotification();

                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }


                },
                new Response.ErrorListener()
                {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        System.out.println(error);
                    }
                })
        {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                Map<String, String>  params = new HashMap<String, String>();
                params.put("findconnection[user_id]",userid);

                return super.getParams();
            }
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> params = new HashMap<String, String>();
                params.put("Authorization", "Token token=\"" + ack + "\"");
                params.put("Accept", "application/json");
                params.put("Content-type", "application/json");
                return params;
            }
        };

        RequestQueue requestQueue = Volley.newRequestQueue(getApplicationContext());
        requestQueue.add(postRequest);







    }



    private void sendNotification() {


     for (Profile p : profileList) {

           if(p.getConnectionstatus().equals("waiting"))
           {

               Intent i = new Intent(this, ConnectionApproveActivty.class);
               i.putExtra("ack",ack);
               i.putExtra("userid",userid);
               i.putExtra("profile",p);

               PendingIntent pi = PendingIntent.getActivity(this,count,i,PendingIntent.FLAG_UPDATE_CURRENT);
               Resources r = getResources();
               Notification notification = new NotificationCompat.Builder(this)
                       .setTicker(p.getNameoffrim())
                       .setSmallIcon(android.R.drawable.ic_menu_report_image)
                       .setContentTitle(p.getNameoffrim())
                       .setContentText(p.getRoll())
                       .setContentIntent(pi)
                       .setAutoCancel(true)
                       .build();


               NotificationManager notificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
               notificationManager.notify(count, notification);
               count++;
           }
      }
        count = 0;

    }

}

