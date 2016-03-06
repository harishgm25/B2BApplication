package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
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
import org.json.JSONException;
import org.json.JSONObject;

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
            onSignupFailed();
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
                            /*user = new HashMap<String, String>();
                                userparamas = new HashMap<String,String>();
                                userparamas.put("email", email);
                                userparamas.put("mobile", moblie);
                                userparamas.put("password", password);
                                userparamas.put("password_confirmation", passwordconfrm);

                            String jsonparams = new GsonBuilder().create().toJson(userparamas, Map.class);

                            user = new HashMap<String,String>();
                            user.put("user",jsonparams);*/
                            try {

                            holder.put("email", email);
                            holder.put("mobile", moblie);
                            holder.put("password", password);
                            holder.put("password_confirmation", passwordconfrm);

                                userObj.put("user",holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                           // String json = new GsonBuilder().create().toJson(user, Map.class);
                            //json = "user:\""+json;
                            HttpPost httpPost = new HttpPost("http://116.202.182.213:3000/users");
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            HttpResponse response = new DefaultHttpClient().execute(httpPost);
                            Log.d("Http Post Response:", response.toString());

                            onSignupSuccess();
                            // onSignupFailed();
                            progressdialog.dismiss();
                        }catch (IOException e){e.printStackTrace();}
                    }
                }, 3000);
    }


    public void onSignupSuccess() {
        _signup.setEnabled(true);
      //  setResult(RESULT_OK, null);
        //finish();
    }

    public void onSignupFailed() {
        Toast.makeText(getContext(), "Login failed", Toast.LENGTH_LONG).show();

        _signup.setEnabled(true);
    }

    // Validating signup fields
    public boolean validate() {
        boolean valid = true;




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

        return  true;//valid;
    }





}

