package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.AlarmManager;
import android.app.Dialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.ref.WeakReference;
import java.net.URL;

import cz.msebera.android.httpclient.Header;


/**
 * Created by harish on 4/3/16.
 */


public class SigninFragment extends Fragment   {

    private EditText _email = null;
    private EditText _password = null;
    private Button _signin = null;
    private TextView _signup = null;
    private String email;
    private ImageView _profileImg;
    private String password;
    private String imgUrl;
    private JSONObject temp1;
    private String[] ip;
    private  Bitmap bmp;

    private FileOutputStream fos;
    private String s[];
    private AlarmManager am = null;
    private PendingIntent pi =null;




    public SigninFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_signin, container, false);
        _email = (EditText) rootView.findViewById(R.id.input_email);
        _profileImg = (ImageView) rootView.findViewById(R.id.circleView);
        _password = (EditText) rootView.findViewById(R.id.input_password);
        _signin = (Button) rootView.findViewById(R.id.btn_signin);
        _signup = (TextView) rootView.findViewById(R.id.link_signup);


        _signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                email = _email.getText().toString();
                //moblie = _mobile.getText().toString();
                password = _password.getText().toString();
                ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
                if (connectionDetector.isConnectingToInternet()) {

                    signin();
                } else {
                    connectionDetector.showConnectivityStatus();
                }

            }
        });

        _signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, new OTPFragment());
                fragmentTransaction.commit();

            }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {

        super.onDetach();
    }


    public void signin() {
        Log.d("TAG", "Signup");


        if (!validate()) {
            onSigninFailed("validation Failed");
            return;
        }

 // TODO: Implement your own signup logic here.


        final ProgressDialog progressdialog = ProgressDialog.show(getContext(), "Please wait","Logging In" ,true);
        progressdialog.setCancelable(false);
        progressdialog.show();
        timerDelayRemoveDialog(5000,progressdialog);
        new android.os.Handler().postDelayed(
                 new Runnable() {
                    public void run() {

                        try {
                            JSONObject holder = new JSONObject();
                            JSONObject userObj = new JSONObject();
                            try {
                                holder.put("email", " ");
                                holder.put("mobile", email); // mobile or email
                                holder.put("password", password);
                                userObj.put("user", holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Http Post for sign_in and receving token and wirting in internal storage
                            ip = getActivity().getResources().getStringArray(R.array.ip_address);
                            HttpPost httpPost = new HttpPost(ip[0] + "users/sign_in");
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            HttpResponse response = new DefaultHttpClient().execute(httpPost);
                            Log.d("Http Post Response:", response.toString());

                            String json = EntityUtils.toString(response.getEntity());
                            temp1 = new JSONObject(json);
                            Log.d("Response status >>>>>>>", temp1.toString());

                            if (temp1.has("success")) {
                                if (temp1.getString("success").equals("true")) {
                                    Context c = getActivity().getApplicationContext();
                                    // Getting email and roll of the user and written in the file
                                    new StoreAck().writeFile(c, temp1);
                                    onSigninSuccess();
                                    progressdialog.dismiss();


                                }
                                else
                                {
                                    onSigninFailed(temp1.getString("error"));
                                    progressdialog.dismiss();
                                }
                            } else {

                                onSigninFailed(temp1.getString("error"));
                                progressdialog.dismiss();
                            }


                        } catch (IOException e) {
                            e.printStackTrace();

                            onSigninFailed("Check Connectivity Try Later");
                        } catch (JSONException e) {
                            e.printStackTrace();

                            onSigninFailed("Check Connectivity Try Later");
                        } catch (Exception e) {
                            e.printStackTrace();

                            onSigninFailed("Check Connectivity Try Later");
                        }
                    }

                 },100);


    }
    public void onSigninSuccess() {
       // _signin.setEnabled(true);
        Log.d("----------", "SigninSuccess");
        callAsyncProfileImageTask();



        //Closing KeyBoard Manually----------------------------------------------
        View v = getActivity().getCurrentFocus();
        if (v != null) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        }

        setRecurringAlarm();
        //Setting the Fragment Based on The User---------------------------------
        Fragment userFragment = null;
        String s[] = new StoreAck().readFile(getContext().getApplicationContext());
        if (s[3].equals("Manufacture"))
            userFragment = new ManufactureFragment();
        if (s[3].equals("Wholesaler"))
            userFragment = new WholeSalerFragment();
        if (s[3].equals("Retailer"))
            userFragment = new RetailerFragment();

        Bundle arg = new Bundle();
        arg.putSerializable("usertokens",s);   // sending user tokens to next fragment or activity
        userFragment.setArguments(arg);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, userFragment);
        fragmentTransaction.commit();
        return;
    }

    public void onSigninFailed(String msg) {

        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();
        _signin.setEnabled(true);
    }



    // Validating signin fields
    public boolean validate() {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
             if(email.length() < 10) {
                 _email.setError("enter a valid email address or Phone ");
                 valid = false;
             }
             else
                 _email.setError(null);
        } else {
            _email.setError(null);
        }
        if (password.isEmpty() || password.length() < 8 || password.length() > 10) {
            _password.setError("between 8 and 10 alphanumeric characters");
            valid = false;
        } else {
            _password.setError(null);
        }
        return valid;
    }


    //----------------Closing the ProgressDialog after given interval-------------------
    public void timerDelayRemoveDialog(long time, final Dialog d){

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing()) {
                    d.dismiss();
                    Toast.makeText(getContext(), "Check Connectivity", Toast.LENGTH_LONG).show();
                }
            }
        }, time);
    }



    public void callAsyncProfileImageTask()
    {
       // Loading Image form Server using Nested AsyncTask--------------------------------------------------------

        new GetProfileImage(getContext().getApplicationContext()).execute();
    }



    private class GetProfileImage extends AsyncTask<Void,Void,Void>
    {

        private  Context context;
        String imgUrl;
        String ip [];


        GetProfileImage(Context c)
        {
            context=c;
           ip = getActivity().getResources().getStringArray(R.array.ip_address);

        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            if (bmp != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                //converting orginal image to thumb
                Bitmap ThumbImage = ThumbnailUtils.extractThumbnail(bmp,100,100);
                ThumbImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
                new StoreAck().writeProfile(context, bytes);

            }



        }

        @Override
        protected Void doInBackground(Void... params) {
            try {

                // Getting the profile Image of the user
                String ack = temp1.getString("token");
                String userid = temp1.getString("userid");
                RequestParams param = new RequestParams();
                try {
                    param.put("profile[id]", userid);

                } catch (Exception e) {
                    e.printStackTrace();
                }
                SyncHttpClient client = new SyncHttpClient();

                client.addHeader("Authorization", "Token token=\"" + ack + "\"");
                client.post(ip[0] + "api/v1/profiles/getprofile", param, new JsonHttpResponseHandler() {


                    @Override
                    public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                        super.onSuccess(statusCode, headers, response);

                        if (response.has("profileImg")) {
                            try {
                                imgUrl = response.getString("profileImg");
                                InputStream in = new URL(ip[0]+imgUrl).openStream();
                                bmp = BitmapFactory.decodeStream(in);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }

                        }


                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                        super.onFailure(statusCode, headers, responseString, throwable);
                    }


                });




            } catch (Exception e) {
                e.printStackTrace();
            }

            return null;
        }
    }

    public void setRecurringAlarm() {

        //---------------------getting others request for approval--------------------------
        s = new StoreAck().readFile(getContext().getApplicationContext());

        if(s!= null) {

            am = (AlarmManager) getActivity().getSystemService(Activity.ALARM_SERVICE);
            Intent i = new Intent(getActivity(), NotificationAlaramService.class);
            i.putExtra("ack", s[0]);
            i.putExtra("userid", s[1]);
            pi = PendingIntent.getBroadcast(getActivity(), 0, i, PendingIntent.FLAG_UPDATE_CURRENT);
            am.cancel(pi);
            am.setRepeating(AlarmManager.ELAPSED_REALTIME, SystemClock.elapsedRealtime(),
                    1 * 60 * 60, pi);

        }

    }




}



