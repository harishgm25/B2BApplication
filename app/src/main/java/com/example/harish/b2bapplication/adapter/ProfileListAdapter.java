package com.example.harish.b2bapplication.adapter;

/**
 * Created by harish on 30/3/16.
 */
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.app.AppController;
import com.example.harish.b2bapplication.model.Profile;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.graphics.Movie;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ProfileListAdapter extends BaseAdapter {
    private Activity activity;
    private LayoutInflater inflater;
    private List<Profile> profilesItems;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProfileListAdapter(Activity activity, List<Profile> profilesItems) {
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
            convertView = inflater.inflate(R.layout.fragment_findconnections_listview, null);

        if (imageLoader == null)
            imageLoader = AppController.getInstance().getImageLoader();
        NetworkImageView thumbNail = (NetworkImageView) convertView
                .findViewById(R.id.img_profile);
        TextView email = (TextView) convertView.findViewById(R.id.profile_email);
        TextView mobile = (TextView) convertView.findViewById(R.id.profile_mobile);

        // getting movie data for the row
        Profile p = profilesItems.get(position);

        // thumbnail image
        thumbNail.setImageUrl(p.getThumbnailUrl(), imageLoader);

        // email
        email.setText(p.getEmail());

        // mobile
        mobile.setText(p.getMobile());


        return convertView;
    }

}