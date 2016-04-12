package com.example.harish.b2bapplication.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;

/**
 * Created by harish on 4/4/16.
 */
public class NotificationAlaramService extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {

        Bundle extras = intent.getExtras();
        Intent dailyUpdater = new Intent(context, NotificationService.class);
        dailyUpdater.putExtra("ack", extras.getString("ack"));
        dailyUpdater.putExtra("userid",extras.getString("userid"));

        context.startService(dailyUpdater);
        Log.d("AlarmReceiver", "Called context.startService from AlarmReceiver.onReceive");
    }
}
