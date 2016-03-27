package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.MyTabAdapter;


/**
 * Created by harish on 4/3/16.
 */
public class WholeSalerFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    private AlertDialog.Builder builder;
    private String s[];


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
        //View rootView = inflater.inflate(R.layout.fragment_findconnections, container, false);
        View tabView =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) tabView.findViewById(R.id.tabs);
        viewPager = (ViewPager) tabView.findViewById(R.id.viewpager);

        //Getting Bundle Serialized Object

        if(getArguments() != null) {
            boolean isnewsignup = getArguments().getBoolean("newsignup");
            s = getArguments().getStringArray("usertokens");  // getting user tokens for previous fragments or activity
            //checking for new signup for profile filling
            if (isnewsignup) {
                isnewsignup = false;
                showDialog();
                getArguments().remove("newsignup");
            }
        }
        viewPager.setAdapter(new MyTabAdapter(getChildFragmentManager(),s));

        /**
         * Now , this is a workaround ,
         * The setupWithViewPager dose't works without the runnable .
         * Maybe a Support Library Bug .
         */

        tabLayout.post(new Runnable() {
            @Override
            public void run() {
                tabLayout.setupWithViewPager(viewPager);
            }
        });


        // Inflate the layout for this fragment
        return tabView;
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

