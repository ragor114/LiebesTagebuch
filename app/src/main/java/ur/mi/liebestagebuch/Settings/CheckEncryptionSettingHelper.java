package ur.mi.liebestagebuch.Settings;

import android.content.Context;
import android.content.SharedPreferences;

public class CheckEncryptionSettingHelper {

    /*
     * Diese Klasse stellt eine statische Methode zur Verfügung, um die oft nötige Abfrage nach der
     * Einstellung bezüglich Notifications zu kapseln.
     *
     * Entwickelt von Jannik Wiese.
     */

    public static boolean encryptionActivated(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SettingsConfig.SWITCH_ENCRYPT, true);
    }

}
