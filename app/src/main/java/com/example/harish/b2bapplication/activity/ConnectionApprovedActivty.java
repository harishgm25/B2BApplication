package com.example.harish.b2bapplication.activity;


import android.graphics.Color;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.model.Profile;
import com.example.harish.b2bapplication.util.CustomVolleyRequestQueue;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;

import org.json.JSONObject;

import cz.msebera.android.httpclient.Header;


/**
 * Created by harish on 4/3/16.
 */

public class ConnectionApprovedActivty extends ActionBarActivity
    {
        String ack;
        String friendUserId;
        String userId;
        Profile profile;
        NetworkImageView imageView;
        ImageLoader mImageLoader;
        TextView firmname;
        TextView estyear;
        TextView billingaadress;
        TextView email;
        TextView website;
        TextView mobile;
        TextView bankAcc;
        TextView pan;
        TextView tanvat;
       // Button connectButton;
        FloatingActionButton connectButton;
        private ConnectionOthersRequestFragment connectionOthersRequestFragment;


        public ConnectionApprovedActivty() {
        // Required empty public constructor
    }

        @Override
        public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_connection_approved);

            imageView = (NetworkImageView) findViewById(R.id.fullprofileimage);
            firmname = (TextView) findViewById(R.id.firmname);
            estyear = (TextView) findViewById(R.id.estyear);
            mobile = (TextView) findViewById(R.id.mobile);
            website = (TextView) findViewById(R.id.website);
            email =(TextView) findViewById(R.id.email);
            pan = (TextView)findViewById(R.id.pan);
            tanvat =(TextView)findViewById(R.id.tanvat);
            bankAcc =(TextView)findViewById(R.id.bankacc);


            billingaadress = (TextView) findViewById(R.id.billingaddress);
            //connectButton = (Button) findViewById(R.id.connectButton);
            connectButton =(FloatingActionButton)findViewById(R.id.fab);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                ack = extras.getString("ack");
                profile = (Profile) extras.getSerializable("profile");
                userId = extras.getString("userid");
                friendUserId = profile.getFriendid();
                mImageLoader = CustomVolleyRequestQueue.getInstance(this.getApplicationContext())
                        .getImageLoader();
                final String url = profile.getThumbnailUrl();
                mImageLoader.get(url, ImageLoader.getImageListener(imageView,
                        R.mipmap.ic_launcher, android.R.drawable
                                .ic_dialog_alert));
                imageView.setImageUrl(url, mImageLoader);
                firmname.setText(profile.getNameoffrim());
                estyear.setText(profile.getEstyear());


                if(profile.getConnectionstatus().equals("approved")) {
                    billingaadress.setText(profile.getBillingaddress());
                    email.setText(profile.getEmail());
                    mobile.setText(profile.getMobile());
                    website.setText(profile.getWebsite());
                    pan.setText(profile.getPan());
                    bankAcc.setText(profile.getBankAcc());
                    tanvat.setText(profile.getTanvat());
                    connectButton.setBackgroundColor(Color.RED);
                    connectButton.setImageResource(R.drawable.ic_action_remove);
                    connectButton.refreshDrawableState();
                    connectButton.setEnabled(true);
                }
                else
                {
                    email.setEnabled(false);
                    mobile.setEnabled(false);
                    website.setEnabled(false);
                    connectButton.setEnabled(false);
                    connectButton.setVisibility(View.INVISIBLE);
                }

            }


            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                    if (connectionDetector.isConnectingToInternet()) {
                        removefriendConnectionRequest();
                    } else {
                        connectionDetector.showConnectivityStatus();
                    }
                }
            });

         }


    public void removefriendConnectionRequest() {
        connectButton.setEnabled(false);
        AsyncHttpClient client = new AsyncHttpClient();
        String[] ip = getApplication().getResources().getStringArray(R.array.ip_address);
        client.addHeader("Authorization", "Token token=\"" + ack + "\"");
        RequestParams param = new RequestParams();
        try {
            param.put("findconnection[user_id]", userId);
            param.put("findconnection[friend]", friendUserId);
        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(ConnectionApprovedActivty.this, "Request Connection Sending", Toast.LENGTH_LONG).show();
        client.post(ip[0] + "api/v1/findconnections/removeotherfriendrequeststatus", param, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Toast.makeText(ConnectionApprovedActivty.this, "Request Connection Sending", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(ConnectionApprovedActivty.this, "Request Connection Send", Toast.LENGTH_SHORT).show();
                ConnectionApprovedActivty.this.finish();
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(ConnectionApprovedActivty.this, "Request Connection Failed", Toast.LENGTH_SHORT).show();
                connectButton.setEnabled(true);
                ConnectionApprovedActivty.this.finish();
            }
        });


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }




}

