package ur.mi.liebestagebuch.Notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;

import ur.mi.liebestagebuch.Settings.SettingsConfig;

public class Reminder extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("notification", "onrecieve");

        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        boolean remind = sharedPreferences.getBoolean(SettingsConfig.SWITCH_REMIND, false);

        if(remind){
            Log.d("notification", "Reminding ...");
            NotificationHelper notificationHelper =new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
            notificationHelper.getManager().notify(1, nb.build());
        } else{
            Log.d("notification", "Reminder deactivated");
        }
    }

}
