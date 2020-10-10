package ur.mi.liebestagebuch.Notification;

import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.ContextWrapper;
import android.content.Intent;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import ur.mi.liebestagebuch.LoginActivity;
import ur.mi.liebestagebuch.R;

public class NotificationHelper extends ContextWrapper {
    public static final String notificationChannelID = "notificationChannelID";
    public static final String notificationChannelName = "Notification Channel";

    private NotificationManager mManager;

    //TODO: Pending intent

    //Der NotificationHelper überprüft ob die Android Version des Nutzers höher oder gleich Oreo ist
    //und ruft in dem Fall dass es so ist, die Methode createChannels auf.
    public NotificationHelper(Context base) {
        super(base);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createChannels();
        }

    }

    //Die Methode createChannels erstellt den ab Android Oreo benötigten Notification Channel um die
    //Notification zu übermitteln.
    @TargetApi(Build.VERSION_CODES.O)
    public void createChannels() {
        NotificationChannel notificationChannel = new NotificationChannel(notificationChannelID, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT);
        notificationChannel.enableLights(true);
        notificationChannel.enableVibration(true);
        notificationChannel.setLightColor(R.color.colorPrimary);
        notificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PRIVATE);

        getManager().createNotificationChannel(notificationChannel);
    }

    //Mit dieser Methode wird ein NotificationManager erstellt und übergeben, der für die Übertragung von
    //von Notifications benötigt wird.
    public NotificationManager getManager() {
        if (mManager == null) {
            mManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        }

        return mManager;
    }

    //TODO: Verbessern notification icon
    //Dieser Builder erstellt die Finale Notification
    public NotificationCompat.Builder getChannelNotification() {
        return new NotificationCompat.Builder(getApplicationContext(), notificationChannelID)
                .setContentTitle("Liebes Tagebuch...")
                .setContentText("Don't forget to write a diary entry today!")
                .setSmallIcon(R.drawable.stift_png_24dpi);


    }


}
