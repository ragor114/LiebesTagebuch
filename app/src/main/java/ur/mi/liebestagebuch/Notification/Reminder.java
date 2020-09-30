package ur.mi.liebestagebuch.Notification;

import android.app.AlarmManager;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

import ur.mi.liebestagebuch.R;

public class Reminder extends BroadcastReceiver{

    @Override
    public void onReceive(Context context, Intent intent) {
        Log.d("notification", "onrecieve");
        NotificationManager notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        Intent repeating_intent = new Intent(context, RepeatingActivity.class);
        repeating_intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);

        PendingIntent pendingIntent = PendingIntent.getActivity(context, 100, repeating_intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "notification")
                .setSmallIcon(R.drawable.android10logo)
                .setContentIntent(pendingIntent)
                //.setContentTitle(R.string.notification_title)
                //.setContentText(R.string.notification_text)
                .setContentTitle("Dear Diary...")
                .setContentText("Don't forget to write a diary entry today!")
                .setAutoCancel(false)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);

        notificationManager.notify(200, builder.build());
    }

   /* public void setService (Context context) {
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        Intent i = new Intent(context, Reminder.class);
        PendingIntent pi = PendingIntent.getBroadcast(context, 0, i, 0);
        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, System.currentTimeMillis(), 1000 * 60 * 60 *24, pi); // Millisekunden * Sekunden * Minuten
    }

    public boolean cancelService(Context context) {
        Intent intent = new Intent(context, Reminder.class);
        PendingIntent sender = PendingIntent.getBroadcast(context, 0, intent, 0);
        AlarmManager alarmManager = (AlarmManager) context.getSystemService(Context.ALARM_SERVICE);
        alarmManager.cancel(sender);

        return true;
    }
    */

}
