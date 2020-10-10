package ur.mi.liebestagebuch;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlarmManager;
import android.app.KeyguardManager;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.hardware.fingerprint.FingerprintManager;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ur.mi.liebestagebuch.Encryption.SecurePasswordSaver;
import ur.mi.liebestagebuch.GridView.DateUtil;
import ur.mi.liebestagebuch.GridView.GridActivity;
import ur.mi.liebestagebuch.Notification.Reminder;
import ur.mi.liebestagebuch.Settings.SettingsActivity;

public class LoginActivity extends AppCompatActivity {

    /*
     * Activity für Login mit Fingerabdruck, oder falls kein Fingerabdruck vorhanden, oder fehlerhaft, mittels Passwort
     *
     * Entwickelt von Moritz Schnell
     *
     * Check: Login-Funktionalität (also alles)
     *
     *
     * Quellen:
     * https://www.youtube.com/watch?v=e49DvaJ1IX4&t=931s
     * https://developer.android.com/reference/android/Manifest.permission
     */

    private SharedPreferences prefs = null;
    private TextView loginFingerprintText;
    private Button loginButton;
    private Button okButton;
    private EditText editTextPassword;
    private boolean isFirstRun;
    public Date installationDate;


    public static String correctPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        isFirstRun = false;

        prefs = getSharedPreferences("ur.mi.liebestagebuch", MODE_PRIVATE);


        loginFingerprintText = (TextView) findViewById(R.id.loginFingerprintText);
        okButton = findViewById(R.id.ok_button);
        editTextPassword = findViewById(R.id.edit_password);


        //OnClickListener auf dem "okButton" überprüft ob das vom Nutzer eingegebene Passwort mit
        //dem, bei der Installation festgelegten, Passwort übereinstimmt.
        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("login", "clicked not firsttime");
                String storedPassword = SecurePasswordSaver.getStoredPassword(getApplicationContext());
                Log.d("login", "Stored password: " + storedPassword);
                if (storedPassword.equals(editTextPassword.getText().toString())) {
                    Log.d("login", "Password correct");
                    loginSuccess();
                } else {
                    loginFingerprintText.setText(R.string.wrong_password);
                }
            }
        });


        //Wenn die App zum ersten Mal gestartet wird, muss zum einloggen ein Passwort festgelegt werden
        //welches nacher zum einloggen ohne Fingerabdruck genutzt wird.
        if (prefs.getBoolean("firstrun", true)) {
            isFirstRun = true;
            Log.d("login", "Is firstrun");
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storePassword();
                    saveInstallationDate();
                }
            });
            prefs.edit().putBoolean("firstrun", false).commit();

        }
    }


    protected void onResume() {
        super.onResume();

        //Nutzen eines BiometricManagers um zu schauen ob der nutzer zugriff auf Fingerabdrücke hat
        final BiometricManager biometricManager = BiometricManager.from(this);
        switch (biometricManager.canAuthenticate()) {
            case BiometricManager.BIOMETRIC_SUCCESS:

                loginFingerprintText.setText(R.string.login_fingerprint_text);

                break;

            case BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE:

                loginFingerprintText.setText(R.string.login_password_text);

                break;

            case BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE:

                loginFingerprintText.setText(R.string.login_fingerprint_hw_unavailable);

                break;

            case BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED:

                loginFingerprintText.setText(R.string.login_fingerprint_no_fingerprint);

                break;

        }

        Executor executor = ContextCompat.getMainExecutor(this);

        final BiometricPrompt biometricPrompt = new BiometricPrompt(LoginActivity.this, executor, new BiometricPrompt.AuthenticationCallback() {
            @Override
            public void onAuthenticationError(int errorCode, @NonNull CharSequence errString) {
                super.onAuthenticationError(errorCode, errString);
            }

            @Override
            public void onAuthenticationSucceeded(@NonNull BiometricPrompt.AuthenticationResult result) {
                super.onAuthenticationSucceeded(result);
                loginSuccess();
            }

            @Override
            public void onAuthenticationFailed() {
                super.onAuthenticationFailed();
            }
        });

        final BiometricPrompt.PromptInfo promptInfo = new BiometricPrompt.PromptInfo.Builder()
                .setTitle("Login")
                .setDescription("Use your fingerprint to login to your Diary")
                .setNegativeButtonText("Cancel")
                .build();


        //Der "loginButton" startet die biometrische Authehtifizierung um sich via Fingerabdruck anzumelden.
        //Dies geschieht allerdings nur, wenn die App bereits gestartet wurde und ein Passwort vorliegt.
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!isFirstRun) {
                    biometricPrompt.authenticate(promptInfo);
                    //Intent switchActivityIntent = new Intent(LoginActivity.this, GridActivity.class);
                    //startActivity(switchActivityIntent);
                } else {
                    loginFingerprintText.setText(R.string.set_password_first);
                }
            }
        });

    }


    //Wenn der Fingerabdruckscan erfolgreich war, wird der Nutzer eingelogt und in die GridActivity weitergeleitet.
    private void loginSuccess() {
        Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
        correctPassword = SecurePasswordSaver.getStoredPassword(this);
        Intent switchActivityIntent = new Intent(LoginActivity.this, GridActivity.class);
        startActivityForResult(switchActivityIntent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == 100){
            finish();
        }
    }

    //Mit dieser Methode wird beim ersten anmelden das eingegebene Passwort in die Datenbank gespeichert.
    private void storePassword() {
        if (!editTextPassword.getText().toString().equals("")) {
            SecurePasswordSaver.storePasswordSecure(editTextPassword.getText().toString(), this);
            editTextPassword.setText("");
            Log.d("login", "clicked first time");
            loginSuccess();
        }
    }

    //Diese Methode legt beim ersten einloggen in die App eine Datei mit dem Datum des ersten Starts an
    public void saveInstallationDate() {

        installationDate = new Date();
        Calendar c = Calendar.getInstance();
        //c.add(Calendar.DAY_OF_MONTH, -5);
        installationDate = c.getTime();
        installationDate = DateUtil.setToMidnight(installationDate);
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        String dateString = sdf.format(installationDate);

        Log.d("Date", "InstallationDate in saveInstallation is: " + dateString);

        File installationFile = new File(this.getDir("date", MODE_PRIVATE), "installationDate");
        if (installationFile.exists()) {
            installationFile.delete();
        }
        try {
            installationFile.createNewFile();
            BufferedWriter writer = new BufferedWriter(new FileWriter(installationFile));
            writer.write(dateString);
            writer.flush();
            writer.close();
            Log.d("Date", "Created new File " + installationFile.getPath());
        } catch (IOException e) {
            Log.d("Date", "IOExeption creating File in saveInstallationDate()");
            e.printStackTrace();
        }
    }


}
