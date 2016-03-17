package com.example.harish.b2bapplication.activity;

import android.app.Activity;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.Toast;

import com.example.harish.b2bapplication.R;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import com.example.harish.b2bapplication.model.ProductJSONParser;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;



/**
 * Created by harish on 4/3/16.
 */
public class ProductFragment extends Fragment {

    private String s[];
    private String ack;
    private String userid;
    private JSONArray productJson;
    ListViewLoaderTask listViewLoaderTask;

    public ProductFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_product, container, false);

        //-------------------------------Getting User ID and Token-----------------------------
        s = new StoreAck().readFile(getContext().getApplicationContext());
        ack = s[0];
        userid= s[1];



        ConnectionDetector connectionDetector = new ConnectionDetector(getContext());
        if (connectionDetector.isConnectingToInternet()) {
            /** JSON Products DownLoader Task*/
            ProductLoaderTask productLoaderTask = new ProductLoaderTask();
            productLoaderTask.execute();
        }
        else
        {
            connectionDetector.showConnectivityStatus();
        }

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

    private class ListViewLoaderTask extends AsyncTask<JSONArray, Void, SimpleAdapter> {

        List<HashMap<String,String>> products = null;
        JSONArray productArray;
        /** Doing the parsing of xml data in a non-ui thread */
        @Override
        protected SimpleAdapter doInBackground(JSONArray... strJson) {
            try{

                JSONArray productArray = strJson[0];
                ProductJSONParser productJSONParser = new ProductJSONParser();
                products = productJSONParser.parse(productArray);
            }catch(Exception e){
                Log.d("JSON Exception1", e.toString());
            }
            /** Keys used in Hashmap */
            String[] from = { "name","description"};

            /** Ids of views in listview_layout */
            int[] to = { R.id.product_name,R.id.product_description};

            /** Instantiating an adapter to store each items
             *  R.layout.listview_layout defines the layout of each item
             */
            SimpleAdapter adapter = new SimpleAdapter(getContext(), products, R.layout.fragment_product_listview, from, to);

            return adapter;
        }

        /** Invoked by the Android system on "doInBackground" is executed completely */
        /** This will be executed in ui thread */
        @Override
        protected void onPostExecute(SimpleAdapter adapter) {

            /** Getting a reference to listview of main.xml layout file */
            ListView listView = ( ListView ) getActivity().findViewById(R.id.lv_countries);

            /** Setting the adapter containing the country list to listview */
            listView.setAdapter(adapter);
        }
    }


    private class ProductLoaderTask extends  AsyncTask<String, Void, JSONArray>
    {
        @Override
        protected void onPreExecute() {
            Toast.makeText(getContext(), "Product DownLoading", Toast.LENGTH_LONG).show();
            super.onPreExecute();
        }

        @Override
        protected JSONArray doInBackground(String... params) {

            String[] ip = getActivity().getResources().getStringArray(R.array.ip_address);
            HttpGet httpGet = new HttpGet(ip[0]+"api/v1/products");
            httpGet.addHeader("Authorization", "Token token=\"" + ack + "\"");
            httpGet.setHeader("Accept", "application/json");
            httpGet.setHeader("Content-type", "application/json");
            try {
                HttpResponse response = new DefaultHttpClient().execute(httpGet);
                String json = EntityUtils.toString(response.getEntity());
                productJson = new JSONArray(json);
                Log.d("Response status >>>>>>>", productJson.toString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }


            return productJson;
        }

        @Override
        protected void onPostExecute(JSONArray jsonObject) {
            Toast.makeText(getContext(), "Product DownLoaded", Toast.LENGTH_LONG).show();
            ListViewLoaderTask listViewLoaderTask = new ListViewLoaderTask();
            listViewLoaderTask.execute(productJson);
            super.onPostExecute(jsonObject);
        }
    }
}

















