package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;
import com.msg91.sendotp.library.Config;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;

import cz.msebera.android.httpclient.Header;


/**
 * Created by harish on 4/3/16.
 */
public class SettingFragment extends Fragment implements VerificationListener{

    private TextView _updatemobile;
    private TextView _updateemail;
    private String s[];
    private ProgressDialog progressDialog;
    private Verification mVerification;
    private SettingFragment settingFragment;
    private String newMobile;
    private boolean newMobileVerification = true;

    public SettingFragment() {
        // Required empty public constructor
        s = new String [6];

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);
        _updateemail = (TextView)rootView.findViewById(R.id.input_updateemail);
        _updatemobile = (TextView)rootView.findViewById(R.id.input_updatemobile);
        _updatemobile.setFocusable(false);
        _updateemail.setFocusable(false);
        settingFragment = this;

        s = new StoreAck().readFile(getContext());
        if(s != null)
        {
            _updateemail.setText(s[2]);
            _updatemobile.setText(s[4]);
            _updatemobile.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    newMobileVerification = true;
                    ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
                    if (connectionDetector.isConnectingToInternet()) {
                        Config config = SendOtpVerification.config().context(getContext().getApplicationContext())
                                .build();
                        mVerification = SendOtpVerification.createSmsVerification(config, _updatemobile.getText().toString(),settingFragment, "91", "");
                        mVerification.initiate();
                        progressDialog = new ProgressDialog(getContext());
                        progressDialog.setCancelable(false);
                        progressDialog.setIndeterminate(false);
                        progressDialog.setMessage("Sending OTP to Current Mobile");
                        progressDialog.show();
                        timerDelayRemoveDialog(50000, progressDialog);
                    }
                    else
                    {
                        connectionDetector.showConnectivityStatus();
                    }

                }
            });

        }


        // Inflate the layout for this fragment
        return rootView;
    }

    public void verifyOTP(String mobileotp)
    {
        mVerification.verify(mobileotp);
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    public void showDialog()
    {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Enter OTP");
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PHONETIC);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                String mobileotp = input.getText().toString();
                closeSoftKey();
                verifyOTP(mobileotp);
                progressDialog.setMessage("Verifying OTP");
                progressDialog.show();
                timerDelayRemoveDialog(50000,progressDialog);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                closeSoftKey();

            }
        });
        builder.show();

    }

    @Override
    public void onInitiated(String response) {
        progressDialog.dismiss();
        showDialog();

    }

    @Override
    public void onInitiationFailed(Exception paramException) {
        progressDialog.dismiss();
        showDialog();
        Toast.makeText(getContext(), "Please Try Again", Toast.LENGTH_LONG).show();

    }

    @Override
    public void onVerified(String response) {

        if (newMobileVerification)
            verifyNewMobile();
        else
        {
            updateUserMobiletoServer(newMobile);
        }

    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Toast.makeText(getContext(), "OTP Verification Failed", Toast.LENGTH_LONG).show();

    }


    public void verifyNewMobile()
    {
        newMobileVerification = false;
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Enter New Mobile Number");
        final EditText input = new EditText(getContext());
        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
        input.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PHONETIC);
        builder.setView(input);

        builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                newMobile = input.getText().toString();
                closeSoftKey();
                verifyNewMobile(newMobile);
                progressDialog.setMessage("Verifying Mobile by Sending OTP");
                progressDialog.show();
                timerDelayRemoveDialog(5000,progressDialog);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                //_verifyotp.setEnabled(true);
                closeSoftKey();

            }
        });
        builder.show();

    }

    public void verifyNewMobile(String mobilenumber)
    {

        ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
        if (connectionDetector.isConnectingToInternet()) {
            Config config = SendOtpVerification.config().context(getContext().getApplicationContext())
                    .build();
            mVerification = SendOtpVerification.createSmsVerification(config,newMobile,settingFragment, "91", "");
            mVerification.initiate();
            progressDialog = new ProgressDialog(getContext());
            progressDialog.setCancelable(false);
            progressDialog.setIndeterminate(false);
            progressDialog.setMessage("Sending OTP for New Mobile");
            progressDialog.show();
            timerDelayRemoveDialog(50000, progressDialog);
        }
        else
        {
            connectionDetector.showConnectivityStatus();
        }

    }

    public  void updateUserMobiletoServer(String newMobile)
    {


        // for updating profile Mobile
        String ip[] =  getContext().getResources().getStringArray(R.array.ip_address);
        s = new StoreAck().readFile(getContext().getApplicationContext());
        String ack = s[0];
        String userid = s[1];
        //String mobile = s[4];

        RequestParams params = new RequestParams();
        try {
            params.put("profile[mobile]",newMobile);
            params.put("profile[id]", userid);


        } catch (Exception e) {
            e.printStackTrace();
        }
        SyncHttpClient client = new SyncHttpClient();

        client.addHeader("Authorization", "Token token=\"" + ack + "\"");
        client.post(ip[0] + "api/v1/profiles/updatemobile", params,new JsonHttpResponseHandler(){

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.w("async", "success!!!!");
                if (response.has("success")) {
                    try {
                        if(response.getString("success").equals("true")) {

                            String mobile = response.getString("mobile");
                            _updatemobile.setText(mobile);
                            s[4] = mobile;
                            // updating the file in internal storage
                            JSONObject jObj = new JSONObject();
                            jObj.put("token", s[0]);
                            jObj.put("userid", s[1]);
                            jObj.put("email", s[2]);
                            jObj.put("roll", s[3]);
                            jObj.put("mobile", s[4]);
                            new StoreAck().writeFile(getContext(), jObj);
                            progressDialog.dismiss();
                            Toast.makeText(getContext(), "Profile Mobile Updated", Toast.LENGTH_LONG).show();
                            getFragmentManager().popBackStackImmediate();
                        }
                        else
                        {
                            Toast.makeText(getContext(), "Profile Mobile Updated Failed", Toast.LENGTH_LONG).show();
                        }
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
                else
                {

                    Toast.makeText(getContext(), "Profile Mobile Updated Failed", Toast.LENGTH_LONG).show();
                }

                //drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("async", "failure!");
                Toast.makeText(getContext(), "Profile Mobile Updated Failed", Toast.LENGTH_LONG).show();
               // drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
            }


        });


    }


    public void closeSoftKey()
    {
        //---------------Manually Closing the SoftInputFormWindow------------------
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Activity.INPUT_METHOD_SERVICE);
        View view = getActivity().getCurrentFocus();
        if(view!=null)
            imm.hideSoftInputFromWindow(view.getWindowToken(), 0);

    }

    //----------------Closing the ProgressDialog after given interval-------------------
    public void timerDelayRemoveDialog(long time, final Dialog d){

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing()) {
                    d.dismiss();
                  Toast.makeText(getContext(), "Seems taking to long", Toast.LENGTH_LONG).show();
                }
            }
        },time);
    }
}

