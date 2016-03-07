package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;
import com.google.gson.GsonBuilder;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.HttpResponseException;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.HashMap;
import java.util.Map;


/**
 * Created by harish on 4/3/16.
 */
public class SignupFragment extends Fragment {


    EditText _email = null;
    EditText _password = null;
    EditText _passwordconfrm = null;
    EditText _mobile = null;
    Button _signup = null;

    private String email ;
    private String moblie ;
    private String password ;
    private String passwordconfrm;
    private ProgressDialog progressdialog;
    private Map user;
    private Map userparamas;
    private FileOutputStream fos;

    public SignupFragment() {
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
         View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
         //ButterKnife.inject(rootView);
         _email = (EditText)rootView.findViewById(R.id.input_email);
         _mobile = (EditText)rootView.findViewById(R.id.input_mobile);
         _password = (EditText)rootView.findViewById(R.id.input_password);
         _passwordconfrm = (EditText)rootView.findViewById(R.id.input_passwordcomform);
         _signup = (Button)rootView.findViewById(R.id.btn_signup);

           _signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = _email.getText().toString();
                moblie = _mobile.getText().toString();
                password = _password.getText().toString();
                passwordconfrm = _passwordconfrm.getText().toString();

                signup();
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



    public void signup() {
        Log.d("TAG", "Signup");


        if (!validate()) {
            onSignupFailed("Validation Failed");
            return;
        }


        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setIndeterminate(false);
        progressdialog.setMessage("Creating Account...");
        progressdialog.show();



        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {


                    public void run() {
                        try {
                            JSONObject holder = new JSONObject();
                            JSONObject userObj = new JSONObject();
                            try {

                            holder.put("email", email);
                            holder.put("mobile", moblie);
                            holder.put("password", password);
                            holder.put("password_confirmation", passwordconfrm);

                                userObj.put("user",holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Http Post for sign_up and receving token and wirting in internal storage
                            HttpPost httpPost = new HttpPost("http://116.202.172.39:3000/users");
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            HttpResponse response = new DefaultHttpClient().execute(httpPost);
                            Log.d("Http Post Response:", response.toString());

                            String json = EntityUtils.toString(response.getEntity());
                            JSONObject temp1 = new JSONObject(json);
                            Log.d("Response status >>>>>>>", temp1.toString());

                            if(temp1.has("success")) {
                                if (temp1.getString("success").equals("true")) {
                                    Context c = getActivity().getApplicationContext();
                                    new StoreAck().writeFile(c, temp1.getString("token"));
                                    onSignupSuccess();
                                    progressdialog.dismiss();
                                }
                            }else
                            {
                                progressdialog.dismiss();
                                onSignupFailed(temp1.getString("errors"));
                            }

                            onSignupSuccess();
                            // onSignupFailed();
                            progressdialog.dismiss();
                        }catch (IOException e){e.printStackTrace();}
                         catch (JSONException e) {e.printStackTrace();}
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signup.setEnabled(true);
      //  setResult(RESULT_OK, null);
        //finish();
    }

    public void onSignupFailed(String msg) {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

        _signup.setEnabled(true);
    }

    // Validating signup fields
    public boolean validate() {
        boolean valid = true;



        if (moblie.isEmpty() || !Patterns.PHONE.matcher(moblie).matches()) {
            _mobile.setError("enter a valid phone number");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            _email.setError("enter a valid email address");
            valid = false;
        } else {
            _email.setError(null);
        }

        if (password.isEmpty() || password.length() < 8 || password.length() > 10) {
            _password.setError("between 8 and 10 alphanumeric characters");
            valid = false;
        } else {
            _password.setError(null);
        }
        if (passwordconfrm.isEmpty() || passwordconfrm.length() < 8 || password.length() > 10) {
            _passwordconfrm.setError("between 8 and 10 alphanumeric characters");
            valid = false;
        } else {
            _passwordconfrm.setError(null);
        }

        return  valid;
    }





}

