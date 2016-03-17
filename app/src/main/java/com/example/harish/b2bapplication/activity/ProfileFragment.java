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
    private String nameoffirm;
    private String estyear;
    private String website;
    private String pan;
    private String tanvat;
    private String bankacc;
    private String billingaddress;
    private String deliveryaddress;

    private String s[];
    private String ack;
    private String userid;


    public EditText _lastname;
    public EditText _firstname;
    public EditText _nameoffirm;
    public EditText _estyear;
    public EditText _website;
    public EditText _pan;
    public EditText _tanvat;
    public EditText _bankacc;
    public EditText _billingaddress;
    public EditText _deliveryaddress;
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
        _nameoffirm  = (EditText) rootView.findViewById(R.id.nameoffirm);
        _estyear = (EditText) rootView.findViewById(R.id.estyear);
        _website = (EditText) rootView.findViewById(R.id.website);
        _pan = (EditText) rootView.findViewById(R.id.pan);
        _tanvat = (EditText) rootView.findViewById(R.id.tanvat);
        _bankacc = (EditText) rootView.findViewById(R.id.bankacc);
        _billingaddress = (EditText) rootView.findViewById(R.id.billingaddress);
        _deliveryaddress = (EditText) rootView.findViewById(R.id.deliveryaddress);

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
                nameoffirm=_nameoffirm.getText().toString();
                estyear=_estyear.getText().toString();
                website=_website.getText().toString();
                pan =_pan.getText().toString();
                tanvat=_tanvat.getText().toString();
                bankacc=_bankacc.getText().toString();
                billingaddress=_billingaddress.getText().toString();
                deliveryaddress=_deliveryaddress.getText().toString();
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
     /*   if (!validate()) {
            onProfileUpdateFailed("Validation Failed");
            return;
        }*/
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
                                holder.put("nameoffirm", nameoffirm);
                                holder.put("estyear", estyear);
                                holder.put("website", website);
                                holder.put("pan", pan);
                                holder.put("tanvat", tanvat);
                                holder.put("bankacc", bankacc);
                                holder.put("billingaddress", billingaddress);
                                holder.put("deliveryaddress", deliveryaddress);
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
        getFragmentManager().popBackStackImmediate();

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
        if (nameoffirm.isEmpty()) {
            _nameoffirm.setError("enter a valid NameofFirm");
            valid = false;
        } else {
            _nameoffirm.setError(null);
        }
        if (estyear.isEmpty()) {
            _estyear.setError("enter a valid EST Year");
            valid = false;
        } else {
            _estyear.setError(null);
        }
        if (tanvat.isEmpty()) {
            _tanvat.setError("enter a valid TAN/VAT");
            valid = false;
        } else {
            _tanvat.setError(null);
        }
        if (pan.isEmpty()) {
            _pan.setError("enter a valid PAN no");
            valid = false;
        } else {
            _pan.setError(null);
        }
        if (bankacc.isEmpty()) {
            _bankacc.setError("enter a valid Bank Acc");
            valid = false;
        } else {
            _bankacc.setError(null);
        }
        if (website.isEmpty()) {
            _website.setError("enter a valid WebSite");
            valid = false;
        } else {
            _website.setError(null);
        }
        if (billingaddress.isEmpty()) {
            _billingaddress.setError("enter a valid Billing Address");
            valid = false;
        } else {
            _billingaddress.setError(null);
        }
        if (deliveryaddress.isEmpty()) {
            _deliveryaddress.setError("enter a valid Delivery Address");
            valid = false;
        } else {
            _deliveryaddress.setError(null);
        }

        return  valid;
    }

}

