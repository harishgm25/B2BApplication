package com.example.harish.b2bapplication.adapter;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

import com.example.harish.b2bapplication.activity.ConnectionMyRequestFragment;
import com.example.harish.b2bapplication.activity.ConnectionOthersRequestFragment;

/**
 * Created by harish on 27/3/16.
 */
public class MyConncetionStatusTabAdapter extends FragmentPagerAdapter {

       String usertokens [];
       public static int int_items = 2 ;


        public MyConncetionStatusTabAdapter(FragmentManager fm, String[] s) {
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
                        case 0 : return new ConnectionMyRequestFragment(usertokens);
                        case 1 : return new ConnectionOthersRequestFragment(usertokens);

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
                    return "My REQUEST";
                case 1 :
                    return "OTHERS REQUEST";

            }
            return null;
        }

}
