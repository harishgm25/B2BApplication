package com.example.harish.b2bapplication.activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.StrictMode;
import android.preference.PreferenceActivity;
import android.provider.MediaStore;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Base64;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import com.example.harish.b2bapplication.R;
import com.example.harish.b2bapplication.model.NavDrawerItem;
import com.loopj.android.http.AsyncHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.SyncHttpClient;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.security.MessageDigest;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.widget.ImageView;
import android.widget.Toast;
import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.StringEntity;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.ByteArrayBody;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.DefaultedHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;
import org.apache.http.entity.ContentType;

import org.json.JSONException;
import org.json.JSONObject;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import cz.msebera.android.httpclient.Header;


public class MainActivity extends AppCompatActivity implements FragmentDrawer.FragmentDrawerListener {

    private Toolbar mToolbar;
    private FragmentDrawer drawerFragment;
    private Button _sign = null;
    private String  signstatus;
    private ImageView _profileView = null;
    private ProgressDialog progressdialog;
    private static final int CAMERA_REQUEST = 1888;
    private static final int SELECT_FILE = 2000;

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
        displayView(position, title);                   // Getting the toggle title for SignIn or LogOut

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
                thumbnail.compress(Bitmap.CompressFormat.JPEG, 90, bytes);

                if(new StoreAck().writeProfile(getApplicationContext(), bytes)) // writing Image to a file
                    updateProfileImg(); // http post request to update profile image
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

    public  void updateProfileImg()
    {
        Log.d("TAG", "Update Pofile");
     /*   if (!validate()) {
            onProfileUpdateFailed("Validation Failed");
            return;
        }*/

        progressdialog = new ProgressDialog(MainActivity.this);
        progressdialog.setIndeterminate(false);
        progressdialog.setMessage("Updating Profile Image");
        progressdialog.show();


        // for updating profile Image
        File file = new File(getApplicationContext().getFilesDir() + "/" + "profileImg.jpg");
        FileBody fb = new FileBody(file,"image/jpg");
        String[] ip = getApplicationContext().getResources().getStringArray(R.array.ip_address);
        String s[] = new StoreAck().readFile(getApplicationContext().getApplicationContext());
        String ack = s[0];
        String userid = s[1];

        RequestParams params = new RequestParams();
        try {
           /* Bitmap bitmap = BitmapFactory.decodeFile(file.toString());
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, stream);
            byte [] byte_arr = stream.toByteArray();
            String image_str = Base64.encodeToString(byte_arr, Base64.DEFAULT);*/

            Log.i("postImage", "Image: " + file);
            params.put("profile[profileImg]", file  );
            params.put("profile[id]", userid);


        } catch (Exception e) {
            e.printStackTrace();
        }
        SyncHttpClient client = new SyncHttpClient();

        client.addHeader("Authorization", "Token token=\"" + ack + "\"");
        client.post(ip[0] + "api/v1/profiles/updateprofile", params,new AsyncHttpResponseHandler() {

            @Override
            public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                Log.w("async", "success!!!!");
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                Log.e("async", "failure!");
            }


        });


        // TODO: Implement your own signup logic here.

                            /*holder.put("profileImg",entity);
                            holder.put("id", userid);
                            userObj.put("profile", holder);*/


                           /*
                            HttpPost httpPost = new HttpPost(ip[0] + "api/v1/profiles/updateprofile");
                            httpPost.setEntity(entity);
                            httpPost.setEntity(new StringEntity(userObj.toString()));
                            httpPost.addHeader("Authorization", "Token token=\"" + ack + "\"");
                            httpPost.setHeader("Accept", "application/json");
                            httpPost.setHeader("Content-type", "application/json");
                            HttpResponse response = new DefaultHttpClient().execute(httpPost,new BasicHttpContext());
                            Log.d("Http Post Response:", response.toString());
                            String json = EntityUtils.toString(response.getEntity());
                            temp1 = new JSONObject(json);
                            Log.d("Response status >>>>>>>", temp1.toString()); */


    }

    public void onProfileImgUpdateSuccess()
    {
        Toast.makeText(MainActivity.this, "Profile Updated", Toast.LENGTH_LONG).show();
        getFragmentManager().popBackStackImmediate();

    }
    public  void onProfileImgUpdateFailed(String msg)
    {
        Toast.makeText(MainActivity.this, msg, Toast.LENGTH_LONG).show();
        getFragmentManager().popBackStackImmediate();

    }
}

