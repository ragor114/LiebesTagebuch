package ur.mi.liebestagebuch.Notification;


import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import ur.mi.liebestagebuch.Settings.CheckEncryptionSettingHelper;
import ur.mi.liebestagebuch.LoginActivity;

public class Reminder extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("notification", "onrecieve");

        // Es wird überprüft, ob die Benachrichtungen aktiviert sind, bevor eine Notification gesendet wird.
        boolean remind = CheckEncryptionSettingHelper.encryptionActivated(context);

        // Die if-Abfrage überprüft, ob die Einstellung zur Erinnerung aktiviert wurde und schickt dann
        // zum ausgewählten Zeitpunkt die Notification an den User.
        if(remind){
            Log.d("notification", "Reminding ...");
            NotificationHelper notificationHelper =new NotificationHelper(context);
            NotificationCompat.Builder nb = notificationHelper.getChannelNotification();
            PendingIntent contentIntent = PendingIntent.getActivity(context, 0, new Intent(context, LoginActivity.class), PendingIntent.FLAG_UPDATE_CURRENT);
            nb.setContentIntent(contentIntent);
            notificationHelper.getManager();
            notificationHelper.getManager().notify(1, nb.build());

        } else{
            Log.d("notification", "Reminder deactivated");
        }
    }

}
