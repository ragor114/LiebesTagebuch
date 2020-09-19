package ur.mi.liebestagebuch;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.biometric.BiometricManager;
import androidx.biometric.BiometricPrompt;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.KeyguardManager;
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

import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ur.mi.liebestagebuch.Encryption.SecurePasswordSaver;
import ur.mi.liebestagebuch.GridView.GridActivity;

public class LoginActivity extends AppCompatActivity {

    /*
     * Activity für Login mit Fingerabdruck, oder falls kein Fingerabdruck vorhanden, oder fehlerhaft, mittels Passwort
     *
     * Entwickelt von Moritz Schnell
     *
     * TODO: Login-Funktionalität (also alles)
     *
     * Quellen:
     * https://www.youtube.com/watch?v=e49DvaJ1IX4&t=931s
     * https://developer.android.com/reference/android/Manifest.permission
     */

    private SharedPreferences prefs = null;
    private TextView loginFingerprintText;
    private Button loginButton;
    private Button okButton;
    private EditText editTextPassword ;
    public static String correctPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prefs = getSharedPreferences("ur.mi.liebestagebuch", MODE_PRIVATE);


        loginFingerprintText = (TextView) findViewById(R.id.loginFingerprintText);
        okButton = findViewById(R.id.ok_button);
        editTextPassword = findViewById(R.id.edit_password);

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.d("login", "clicked not firsttime");
                String storedPassword = SecurePasswordSaver.getStoredPassword(getApplicationContext());
                Log.d("login", "Stored password: " + storedPassword);
                if(storedPassword.equals(editTextPassword.getText().toString())){
                    Log.d("login", "Password correct");
                    loginSuccess();
                } else{
                    loginFingerprintText.setText(R.string.wrong_password);
                }
            }
        });


        if (prefs.getBoolean("firstrun", true)){
            Log.d("login", "Is firstrun");
            okButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    storePassword();
                }
            });
            prefs.edit().putBoolean("firstrun", false).commit();
        }
    }

    protected void onResume(){
        super.onResume();

        //Nutzen eines BiometricManagers um zu schauen ob der nutzer zugriff auf fingerabdrücke hat
        final BiometricManager biometricManager = BiometricManager.from(this);
        switch(biometricManager.canAuthenticate()){
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
        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                biometricPrompt.authenticate(promptInfo);
                //Intent switchActivityIntent = new Intent(LoginActivity.this, GridActivity.class);
                //startActivity(switchActivityIntent);
            }
        });
    }

    private void loginSuccess() {
        Toast.makeText(getApplicationContext(), "Login Success!", Toast.LENGTH_SHORT).show();
        correctPassword = SecurePasswordSaver.getStoredPassword(this);
        Intent switchActivityIntent = new Intent(LoginActivity.this, GridActivity.class);
        startActivity(switchActivityIntent);
    }

    private void storePassword() {
        if (!editTextPassword.getText().toString().equals("")) {
            SecurePasswordSaver.storePasswordSecure(editTextPassword.getText().toString(), this);
            editTextPassword.setText("");
            Log.d("login", "clicked first time");
            loginSuccess();
        }
    }
    
}

