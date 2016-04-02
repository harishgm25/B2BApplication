package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
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
import com.android.volley.toolbox.Volley;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.ConnectionRequestListAdapter;
import com.example.harish.b2bapplication.model.FindConnectionJSONParser;
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

    private String usertokens [];
    private String ack;
    private String userid;
    private ConnectionRequestListAdapter adapter;
    private ListView listView;
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
        View rootView = inflater.inflate(R.layout.fragment_connection_request, container, false);
        if(getArguments() != null) {
            s = getArguments().getStringArray("usertokens");  // getting user tokens for previous fragments or activity

        }


        ack = s[0];
        userid= s[1];
        listView = (ListView)rootView.findViewById(R.id.lv_connection_request);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Profile profile = (Profile) adapter.getItem(arg2);
                Log.d("profile", profile.toString());
                Bundle arg = new Bundle();
                arg.putSerializable("userprofile", profile);


                Intent completeProfile= new Intent(getActivity(),ConnectionActivty.class);
                completeProfile.putExtra("ack",ack);
                completeProfile.putExtra("userid",userid);
                completeProfile.putExtra("profile",profile);
                startActivity(completeProfile);


            }
        });


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


                            adapter = new ConnectionRequestListAdapter(getActivity(),profileList);
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


