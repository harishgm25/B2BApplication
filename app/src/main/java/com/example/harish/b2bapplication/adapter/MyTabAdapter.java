package com.example.harish.b2bapplication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.harish.b2bapplication.activity.CatalogFragment;
import com.example.harish.b2bapplication.activity.FindConnectionFragment;
import com.example.harish.b2bapplication.activity.OrderFragment;

/**
 * Created by harish on 27/3/16.
 */
public class MyTabAdapter  extends FragmentPagerAdapter {

       String usertokens [];
       public static int int_items = 3 ;


        public MyTabAdapter(FragmentManager fm,String[] s ) {
              super(fm);
              usertokens =s;
        }

        /**
         * Return fragment with respect to Position .,
         */

        @Override
        public Fragment getItem(int position)
        {
            switch (position){
                        case 0 : return new FindConnectionFragment(usertokens);
                        case 1 : return new OrderFragment(usertokens);
                        case 2 : return new CatalogFragment(usertokens);
            }
            return null;
        }

        @Override
        public int getCount() {

            return int_items;

        }

        /**
         * This method returns the title of the tab according to the position.
         */

        @Override
        public CharSequence getPageTitle(int position) {

            switch (position){
                case 0 :
                    return "CONNECTIONS";
                case 1 :
                    return "ORDERS";
                case 2 :
                    return "CATALOGS";
            }
            return null;
        }

}
