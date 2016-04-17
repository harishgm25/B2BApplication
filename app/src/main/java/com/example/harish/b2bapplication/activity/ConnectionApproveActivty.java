package com.example.harish.b2bapplication.activity;



import android.app.Activity;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.ActionBarActivity;
import android.support.v7.widget.Toolbar;
import android.support.v4.app.FragmentManager;

import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.ConnectionRequestListAdapter;
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
public class ConnectionApproveActivty extends ActionBarActivity
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


        public ConnectionApproveActivty() {
        // Required empty public constructor
    }

        @Override
        public void onCreate (Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_connection_approve);

            imageView = (NetworkImageView) findViewById(R.id.fullprofileimage);
            firmname = (TextView) findViewById(R.id.firmname);
            estyear = (TextView) findViewById(R.id.estyear);
            mobile = (TextView) findViewById(R.id.mobile);
            website = (TextView) findViewById(R.id.website);
            email =(TextView) findViewById(R.id.email);
            billingaadress = (TextView) findViewById(R.id.billingaddress);
            connectButton = (FloatingActionButton) findViewById(R.id.fab);
            pan = (TextView)findViewById(R.id.pan);
            tanvat =(TextView)findViewById(R.id.tanvat);
            bankAcc =(TextView)findViewById(R.id.bankacc);

            Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
            toolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(toolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            Bundle extras = getIntent().getExtras();
            if (extras != null) {
                ack = extras.getString("ack");
                profile = (Profile) extras.getSerializable("profile");
                userId = extras.getString("userid");
                friendUserId = profile.getUserid();
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

                    connectButton.setEnabled(false);
                    //connectButton.setImageResource(R.drawable.ic_action_remove);
                   // connectButton.refreshDrawableState();

                }
                else
                {
                    email.setEnabled(false);
                    mobile.setEnabled(false);
                    website.setEnabled(false);
                }

            }


            connectButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                    if (connectionDetector.isConnectingToInternet()) {
                        friendConnectionRequest();
                    } else {
                        connectionDetector.showConnectivityStatus();
                    }


                }
            });

         }


    public void friendConnectionRequest() {
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
        Toast.makeText(ConnectionApproveActivty.this, "Request Connection Sending", Toast.LENGTH_LONG).show();
        client.post(ip[0] + "api/v1/findconnections/approveotherfriendrequeststatus", param, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Toast.makeText(ConnectionApproveActivty.this, "Request Connection Sending", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(ConnectionApproveActivty.this, "Request Connection Send", Toast.LENGTH_SHORT).show();
                android.support.v4.app.Fragment fragment = getSupportFragmentManager().findFragmentById(R.id.fragment_connection_others_request);
                //con(ConnectionOthersRequestFragment).getActivityCallBack();
                ConnectionApproveActivty.this.finish();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(ConnectionApproveActivty.this, "Request Connection Failed", Toast.LENGTH_SHORT).show();
                connectButton.setEnabled(true);
                ConnectionApproveActivty.this.finish();

            }


        });


    }

    @Override
    protected void onDestroy() {

        super.onDestroy();
    }




}

