package com.example.harish.b2bapplication.activity;

import android.content.Context;
import android.content.ContextWrapper;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;
import android.view.View;

import com.example.harish.b2bapplication.R;
import android.view.LayoutInflater;

import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;


/**
 * Created by harish on 6/3/16.
 */
public class StoreAck {
    String filename = "acktoken";
    String profileImg = "profileImg.jpg";
    FileOutputStream outputStream;
    FileInputStream inputStream;
    View rootView;

    public void writeFile(Context c, JSONObject jObj)
    {
        try {
            String ack = jObj.getString("token");
            String userid = jObj.getString("userid");
            String  email = jObj.getString("email");
            String roll = jObj.getString("roll");
            String mobile = jObj.getString("mobile");

            outputStream = c.openFileOutput(filename, c.MODE_PRIVATE);
            outputStream.write(ack.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(userid.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(email.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(roll.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.write(mobile.getBytes());
            outputStream.write("\n".getBytes());
            outputStream.close();
            Log.d("<<<<<<<<<<<<<<<<<<<<<", "Ack in file");



        }catch (Exception e){e.printStackTrace();}


    }

    public String[] readFile(Context c)
    {
        try {
            inputStream = c.openFileInput(filename);
            BufferedReader buffer = new BufferedReader( new InputStreamReader(inputStream));
            String line  [] = new String[6];
            String temp = null;
            int i = 0;
            while((temp=buffer.readLine())!= null )
            {
                line[i] = temp;
                i++;
            }

            Log.d(line.toString(), ">>>>>>>>>>>>>>>>>>>>>>Ack in file");

            inputStream.close();
            return line;



        }catch (IOException e){

            e.printStackTrace();
            return null;

        }


    }

    public void DeleteFile(Context c)
    {


            boolean flag = c.deleteFile(filename);
                           c.deleteFile(profileImg);
            if (flag == true) {
                Log.d("true", ">>>>>>>>>>>>>>>>>>>>>>Ack in deleted");
            } else {
                Log.d("false", ">>>>>>>>>>>>>>>>>>>>>>Ack in deleted");
            }

    }

    // thumb image
    public boolean writeProfile(Context c,ByteArrayOutputStream bytes)
    {

        try {
            outputStream = c.openFileOutput(profileImg, c.MODE_PRIVATE);
            outputStream.write(bytes.toByteArray());
            outputStream.close();
            Log.d("<<<<<<<<<<<<<<<<<<<<<", "ProfileImg in file");

            return true;


        }catch (Exception e){e.printStackTrace();
            return false;
        }

    }

    public Bitmap readProfile(Context c) {
        Bitmap bitmap=null;
        try {
            inputStream = c.openFileInput(profileImg);
            bitmap = BitmapFactory.decodeStream(inputStream);
            inputStream.close();

        } catch (IOException e) {

            e.printStackTrace();
            return null;

        }
        return  bitmap;
    }

   // writing the original file and need to push to the server
    public File writeProfileOrignalImage(Context c)
    {

        try {
            ContextWrapper cw = new ContextWrapper(c);
            File directory = cw.getDir("media", c.MODE_PRIVATE);
            return directory;

        }catch (Exception e){e.printStackTrace();
            return null;
        }

    }



}
