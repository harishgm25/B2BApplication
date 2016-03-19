package com.example.harish.b2bapplication.activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.adapter.NavigationDrawerAdapter;
import com.example.harish.b2bapplication.model.NavDrawerItem;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.security.MessageDigest;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.widget.ImageView;

import java.security.NoSuchAlgorithmException;
import java.util.List;

public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Button _sign = null;
    private String  signstatus;
    private ImageView _profileView = null;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

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

          //  _sign = (Button) findViewById(R.id.btn_sign);

            _profileView = (ImageView)findViewById(R.id.circleView);

            _profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                    DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (connectionDetector.isConnectingToInternet())
                    {

                          getImageProfile();
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
    public void onDrawerItemSelected(View view, int position,List menu) {

        NavDrawerItem navDrawerItem = (NavDrawerItem) menu.get(7); // manually overriding and getting NavDrawerItem List
        String title = navDrawerItem.getTitle();
        displayView(position,title);                   // Getting the toggle title for SignIn or LogOut

    }
    private void displayView(int position, String toggletitle) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                fragment = new HomeFragment();
                title = getString(R.string.nav_item_home);
                break;
            case 1:
                    if(toggletitle.equals("SIGNIN"))
                    {
                        signinout(toggletitle);
                    }
                    else
                    {
                    callProfileFragment(toggletitle);
                    title = getString(R.string.nav_item_profile);
                     }
                break;
            case 2:
                fragment = new HomeFragment();
                title = getString(R.string.nav_item_accountsetting);
                break;
            case 3://sandeep block

                fragment = new ProductFragment();
                title = toggletitle;
                break;

            case 7:

                    signinout(toggletitle);
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


    public void signinout(String title)
    {
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (connectionDetector.isConnectingToInternet())
        {
            if (title.equals("LOGOUT")) {
                new StoreAck().DeleteFile(getApplicationContext());
                _profileView.setImageResource(R.drawable.ic_action_profile); // setting the default image
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

    public void callProfileFragment(String title)
    {
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (connectionDetector.isConnectingToInternet())
        {
                ProfileFragment p = new ProfileFragment();
                Bundle bundles = new Bundle();
                FragmentManager fragmentManager = getSupportFragmentManager();
                FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                fragmentTransaction.addToBackStack(null);
                fragmentTransaction.replace(R.id.container_body,p);
                fragmentTransaction.commit();
                drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually


        }
        else
        {
            connectionDetector.showConnectivityStatus();
            drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
        }


    }

   public void getImageProfile()
   {
       final CharSequence[] items = { "Take Photo", "Choose from Library", "Cancel" };
       AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
       builder.setTitle("Add Photo!");
       builder.setItems(items, new DialogInterface.OnClickListener() {
           @Override
           public void onClick(DialogInterface dialog, int item) {
               if (items[item].equals("Take Photo")) {
                   Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                   startActivityForResult(intent, CAMERA_REQUEST);
               } else if (items[item].equals("Choose from Library")) {
                   Intent intent = new Intent(
                           Intent.ACTION_PICK,
                           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   intent.setType("image/*");
                   startActivityForResult(
                           Intent.createChooser(intent, "Select File"),SELECT_FILE);
               } else if (items[item].equals("Cancel")) {
                   dialog.dismiss();
               }
           }
       });
       builder.show();
   }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (requestCode == CAMERA_REQUEST) {
                Bitmap thumbnail = (Bitmap) data.getExtras().get("data");
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.JPEG,90, bytes);
                new StoreAck().writeProfile(getApplicationContext(), bytes);
                _profileView.setImageBitmap(thumbnail);

            } else if (requestCode == SELECT_FILE) {
                Uri selectedImageUri = data.getData();
                String[] projection = {MediaStore.MediaColumns.DATA};
                CursorLoader cursorLoader = new CursorLoader(this, selectedImageUri, projection, null, null,
                        null);
                Cursor cursor = cursorLoader.loadInBackground();
                int column_index = cursor.getColumnIndexOrThrow(MediaStore.MediaColumns.DATA);
                cursor.moveToFirst();
                String selectedImagePath = cursor.getString(column_index);

                Bitmap bm;
                BitmapFactory.Options options = new BitmapFactory.Options();
                options.inJustDecodeBounds = true;
                BitmapFactory.decodeFile(selectedImagePath, options);
                final int REQUIRED_SIZE = 200;
                int scale = 1;
                while (options.outWidth / scale / 2 >= REQUIRED_SIZE
                        && options.outHeight / scale / 2 >= REQUIRED_SIZE)
                    scale *= 2;
                options.inSampleSize = scale;
                options.inJustDecodeBounds = false;
                bm = BitmapFactory.decodeFile(selectedImagePath, options);
                _profileView.setImageBitmap(bm);
            }


        }
    }
}

