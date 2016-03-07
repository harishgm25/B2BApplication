package com.example.harish.b2bapplication.activity;

import android.content.Context;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;


/**
 * Created by harish on 6/3/16.
 */
public class StoreAck {
    String filename = "acktoken";
    String string = "Hello world!";
    FileOutputStream outputStream;

    public void writeFile(FileOutputStream fos,String ack)
    {
        try {
            outputStream = fos;
            outputStream.write(string.getBytes());
            outputStream.close();
            Log.d(">>>>>>>>>>>>>>>>>","Ack in file");
        }catch (IOException e){e.printStackTrace();}


    }



}
