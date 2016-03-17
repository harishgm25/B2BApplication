package com.example.harish.b2bapplication.activity;
import android.content.pm.PackageInfo;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.harish.b2bapplication.R;
import java.security.MessageDigest;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.widget.ImageView;

import java.security.NoSuchAlgorithmException;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Button _sign = null;
    private ImageView _profileView = null;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        //DrawerLayout drawerLayout = (DrawerLayout)findViewById(R.id.drawer_layout);



        super.onPrepareOptionsMenu(menu);
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);

        //-------------------- OTP SSH KEY for MSG 91------------------------------
        MessageDigest md = null;
        try {
            PackageInfo info = getApplicationContext().getPackageManager().getPackageInfo(
                    getApplicationContext().getPackageName(),
                    PackageManager.GET_SIGNATURES);
            for (Signature signature : info.signatures) {
                md = MessageDigest.getInstance("SHA");
                md.update(signature.toByteArray());
            }
        } catch (PackageManager.NameNotFoundException e) {

        } catch (NoSuchAlgorithmException e) {

        }
        //   Log.i("SecretKey = ",Base64.encodeToString(md.digest(), Base64.DEFAULT));
        //---------------------------------------------------------------------------------



            drawerFragment = (FragmentDrawer) getSupportFragmentManager().findFragmentById(R.id.fragment_navigation_drawer);
            drawerFragment.setUp(R.id.fragment_navigation_drawer, (DrawerLayout) findViewById(R.id.drawer_layout), mToolbar);
            drawerFragment.setDrawerListener(this);

            _sign = (Button) findViewById(R.id.btn_sign);

            _profileView = (ImageView)findViewById(R.id.circleView);

            _profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (connectionDetector.isConnectingToInternet())
                    {
                        String s = _sign.getText().toString();
                        if ((_sign.getText().toString()).equals("Signin")) {
                            _profileView.setOnClickListener(null);
                            drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
                        } else {
                            ProfileFragment p = new ProfileFragment();
                            Bundle bundles = new Bundle();
                            FragmentManager fragmentManager = getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.replace(R.id.container_body,p);
                            fragmentTransaction.commit();
                            drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually

                        }
                    }
                    else
                    {
                        connectionDetector.showConnectivityStatus();
                        drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
                    }

                }
            });






                _sign.setOnClickListener(new View.OnClickListener() {
                       @Override
                        public void onClick (View v){
                            ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                            DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                            if (connectionDetector.isConnectingToInternet())
                            {
                                if ((_sign.getText().toString()).equals("Logout")) {
                                    new StoreAck().DeleteFile(getApplicationContext());
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container_body, new HomeFragment());
                                    fragmentTransaction.commit();
                                    drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
                                } else {
                                    SigninFragment s = new SigninFragment();
                                    Bundle bundles = new Bundle();
                                    FragmentManager fragmentManager = getSupportFragmentManager();
                                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                    fragmentTransaction.replace(R.id.container_body, s);
                                    fragmentTransaction.commit();
                                    drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually

                                }
                             }
                            else
                            {
                                connectionDetector.showConnectivityStatus();
                                drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
                            }
                    }

                });






    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.

        getMenuInflater().inflate(R.menu.menu_main, menu);



        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position) {
        displayView(position);
    }
    private void displayView(int position) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.nav_item_category1);
                break;
            case 1:
                fragment = new HomeFragment();
                title = getString(R.string.nav_item_category2);
                break;
            case 2:
                fragment = new HomeFragment();
                title = getString(R.string.nav_item_category3);
                break;
            case 3://sandeep block

                fragment = new ProductFragment();
                title = getString(R.string.nav_item_category4);
                break;


            default:
                break;
        }
        if (fragment != null) {
            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();

            // set the toolbar title
           // getSupportActionBar().setTitle(title);
        }
    }



}