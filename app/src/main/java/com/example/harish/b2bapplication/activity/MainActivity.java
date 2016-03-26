package com.example.harish.b2bapplication.activity;
import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.model.NavDrawerItem;
import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.security.MessageDigest;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.http.entity.mime.content.FileBody;
import org.json.JSONObject;

import java.security.NoSuchAlgorithmException;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private ImageView _profileView = null;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;
    private Uri mImageUri;  // for temp image
    private File tempDir; // for temp dir for image
    private  DrawerLayout drawerLayout;
    private String s[];
    private UploadImageAsyncTask uploadImageAsyncTask;

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {

        super.onPrepareOptionsMenu(menu);
        return true;

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
        StrictMode.setThreadPolicy(policy);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
            mToolbar = (Toolbar) findViewById(R.id.toolbar);
            setSupportActionBar(mToolbar);
            getSupportActionBar().setDisplayShowHomeEnabled(true);
            s = new String[5];


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
            _profileView = (ImageView)findViewById(R.id.circleView);

            _profileView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
                    drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
                    if (connectionDetector.isConnectingToInternet())
                    {
                        _profileView.setEnabled(false);
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
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onDrawerItemSelected(View view, int position,List menu) {
        NavDrawerItem navDrawerItem = (NavDrawerItem) menu.get(7); // manually overriding and getting NavDrawerItem List
        String title = navDrawerItem.getTitle();
        displayView(position, title);                   // Getting the toggle title for SignIn or LogOut

    }
    private void displayView(int position, String toggletitle) {
        Fragment fragment = null;
        String title = getString(R.string.app_name);
        switch (position) {
            case 0:
                getUserHome();
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
                fragment = new SettingFragment();
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
            // fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container_body, fragment);
            fragmentTransaction.commit();
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

    public void getUserHome()
    {
        s = new StoreAck().readFile(getApplicationContext().getApplicationContext());
        ConnectionDetector connectionDetector = new ConnectionDetector(getApplicationContext());
        DrawerLayout drawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        Fragment userFragment = null;
        if (connectionDetector.isConnectingToInternet())
        {

            if (s == null)
                userFragment = new HomeFragment();
            else {
                if (s[3].equals("Manufacture"))
                    userFragment = new ManufactureFragment();
                if (s[3].equals("WholeSeller"))
                    userFragment = new WholeSalerFragment();
                if (s[3].equals("Retailer"))
                    userFragment = new RetailerFragment();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.replace(R.id.container_body, userFragment);
            fragmentTransaction.commit();
            return;
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
                fragmentTransaction.replace(R.id.container_body, p);
                fragmentTransaction.commit();
                drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually


        }
        else
        {
            connectionDetector.showConnectivityStatus();
            drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually
        }


    }
   // Getting the Image form  Camera or Gallery by Calling Camera startactivtyforresult and onactivityresult----------------------
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
                   File photo = null;
                   try {

                   } catch (Exception e) {
                       Toast.makeText(getApplicationContext(), "Can't create File", Toast.LENGTH_LONG).show();

                   }
                   intent.putExtra(MediaStore.EXTRA_OUTPUT, mImageUri);
                   startActivityForResult(intent, CAMERA_REQUEST);

               } else if (items[item].equals("Choose from Library")) {
                   Intent intent = new Intent(
                           Intent.ACTION_PICK,
                           android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
                   intent.setType("image/*");
                   startActivityForResult(intent, SELECT_FILE);
               } else if (items[item].equals("Cancel")) {
                   dialog.dismiss();
                   drawerLayout.closeDrawer(Gravity.LEFT);
               }
           }

       });
       builder.show();
   }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        Bitmap bitmap;

       if (resultCode == RESULT_OK) {   // For Camera
            if (requestCode == CAMERA_REQUEST) {

                Bitmap thumb = (Bitmap)data.getExtras().get("data"); // gettting Thumb Image
                new UploadImageAsyncTask(MainActivity.this,thumb).execute();

            }
           if (requestCode == SELECT_FILE) {
               Uri selectedImage = data.getData();
               String[] filePathColumn = { MediaStore.Images.Media.DATA };
               Cursor cursor = getContentResolver().query(selectedImage,
               filePathColumn, null, null, null);
               cursor.moveToFirst();
               int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
               String imgDecodableString = cursor.getString(columnIndex);
               cursor.close();
               bitmap = BitmapFactory.decodeFile(imgDecodableString);
               new UploadImageAsyncTask(MainActivity.this,bitmap).execute();

           }
       }
        super.onActivityResult(requestCode, resultCode, data);
    }


    private class UploadImageAsyncTask extends AsyncTask<Void,Void,Void>
    {
        Bitmap b;
        ProgressDialog dialog;
        UploadImageAsyncTask(MainActivity mainActivity, Bitmap b)
        {
            this.b =b;
            dialog = new ProgressDialog(mainActivity);
        }
        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            _profileView.setImageBitmap(b);
             dialog.dismiss();
            _profileView.setEnabled(true);
            drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            dialog.setCancelable(false);
            dialog.setIndeterminate(false);
            dialog.setMessage("Uploading Image");
            timerDelayRemoveDialog(50000,dialog);
            dialog.show();
        }

        @Override
        protected Void doInBackground(Void... params) {

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            b.compress(Bitmap.CompressFormat.JPEG,100, bytes);
            if(new StoreAck().writeProfile(getApplicationContext(), bytes)) // writing  Image to a file
                updateProfileImg(); // http post request to update profile image
            return null;
        }
    }

    public  void updateProfileImg()
    {
        Log.d("TAG", "Update Pofile");

        // For updating profile Image captured by Camera or Gallery in file and Server------------------------------------

        File file = new File(getApplicationContext().getFilesDir() + "/" + "profileImg.jpg");
        FileBody fb = new FileBody(file,"image/jpg");
        String[] ip = getApplicationContext().getResources().getStringArray(R.array.ip_address);
        s = new StoreAck().readFile(getApplicationContext().getApplicationContext());
        String ack = s[0];
        String userid = s[1];

        RequestParams params = new RequestParams();
        try {
            Log.i("postImage", "Image: " + file);
            params.put("profile[profileImg]", file );
            params.put("profile[id]", userid);


        } catch (Exception e) {
            e.printStackTrace();
        }
        SyncHttpClient client = new SyncHttpClient();
        client.setTimeout(30 * 10000);

        client.addHeader("Authorization", "Token token=\"" + ack + "\"");
        client.post(ip[0] + "api/v1/profiles/updateprofile", params, new JsonHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                Log.w("async", "success!!!!");
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Profile Image Updated", Toast.LENGTH_LONG).show();
                    }
                });
            }
            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                Log.e("async", responseString);
                runOnUiThread(new Runnable() {
                    public void run() {
                        Toast.makeText(MainActivity.this, "Profile Image Updated", Toast.LENGTH_LONG).show();
                    }
                });


            }
        });
    }
    //----------------Closing the ProgressDialog after given interval-------------------
    public void timerDelayRemoveDialog(long time, final Dialog d){

        android.os.Handler handler = new android.os.Handler();
        handler.postDelayed(new Runnable() {
            public void run() {
                if(d.isShowing()) {
                    d.dismiss();
                    Toast.makeText(MainActivity.this, "Seems taking to long", Toast.LENGTH_LONG).show();
                    drawerLayout.closeDrawer(Gravity.LEFT); // closing DrawerLayOut Manually

                }
            }
        },time);
    }

}




