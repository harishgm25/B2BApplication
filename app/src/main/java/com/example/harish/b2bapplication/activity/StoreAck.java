package com.example.harish.b2bapplication.activity;

import android.content.Context;
import android.util.Log;
import android.view.View;

import com.example.harish.b2bapplication.R;
import android.view.LayoutInflater;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
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

    FileOutputStream outputStream;
    FileInputStream inputStream;
    View rootView;

    public void writeFile(Context c,String ack)
    {
        try {
            outputStream = c.openFileOutput(filename,c.MODE_PRIVATE);
            outputStream.write(ack.getBytes());
            outputStream.close();
            Log.d("<<<<<<<<<<<<<<<<<<<<<", "Ack in file");



        }catch (IOException e){e.printStackTrace();}


    }

    public String readFile(Context c)
    {
        try {
            inputStream = c.openFileInput(filename);
            BufferedReader buffer = new BufferedReader( new InputStreamReader(inputStream));
            String line= null;
            String temp = null;
            while((temp=buffer.readLine())!= null )
            {
                line = temp;
            }

            Log.d(line, ">>>>>>>>>>>>>>>>>>>>>>Ack in file");

            inputStream.close();
            return line;



        }catch (IOException e){

            e.printStackTrace();
            return null;

        }


    }



}
