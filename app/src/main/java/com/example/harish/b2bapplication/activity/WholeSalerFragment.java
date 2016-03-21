package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.harish.b2bapplication.R;


/**
 * Created by harish on 4/3/16.
 */
public class WholeSalerFragment extends Fragment {

    private AlertDialog.Builder builder;


    public WholeSalerFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);




    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_wholesaler, container, false);

        //Getting Bundle Serialized Object

        if(getArguments() != null) {
            boolean isnewsignup = getArguments().getBoolean("newsignup");
            //checking for new signup for profile filling
            if (isnewsignup) {
                isnewsignup = false;
                showDialog();
                getArguments().remove("newsignup");
            }
        }


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
        builder = new AlertDialog.Builder(getContext());
        builder.setCancelable(false);
        builder.setTitle("Wish to Complete the Profile");
        final EditText input = new EditText(getContext());
        builder.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
                callProfileFragment();

            }
        });
        builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();


            }
        });
        builder.show();



    }

    public void callProfileFragment()
    {

        FragmentTransaction fragmentTransaction = getFragmentManager().beginTransaction();
        fragmentTransaction.addToBackStack(null);
        fragmentTransaction.replace(R.id.container_body, new ProfileFragment());
        fragmentTransaction.commit();
        builder.setCancelable(true);

    }

}

