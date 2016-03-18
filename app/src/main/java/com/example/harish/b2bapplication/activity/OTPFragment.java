package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;
import com.msg91.sendotp.library.Config;
import com.msg91.sendotp.library.SendOtpVerification;
import com.msg91.sendotp.library.Verification;
import com.msg91.sendotp.library.VerificationListener;

import java.util.logging.Handler;


/**
 * Created by harish on 4/3/16.
 */
public class OTPFragment extends Fragment implements VerificationListener {



    private EditText _mobile = null;
    private Button _verifyotp = null;
    private Verification mVerification;
    private OTPFragment otpFragment;
    private  String mobileotp;
    private ProgressDialog progressDialog;


    public OTPFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
       }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,Bundle savedInstanceState) {
        final StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
         View rootView = inflater.inflate(R.layout.fragment_otp, container, false);
         _mobile = (EditText)rootView.findViewById(R.id.input_mobile);
         _verifyotp = (Button)rootView.findViewById(R.id.btn_signup_otp);
         otpFragment = this;

        _verifyotp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                // Implement OTP Verification for given mobile
                ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
                if (connectionDetector.isConnectingToInternet()) {
                    Config config = SendOtpVerification.config().context(getContext().getApplicationContext())
                            .build();
                //  mVerification = SendOtpVerification.createSmsVerification(config, _mobile.getText().toString(), otpFragment, "91", "");
                 // mVerification.initiate();
                    onVerified("hello");
               /*     _verifyotp.setEnabled(false);
                    progressDialog = new ProgressDialog(getActivity());
                    progressDialog.setCancelable(false);
                    progressDialog.setIndeterminate(false);
                    progressDialog.setMessage("Sending OTP");
                    progressDialog.show();
                    timerDelayRemoveDialog(5000, progressDialog);*/


                }
                else
                {
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

    public  void  showDialog()
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
                mobileotp = input.getText().toString();
                closeSoftKey();
                verifyOTP();
                progressDialog.setMessage("Verifying OTP");
                progressDialog.show();
                timerDelayRemoveDialog(5000,progressDialog);

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                _verifyotp.setEnabled(true);
                closeSoftKey();


            }
        });
        builder.show();


    }

    public void verifyOTP()
    {
        mVerification.verify(mobileotp);
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


    }

    @Override
    public void onVerified(String response) {
//        progressDialog.dismiss();
        Toast.makeText(getContext(), "OTP Verifyed", Toast.LENGTH_LONG).show();
        SignupFragment signupFragment = new SignupFragment();
        Bundle arg = new Bundle();
        String mob = _mobile.getText().toString();
        arg.putSerializable("mobile",mob);
        signupFragment.setArguments(arg);
        FragmentManager fragmentManager = getFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        fragmentTransaction.replace(R.id.container_body, signupFragment);
        fragmentTransaction.commit();
    }

    @Override
    public void onVerificationFailed(Exception paramException) {
        Toast.makeText(getContext(), "OTP Verification Failed", Toast.LENGTH_LONG).show();
         progressDialog.dismiss();
        _verifyotp.setText("Resend OTP");
        _verifyotp.setEnabled(true);
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
                    Toast.makeText(getContext(), "Check Connectivity", Toast.LENGTH_LONG).show();
                }
            }
        }, 10000);
    }
}

