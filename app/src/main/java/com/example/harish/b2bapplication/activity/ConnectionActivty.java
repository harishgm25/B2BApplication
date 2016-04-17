package com.example.harish.b2bapplication.activity;


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
public class ConnectionActivty extends ActionBarActivity {
    String ack;
    String  friendUserId ;
    String  userId ;
    Profile profile;
    NetworkImageView imageView;
    ImageLoader mImageLoader;
    TextView firmname;
    TextView estyear;
    TextView billingaadress;
    FloatingActionButton connectButton;


    public ConnectionActivty() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connection);

        imageView = (NetworkImageView) findViewById(R.id.fullprofileimage);
        firmname =(TextView) findViewById(R.id.firmname);
        estyear =(TextView) findViewById(R.id.estyear);
        billingaadress =(TextView) findViewById(R.id.billingaddress);
        connectButton = (FloatingActionButton) findViewById(R.id.fab);


        Toolbar toolbar = (Toolbar)findViewById(R.id.toolbar);
        toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        Bundle extras = getIntent().getExtras();
        if(extras!=null)
        {
            ack = extras.getString("ack");
            profile = (Profile)extras.getSerializable("profile");
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
            billingaadress.setText(profile.getBillingaddress());


        }


        connectButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                if (connectionDetector.isConnectingToInternet())
                {
                    friendConnectionRequest();
                }
                else
                {
                    connectionDetector.showConnectivityStatus();
                }




            }
        });

    }


    public  void  friendConnectionRequest()
    {
        connectButton.setEnabled(false);
        AsyncHttpClient client = new AsyncHttpClient();
        String[] ip = getApplication().getResources().getStringArray(R.array.ip_address);
        client.addHeader("Authorization", "Token token=\"" + ack + "\"");
        RequestParams param = new RequestParams();
        try {
            param.put("findconnection[user_id]", userId);
            param.put("findconnection[friend]",friendUserId);


        } catch (Exception e) {
            e.printStackTrace();
        }
        Toast.makeText(ConnectionActivty.this, "Request Connection Sending", Toast.LENGTH_LONG).show();
        client.post(ip[0] + "api/v1/findconnections/addfriendrequest", param, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                super.onStart();
                Toast.makeText(ConnectionActivty.this, "Request Connection Sending", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                super.onSuccess(statusCode, headers, response);
                Toast.makeText(ConnectionActivty.this, "Request Connection Send", Toast.LENGTH_LONG).show();
                ConnectionActivty.this.finish();


            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                super.onFailure(statusCode, headers, responseString, throwable);
                Toast.makeText(ConnectionActivty.this, "Request Connection Failed", Toast.LENGTH_LONG).show();
                connectButton.setEnabled(true);
                ConnectionActivty.this.finish();

            }


        });



    }
    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

