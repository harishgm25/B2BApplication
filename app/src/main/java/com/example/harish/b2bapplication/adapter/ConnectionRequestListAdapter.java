package com.example.harish.b2bapplication.adapter;

/**
 * Created by harish on 30/3/16.
 */

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.app.AppController;
import com.example.harish.b2bapplication.model.Profile;

import java.util.List;

public class ConnectionRequestListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Profile> profilesItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ConnectionRequestListAdapter(Activity activity, List<Profile> profilesItems) {
        this.activity = activity;
        this.profilesItems = profilesItems;
    }


    @Override
    public int getCount() {
        return profilesItems.size();
    }

    @Override
    public Object getItem(int location) {
        return profilesItems.get(location);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        if (inflater == null)
            inflater = (LayoutInflater) activity
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        if (convertView == null)
            convertView = inflater.inflate(R.layout.fragment_connection_my_request_listview, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.img_profile);
        TextView firmname = (TextView) convertView.findViewById(R.id.profile_firmname);
        TextView billingaddress = (TextView) convertView.findViewById(R.id.profile_billingaddress);
        TextView roll =(TextView) convertView.findViewById(R.id.profile_roll);
        TextView status = (TextView) convertView.findViewById(R.id.connection_request_status);

        // getting movie data for the row
        Profile p = profilesItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(p.getThumbnailUrl(), imageLoader);

        // Name of Firm
        firmname.setText(p.getNameoffrim());

        // Billing Address
        billingaddress.setText(p.getBillingaddress());

        roll.setText(p.getRoll());

        status.setText(p.getConnectionstatus());


        return convertView;
    }

}