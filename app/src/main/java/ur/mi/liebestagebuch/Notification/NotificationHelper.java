package ur.mi.liebestagebuch.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.ContextWrapper;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ur.mi.liebestagebuch.R;

public class NotificationHelper extends ContextWrapper {
    public static final String notificationChannelID = "notificationChannelID";
    public static final String notificationChannelName = "Notification Channel";

    private NotificationManager mManager;


    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }
    }

    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(notificationChannelID, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(R.color.colorPrimary);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    public NotificationManager getManager(){
        if (mManager == null){
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }


    public NotificationCompat.Builder getChannelNotification(){
        return new NotificationCompat.Builder(getApplicationContext(),notificationChannelID)
                .setContentTitle("Dear Diary...")
                .setContentText("Don't forget to write a diary entry today!")
                .setSmallIcon(R.drawable.ic_notifications_active_black_24dp);
    }
}
