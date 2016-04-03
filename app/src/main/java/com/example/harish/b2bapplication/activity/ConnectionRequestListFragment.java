package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.ConnectionRequestListAdapter;
import com.example.harish.b2bapplication.adapter.MyConncetionStatusTabAdapter;
import com.example.harish.b2bapplication.adapter.MyTabAdapter;
import com.example.harish.b2bapplication.model.FindConnectionJSONParser;
import com.example.harish.b2bapplication.model.FindConnectionStatusJSONParser;
import com.example.harish.b2bapplication.model.Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by harish on 4/3/16.
 */
public class ConnectionRequestListFragment extends Fragment {

    public static TabLayout tabLayout;
    public static ViewPager viewPager;
    private String s[];

    public ConnectionRequestListFragment() {
        // Required empty public constructor
        s = new String[6];
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
 }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View tabView =  inflater.inflate(R.layout.tab_layout,null);
        tabLayout = (TabLayout) tabView.findViewById(R.id.tabs);
        viewPager = (ViewPager) tabView.findViewById(R.id.viewpager);

        if(getArguments() != null) {
            s = getArguments().getStringArray("usertokens");  // getting user tokens for previous fragments or activity

        }
        viewPager.setAdapter(new MyConncetionStatusTabAdapter(getChildFragmentManager(),s));


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






}


