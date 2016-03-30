package com.example.harish.b2bapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.example.harish.b2bapplication.adapter.ProfileListAdapter;
import com.example.harish.b2bapplication.app.AppController;
import com.example.harish.b2bapplication.model.FindConnectionJSONParser;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.model.Profile;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.android.volley.toolbox.JsonArrayRequest;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


/**
 * Created by harish on 4/3/16.
 */
public class FindConnectionFragment extends Fragment {
    private String usertokens [];
    private String ack;
    private String userid;
    private ProfileListAdapter adapter;
    private ProgressDialog pDialog;
    private  ListView listView;


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
        //TextView label = (TextView)rootView.findViewById(R.id.label);
       // label.setText(usertokens[3]);
        ack = usertokens[0];
        userid= usertokens[1];
       listView = (ListView)rootView.findViewById(R.id.lv_profiles);


        ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
        if (connectionDetector.isConnectingToInternet()) {

            String[] ip = getActivity().getResources().getStringArray(R.array.ip_address);
            StringRequest postRequest = new StringRequest(Request.Method.POST, ip[0]+"api/v1/profiles/showconnectionprofile",
                    new Response.Listener<String>()
                    {
                        String[] ip = getActivity().getResources().getStringArray(R.array.ip_address);
                        List profileList;
                        @Override
                        public void onResponse(String response) {
                            // response
                           // Log.d("Response+++===", response);
                            JSONObject jsnobject = null;
                            try {
                                jsnobject = new JSONObject(response);
                                JSONArray profileArray = jsnobject.getJSONArray("profile");
                                FindConnectionJSONParser profileJSONParser = new FindConnectionJSONParser();
                                profileList = profileJSONParser.parse(profileArray,ip[0]);
                                String name = "temp";
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }


                            adapter = new ProfileListAdapter(getActivity(),profileList);
                            listView.setAdapter(adapter);
                            adapter.notifyDataSetChanged();

                        }
                    },
                    new Response.ErrorListener()
                    {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            // error
                            Log.d("Error.Response",error.toString());
                        }
                    }

            ) {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("Authorization", "Token token=\"" + ack + "\"");
                    params.put("Accept", "application/json");
                    params.put("Content-type", "application/json");

                    return params;
                }
            };
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(postRequest);

        }
        else
        {
            connectionDetector.showConnectivityStatus();
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



}


