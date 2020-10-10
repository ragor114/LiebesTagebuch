package ur.mi.liebestagebuch.Settings;

import android.content.Context;
import android.content.SharedPreferences;

import ur.mi.liebestagebuch.R;

public class CheckEncryptionSettingHelper {

    /*
     * Diese Klasse stellt eine statische Methode zur Verfügung, um die oft nötige Abfrage nach der
     * Einstellung bezüglich Notifications zu kapseln.
     *
     * Entwickelt von Jannik Wiese.
     */

    public static boolean encryptionActivated(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(context.getString(R.string.shared_prefs_name), Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(context.getString(R.string.encrypt_setting), true);
    }

}
