package com.example.harish.b2bapplication.activity;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
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
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.ConnectionRequestListAdapter;
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
public class ConnectionMyRequestFragment extends Fragment {


    private String usertokens [];
    private String ack;
    private String userid;
    private ConnectionRequestListAdapter adapter;
    private ListView listView;
    private ProgressDialog progressDialog;
    private String s[];


    public ConnectionMyRequestFragment()
    {

    }

    @SuppressLint("ValidFragment")
    public ConnectionMyRequestFragment(String[] usertokens) {
        // Required empty public constructor
        this.usertokens=usertokens;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_connection_my_request, container, false);


        ack = usertokens[0];
        userid= usertokens[1];
        listView = (ListView)rootView.findViewById(R.id.lv_connection_request);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {
                // TODO Auto-generated method stub
                Profile profile = (Profile) adapter.getItem(arg2);
                Log.d("profile", profile.toString());
                Bundle arg = new Bundle();
                arg.putSerializable("userprofile", profile);


                Intent completeProfile= new Intent(getActivity(),ConnectionApprovedActivty.class);
                completeProfile.putExtra("ack",ack);
                completeProfile.putExtra("userid",userid);
                completeProfile.putExtra("profile",profile);
                startActivityForResult(completeProfile, 1001);


            }
        });


        // Inflate the layout for this fragment
        getListFromServer();
        return rootView;
    }

    public void getListFromServer()
    {
        ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
        if (connectionDetector.isConnectingToInternet()) {

            String[] ip = getActivity().getResources().getStringArray(R.array.ip_address);
            JSONObject holderData = new JSONObject();
            JSONObject childData = new JSONObject();
            try {
                childData.put("user_id",userid);
                holderData.put("findconnection", childData);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            JsonObjectRequest postRequest = new JsonObjectRequest(Request.Method.POST,ip[0]+"api/v1/findconnections/getfriendrequeststatus",holderData,
                    new Response.Listener<JSONObject>()
                    {
                        List profileList;
                        String[] ip = getActivity().getResources().getStringArray(R.array.ip_address);
                        @Override
                        public void onResponse(JSONObject response) {
                            System.out.println(response);
                            JSONObject jsnobject = null;
                            try {
                                jsnobject = response;
                                JSONArray profileArray = jsnobject.getJSONArray("myfriendrequest");
                                FindConnectionStatusJSONParser profileJSONParser = new FindConnectionStatusJSONParser();
                                profileList = profileJSONParser.parse(profileArray,ip[0]);
                                progressDialog.dismiss();
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
                            System.out.println(error);
                            progressDialog.dismiss();
                        }
                    })
            {
                @Override
                protected Map<String, String> getParams() throws AuthFailureError {
                    Map<String, String>  params = new HashMap<String, String>();
                    params.put("findconnection[user_id]",userid);

                    return super.getParams();
                }
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    Map<String, String> params = new HashMap<String, String>();
                    params.put("Authorization", "Token token=\"" + ack + "\"");
                    params.put("Accept", "application/json");
                    params.put("Content-type", "application/json");
                    return params;
                }
            };
            postRequest.setRetryPolicy(new DefaultRetryPolicy(10000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            RequestQueue requestQueue = Volley.newRequestQueue(getActivity());
            requestQueue.add(postRequest);
            progressDialog = new ProgressDialog(getActivity());
            progressDialog.setMessage("Fetching The File....");
            progressDialog.show();

        }
        else
        {
            connectionDetector.showConnectivityStatus();
        }


    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        getListFromServer();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}

