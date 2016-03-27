package com.example.harish.b2bapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.harish.b2bapplication.R;


/**
 * Created by harish on 4/3/16.
 */
public class FindConnectionFragment extends Fragment {
    String usertokens [];

    public FindConnectionFragment() {
        // Required empty public constructor
    }
    @SuppressLint("ValidFragment")
    public FindConnectionFragment(String s[]) {
        usertokens =s;
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_findconnections, container, false);
        TextView label = (TextView)rootView.findViewById(R.id.label);
        label.setText(usertokens[3]);


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
}

