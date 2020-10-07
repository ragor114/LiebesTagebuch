package ur.mi.liebestagebuch.Settings;

import android.content.Context;
import android.content.SharedPreferences;

public class CheckEncryptionSettingHelper {

    public static boolean encryptionActivated(Context context){
        SharedPreferences sharedPreferences = context.getSharedPreferences(SettingsConfig.SHARED_PREFS, Context.MODE_PRIVATE);
        return sharedPreferences.getBoolean(SettingsConfig.SWITCH_ENCRYPT, true);
    }

}
