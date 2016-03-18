package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Patterns;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.util.Log;
import android.widget.Spinner;
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
    private  String rollOption;
    private String password ;
    private String passwordconfrm;
    private ProgressDialog progressdialog;
    private Spinner roll;
    private View  franavdrawer = null;


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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
         View rootView = inflater.inflate(R.layout.fragment_signup, container, false);
        franavdrawer = inflater.inflate(R.layout.fragment_navigation_drawer,container,false);
         _email = (EditText)rootView.findViewById(R.id.input_email);
         _mobile = (EditText)rootView.findViewById(R.id.input_mobile);
         _password = (EditText)rootView.findViewById(R.id.input_password);
         _passwordconfrm = (EditText)rootView.findViewById(R.id.input_passwordcomform);
         _signup = (Button)rootView.findViewById(R.id.btn_signup);

         //Getting Bundle Serialized Object
         String mob =  getArguments().getString("mobile");
        _mobile.setText(mob);
        _mobile.setKeyListener(null);

         // Filling the Spinner
         roll = (Spinner) rootView.findViewById(R.id.roll);
         String [] rolls = {"Manufacture","WholeSaler","Retailer"};
         ArrayAdapter<String> LTRadapter = new ArrayAdapter<String>(this.getActivity(), android.R.layout.simple_spinner_item, rolls);
         LTRadapter.setDropDownViewResource(android.R.layout.simple_dropdown_item_1line);
         roll.setAdapter(LTRadapter);

           _signup.setOnClickListener(new View.OnClickListener() {
               @Override
               public void onClick(View v) {
                   email = _email.getText().toString();
                   moblie = _mobile.getText().toString();
                   rollOption = roll.getSelectedItem().toString();
                   password = _password.getText().toString();
                   passwordconfrm = _passwordconfrm.getText().toString();
                   ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
                   if (connectionDetector.isConnectingToInternet()) {

                       signup();
                   } else {
                       connectionDetector.showConnectivityStatus();
                   }

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
                            holder.put("roll",rollOption);
                            holder.put("password", password);
                            holder.put("password_confirmation", passwordconfrm);

                                userObj.put("user",holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                            // Http Post for sign_up and receving token and wirting in internal storage
                            String [] ip = getActivity().getResources().getStringArray(R.array.ip_address);
                            HttpPost httpPost = new HttpPost(ip[0]+"users");
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
                                    new StoreAck().writeFile(c, temp1);
                                    onSignupSuccess();
                                    progressdialog.dismiss();
                                }
                            }else
                            {
                                progressdialog.dismiss();
                                onSignupFailed(temp1.getString("errors"));
                            }

                            //onSignupSuccess();
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
        franavdrawer.setVisibility(View.VISIBLE);
        franavdrawer.invalidate();

        //Setting the Fragment Based on The User---------------------------------
        android.support.v4.app.Fragment userFragment = null;
        String s[] = new StoreAck().readFile(getContext().getApplicationContext());
        if(s[3].equals("Manufacture"))
            userFragment = new ManufactureFragment();
        if(s[3].equals("WholeSaler"))
            userFragment = new WholeSalerFragment();
        if(s[3].equals("\"Retaile"))
            userFragment = new RetailerFragment();


        // setting flag for dialog for filling the profile
        Bundle arg = new Bundle();
        Boolean newprofile = true;
        arg.putSerializable("newsignup",newprofile);
        if (userFragment != null) {
            userFragment.setArguments(arg);
        }
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, userFragment);
        fragmentTransaction.commit();

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

