package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Entity;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.model.NavDrawerItem;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.io.FileOutputStream;
import java.io.IOException;
import java.io.Serializable;
import java.util.Map;


/**
 * Created by harish on 4/3/16.
 */
public class SigninFragment extends Fragment {


    private EditText _email = null;
    private EditText _password = null;
    private EditText _mobile = null;
    private Button _signin = null;
    private Button _sign  = null;
    private TextView _signup = null;
    private View  franavdrawer = null;
    private DrawerLayout drawerLayout = null;

    private String email ;
    private String moblie ;
    private String password ;
    private ProgressDialog progressdialog;

    private FileOutputStream fos;

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
         franavdrawer = inflater.inflate(R.layout.fragment_navigation_drawer,container,false);

         _sign =  (Button)franavdrawer.findViewById(R.id.btn_sign);

         _email = (EditText)rootView.findViewById(R.id.input_email);
         _mobile = (EditText)rootView.findViewById(R.id.input_mobile);
         _password = (EditText)rootView.findViewById(R.id.input_password);
         _signin = (Button)rootView.findViewById(R.id.btn_signin);
         _signup = (TextView)rootView.findViewById(R.id.link_signup);


           _signin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                email = _email.getText().toString();
                moblie = _mobile.getText().toString();
                password = _password.getText().toString();
                signin();
            }
        });

        _signup.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                FragmentManager fragmentManager = getFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.replace(R.id.container_body, new SignupFragment());
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


        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setIndeterminate(false);
        progressdialog.setMessage("Signin Account...");
        progressdialog.show();



        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        JSONObject temp1;
                        try {
                            JSONObject holder = new JSONObject();
                            JSONObject userObj = new JSONObject();

                            try {

                            holder.put("email", email);
                            holder.put("mobile", moblie);
                            holder.put("password", password);


                                userObj.put("user",holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Http Post for sign_in and receving token and wirting in internal storage

                            HttpPost httpPost = new HttpPost("http://116.202.152.62:3000/users/sign_in");
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            HttpResponse response = new DefaultHttpClient().execute(httpPost);
                            Log.d("Http Post Response:", response.toString());

                            String json = EntityUtils.toString(response.getEntity());
                            temp1 = new JSONObject(json);
                            Log.d("Response status >>>>>>>", temp1.toString());

                            if(temp1.has("success")) {
                                if (temp1.getString("success").equals("true")) {
                                    Context c = getActivity().getApplicationContext();
                                    new StoreAck().writeFile(c,temp1);
                                    onSigninSuccess();
                                    progressdialog.dismiss();

                                }
                            }else
                            {
                                progressdialog.dismiss();
                                onSigninFailed(temp1.getString("error"));
                            }



                        }catch (IOException e){e.printStackTrace();
                            progressdialog.dismiss();
                            onSigninFailed("error");
                        }
                        catch (JSONException e) {e.printStackTrace();
                            progressdialog.dismiss();
                            onSigninFailed("error");
                        }
                        catch (Exception e) {e.printStackTrace();
                            progressdialog.dismiss();
                            onSigninFailed("error");
                        }
                    }
                }, 3000);
    }


    public void onSigninSuccess() {
        _signin.setEnabled(true);
        Log.d("----------", _sign.getText().toString());
        _sign.setText("Log_out");
        franavdrawer.setVisibility(View.VISIBLE);
        franavdrawer.invalidate();



        return;
    }

    public void onSigninFailed(String msg) {
        Toast.makeText(getContext(),msg, Toast.LENGTH_LONG).show();

        _signin.setEnabled(true);
    }

    // Validating signin fields
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

        return  valid;
    }





}

