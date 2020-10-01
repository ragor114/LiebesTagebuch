package ur.mi.liebestagebuch.Notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;

public class Reminder extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("notification", "onrecieve");

        NotificationHelper notificationHelper =new NotificationHelper(context);
        NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
        notificationHelper.getManager().notify(1, nb.build());

    }

}
