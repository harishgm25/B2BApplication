package com.example.harish.b2bapplication.adapter;

/**
 * Created by harish on 30/3/16.
 */
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.app.AppController;
import com.example.harish.b2bapplication.model.Profile;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.NetworkImageView;

public class ProfileListAdapter extends BaseAdapter implements Filterable {
    private Activity activity;
    private LayoutInflater inflater;



    private List<Profile> profilesItems;
    private List<Profile>OriginalData;
    ImageLoader imageLoader = AppController.getInstance().getImageLoader();

    public ProfileListAdapter(Activity activity, List<Profile> profilesItems) {
        this.activity = activity;
        this.profilesItems = profilesItems;
        this.OriginalData = profilesItems;
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
            convertView = inflater.inflate(R.layout.fragment_findconnections_gridview, null);

            if (imageLoader == null)
                imageLoader = AppController.getInstance().getImageLoader();
            NetworkImageView thumbNail = (NetworkImageView) convertView
                    .findViewById(R.id.img_profile);
            TextView firmname = (TextView) convertView.findViewById(R.id.profile_firmname);
            TextView billingaddress = (TextView) convertView.findViewById(R.id.profile_billingaddress);
            TextView roll = (TextView) convertView.findViewById(R.id.profile_roll);

            // getting movie data for the row
            Profile p = profilesItems.get(position);
            // thumbnail image
            thumbNail.setImageUrl(p.getThumbnailUrl(), imageLoader);

            // Name of Firm
            firmname.setText(p.getNameoffrim());

            // Billing Address
            billingaddress.setText(p.getBillingaddress());

            roll.setText(p.getRoll());


        return convertView;
    }

    @Override
    public Filter getFilter() {

        Filter mFilter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                Profile filterProfile =null;

                FilterResults results = new FilterResults();

                final List<Profile> list = OriginalData;

                int count = list.size();
                final List<Profile> nlist = new ArrayList<Profile>(count);

                String filterableString ;

                for (int i = 0; i < count; i++) {
                    filterProfile = list.get(i);
                    if (filterProfile.getRoll().equals(constraint)) {
                        nlist.add(filterProfile);
                    }
                }

                results.values = nlist;
                results.count = nlist.size();

                return results;
            }

            @SuppressWarnings("unchecked")
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                profilesItems = (List<Profile>) results.values;
                notifyDataSetChanged();
            }
        };
        return mFilter;
    }
}