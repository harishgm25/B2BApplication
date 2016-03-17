package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
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
public class ProfileFragment extends Fragment {


    private String lastname;
    private String firstname;
    private String shopname;
    private String address;
    private String tanvat;
    private String regno;
    private String shopestablishment;
    private String tradelicense;
    private String manufacturinglicense;
    private String s[];
    private String ack;
    private String userid;


    public EditText _lastname;
    public EditText _firstname;
    public EditText _shopname;
    public EditText _address;
    public EditText _tanvat;
    public EditText _regno;
    public EditText _shopestablishment;
    public EditText _tradelicense;
    public EditText _manufacturinglicense;
    public Button profileUpdateBtn ;
    public ProgressDialog progressdialog;


    public ProfileFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
       StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
       StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_profile, container, false);
        _lastname = (EditText) rootView.findViewById(R.id.lastname);
        _firstname= (EditText) rootView.findViewById(R.id.firstname);
        _address  = (EditText) rootView.findViewById(R.id.address);
        _shopname = (EditText) rootView.findViewById(R.id.shopname);
        _shopestablishment = (EditText) rootView.findViewById(R.id.shopestablishment);
        _regno = (EditText) rootView.findViewById(R.id.registerno);
        _tanvat = (EditText) rootView.findViewById(R.id.tanvat);
        _tradelicense = (EditText) rootView.findViewById(R.id.tradelicense);
        _manufacturinglicense = (EditText) rootView.findViewById(R.id.manufacturinglicense);


        //-------------------------------Getting User ID and Token-----------------------------
        s = new StoreAck().readFile(getContext().getApplicationContext());
        ack = s[0];
        userid= s[1];
        //------------------------------------Getting Profile and Intialising
        new GetProfile().getProfile(ack, userid, getContext(),this);





        profileUpdateBtn =(Button)rootView.findViewById(R.id.btn_updateprofile);
        profileUpdateBtn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                lastname = _lastname.getText().toString();
                firstname =_firstname.getText().toString();
                address=_address.getText().toString();
                shopestablishment=_shopestablishment.getText().toString();
                shopname=_shopname.getText().toString();
                regno =_regno.getText().toString();
                tanvat=_tanvat.getText().toString();
                tradelicense=_tradelicense.getText().toString();
                manufacturinglicense=_manufacturinglicense.getText().toString();
                ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
                if (connectionDetector.isConnectingToInternet()) {

                         updateProfile();
                }

                else {
                    connectionDetector.showConnectivityStatus();
                }
        }
        });


        // Inflate the layout for this fragment
        return rootView;
    }

    public void updateProfile(){
        Log.d("TAG", "Update Pofile");
        if (!validate()) {
            onProfileUpdateFailed("Validation Failed");
            return;
        }
        progressdialog = new ProgressDialog(getActivity());
        progressdialog.setIndeterminate(false);
        progressdialog.setMessage("Updating Profile");
        progressdialog.show();



        // TODO: Implement your own signup logic here.

        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        JSONObject temp1;
                        try {
                            JSONObject holder = new JSONObject();
                            JSONObject userObj = new JSONObject();
                            String s[] = new StoreAck().readFile(getContext().getApplicationContext());
                            String ack = s[0];
                            String userid = s[1];


                            try {
                                holder.put("lastname", lastname);
                                holder.put("firstname", firstname);
                                holder.put("shopname", shopname);
                                holder.put("shopestablishment", shopestablishment);
                                holder.put("address", address);
                                holder.put("tanvat", tanvat);
                                holder.put("registerno", regno);
                                holder.put("tradelicense", tradelicense);
                                holder.put("manufacturinglicense", manufacturinglicense);
                                holder.put("id", userid);
                                userObj.put("profile", holder);
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                            // Http Post for sign_in and receving token and wirting in internal storage
                            String[] ip = getActivity().getResources().getStringArray(R.array.ip_address);
                            HttpPost httpPost = new HttpPost(ip[0] + "api/v1/profiles/updateprofile");
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.addHeader("Authorization", "Token token=\"" + ack + "\"");
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
                                    new StoreAck().writeFile(c, temp1);
                                    progressdialog.dismiss();
                                    onProfileUpdateSuccess();

                                } else {
                                      progressdialog.dismiss();
                                      onProfileUpdateFailed(temp1.getString("Updation Failed"));
                                }


                            } else {
                                progressdialog.dismiss();
                                onProfileUpdateFailed(temp1.getString("Updation Failed"));
                            }


                        } catch (IOException e) {
                            e.printStackTrace();
                            progressdialog.dismiss();
                            //  onSigninFailed("error");
                        } catch (JSONException e) {
                            e.printStackTrace();
                            progressdialog.dismiss();
                            // onSigninFailed("error");
                        } catch (Exception e) {
                            e.printStackTrace();
                            progressdialog.dismiss();
                            //onSigninFailed("error");
                        }
                    }
                }, 3000);

        }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }



    public void onProfileUpdateSuccess()
    {
        Toast.makeText(getContext(), "Profile Updated", Toast.LENGTH_LONG).show();
        getFragmentManager().popBackStackImmediate();

    }
    public  void onProfileUpdateFailed(String msg)
    {
        Toast.makeText(getContext(), msg, Toast.LENGTH_LONG).show();

    }

    // Validating signup fields
    public boolean validate() {
        boolean valid = true;




      /*

        public EditText _manufacturinglicense;
        public Button profileUpdateBtn ;*/

        if ( lastname.isEmpty() ) {
            _lastname.setError("enter a valid Last Name");
            valid = false;
        } else {
            _lastname.setError(null);
        }
        if (firstname.isEmpty() ) {
            _firstname.setError("enter a valid First Name");
            valid = false;
        } else {
            _firstname.setError(null);
        }
        if (shopname.isEmpty()) {
            _shopname.setError("enter a valid ShopName");
            valid = false;
        } else {
            _shopname.setError(null);
        }
        if (address.isEmpty()) {
            _address.setError("enter a valid Address");
            valid = false;
        } else {
            _address.setError(null);
        }
        if (tanvat.isEmpty()) {
            _tanvat.setError("enter a valid TAN/VAT");
            valid = false;
        } else {
            _tanvat.setError(null);
        }
        if (regno.isEmpty()) {
            _regno.setError("enter a valid Registration No");
            valid = false;
        } else {
            _regno.setError(null);
        }
        if (shopestablishment.isEmpty()) {
            _shopestablishment.setError("enter a valid ShopEstablishment");
            valid = false;
        } else {
            _shopestablishment.setError(null);
        }
        if (tradelicense.isEmpty()) {
            _tradelicense.setError("enter a valid Trade License");
            valid = false;
        } else {
            _tradelicense.setError(null);
        }
        if (manufacturinglicense.isEmpty()) {
            _manufacturinglicense.setError("enter a valid ManufacturingLicense");
            valid = false;
        } else {
            _manufacturinglicense.setError(null);
        }

        return  valid;
    }

}

