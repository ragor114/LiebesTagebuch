package ur.mi.liebestagebuch.Settings;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Switch;
import android.widget.TimePicker;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Calendar;

import ur.mi.liebestagebuch.Notification.Reminder;
import ur.mi.liebestagebuch.R;

public class SettingsActivity extends AppCompatActivity {

    private Switch sw_encrypt;
    public Switch sw_remind;
    private TimePicker reminder_picker;
    private LinearLayout password;
    private ImageButton passwordArrow;



    private boolean switchEncrypt;
    private boolean switchRemind;
    private int timeHour;
    private int timeMinute;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.settings_layout);

        sw_encrypt = findViewById(R.id.switch_encrypt);
        sw_remind = findViewById(R.id.switch_remind);
        reminder_picker = findViewById(R.id.reminder_picker);
        password = findViewById(R.id.change_password);
        passwordArrow = findViewById(R.id.password_arrow);

        reminder_picker.setIs24HourView(true);

        initListeners();

        load();
        update();
        toggleTimePicker();

    }

    //Initialisiere die OnClickListener für die verschiedenen Einstellungsmöglichkeiten
    private void initListeners(){
        sw_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEntriesDeOrEncryption();
            }
        });

        sw_remind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                save();
            }
        });

        reminder_picker.setOnTimeChangedListener(new TimePicker.OnTimeChangedListener() {
            @Override
            public void onTimeChanged(TimePicker view, int hourOfDay, int minute) {
                save();
                Log.println(Log.DEBUG,"DB","Time Changed");
            }
        });

        password.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.println(Log.DEBUG,"DB","Password Button Pressed");
                startPasswordChangeActivity();
            }
        });

        passwordArrow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.println(Log.DEBUG,"DB","Password Button Pressed");
                startPasswordChangeActivity();
            }
        });
    }

    private void startEntriesDeOrEncryption() {
        if(!sw_encrypt.isChecked()){
            Intent intent = new Intent(SettingsActivity.this, DisableEncryptionActivity.class);
            startActivityForResult(intent, getResources().getInteger(R.integer.decryption_request_code));
        } else{
            Intent intent = new Intent(SettingsActivity.this, EnableEncryptionActivity.class);
            startActivityForResult(intent, getResources().getInteger(R.integer.encryption_request_code));
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            if(requestCode == getResources().getInteger(R.integer.decryption_request_code)){
                Bundle extras = data.getExtras();
                if(extras != null){
                    boolean decrypted = extras.getBoolean(getString(R.string.has_decrypted_key));
                    sw_encrypt.setChecked(false);
                    save();
                } else {
                    sw_encrypt.setChecked(true);
                    save();
                }
            } else if(requestCode == getResources().getInteger(R.integer.encryption_request_code)){
                Bundle extras = data.getExtras();
                if(extras != null){
                    sw_encrypt.setChecked(true);
                    save();
                } else{
                    sw_encrypt.setChecked(false);
                    save();
                }
            }
        } else if(resultCode == RESULT_CANCELED){
            sw_encrypt.setChecked(!sw_encrypt.isChecked());
        }
    }

    private void startPasswordChangeActivity() {
        Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
        startActivityForResult(intent, 12345);
    }

    //Speichert die vom User gewählten Einstellungen in Shared Preferences
    private void save(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(getString(R.string.encrypt_setting),sw_encrypt.isChecked());
        editor.putBoolean(getString(R.string.remind_setting),sw_remind.isChecked());
        editor.putInt(getString(R.string.remind_hour),reminder_picker.getHour());
        editor.putInt(getString(R.string.remind_minute),reminder_picker.getMinute());

        Log.println(Log.DEBUG,"DB","Preferences saved");

        editor.apply();
        toggleTimePicker();

    }

    //Lädt die vom User gewählten Einstellungen aus den Shared Preferences
    public void load(){
        SharedPreferences sharedPreferences = getSharedPreferences(getString(R.string.shared_prefs_name), MODE_PRIVATE);
        switchEncrypt = sharedPreferences.getBoolean(getString(R.string.encrypt_setting),true);
        switchRemind = sharedPreferences.getBoolean(getString(R.string.remind_setting),false);
        timeHour = sharedPreferences.getInt(getString(R.string.remind_hour),18);
        timeMinute = sharedPreferences.getInt(getString(R.string.remind_minute),0);
    }

    //Überschreibt die Shared Preferences falls der User in den Einstellungen etwas geändert hat
    public void update(){
        sw_encrypt.setChecked(switchEncrypt);
        sw_remind.setChecked(switchRemind);
        reminder_picker.setHour(timeHour);
        reminder_picker.setMinute(timeMinute);
    }

    //Zeigt bzw versteckt den Time Picker je nachdem ob der User Benachrichtigungen erhalten möchte
    private void toggleTimePicker(){
        if(sw_remind.isChecked()){
            reminder_picker.setVisibility(View.VISIBLE);
            setNotificationTime();
        }else{
            reminder_picker.setVisibility(View.GONE);
        }
    }

    private void setNotificationTime() {
        Calendar calendar = Calendar.getInstance();

        calendar.set(Calendar.HOUR_OF_DAY, reminder_picker.getHour());
        calendar.set(Calendar.MINUTE, reminder_picker.getMinute());
        calendar.set(Calendar.SECOND, 0);

        AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);

        Intent intent = new Intent(SettingsActivity.this, Reminder.class);
        PendingIntent pendingIntent = PendingIntent. getBroadcast(SettingsActivity.this, 1, intent, 0);


        alarmManager.setRepeating(AlarmManager.RTC_WAKEUP, calendar.getTimeInMillis(), AlarmManager.INTERVAL_DAY, pendingIntent);
    }
}
