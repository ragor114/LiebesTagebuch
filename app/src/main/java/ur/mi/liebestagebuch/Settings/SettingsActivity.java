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

    private static final String SHARED_PREFS = "sharedPrefs";
    private static final String SWITCH_ENCRYPT = "sw_encrypt";
    private static final String SWITCH_REMIND = "sw_remind";
    private static final String TIMEPICKER_HOUR = "time_hour";
    private static final String TIMEPICKER_MINUTE = "time_minute";

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

    private void initListeners(){
        sw_encrypt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startEntriesDeOrEncryption();
                save();
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
        //TODO: Activity starten, die Einträge ver- oder entschlüsselt und sich dann beendet.
    }

    private void startPasswordChangeActivity() {
        Intent intent = new Intent(SettingsActivity.this, ChangePasswordActivity.class);
        startActivityForResult(intent, 12345);
    }

    private void save(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();

        editor.putBoolean(SWITCH_ENCRYPT,sw_encrypt.isChecked());
        editor.putBoolean(SWITCH_REMIND,sw_remind.isChecked());
        editor.putInt(TIMEPICKER_HOUR,reminder_picker.getHour());
        editor.putInt(TIMEPICKER_MINUTE,reminder_picker.getMinute());

        Log.println(Log.DEBUG,"DB","Preferences saved");

        editor.apply();
        toggleTimePicker();

    }

    public void load(){
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        switchEncrypt = sharedPreferences.getBoolean(SWITCH_ENCRYPT,true);
        switchRemind = sharedPreferences.getBoolean(SWITCH_REMIND,false);
        timeHour = sharedPreferences.getInt(TIMEPICKER_HOUR,18);
        timeMinute = sharedPreferences.getInt(TIMEPICKER_MINUTE,0);
    }

    public void update(){
        sw_encrypt.setChecked(switchEncrypt);
        sw_remind.setChecked(switchRemind);
        reminder_picker.setHour(timeHour);
        reminder_picker.setMinute(timeMinute);
    }

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
