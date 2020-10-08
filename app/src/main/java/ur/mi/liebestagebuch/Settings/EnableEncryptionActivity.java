package ur.mi.liebestagebuch.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.LoginActivity;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;
import ur.mi.liebestagebuch.database.data.Entry;

public class EnableEncryptionActivity extends AppCompatActivity implements DatabaseListener, CryptoListener {

    private Button enableButton;
    private TextView encryptionRunningTv;

    private DBHelper dbHelper;
    private List<Entry> allEntries;
    private ArrayList<String> allContents;
    private int currentEntryPosition;
    private byte[] currentIv;
    private byte[] currentSalt;
    private Date currentDate;

    private boolean isRunning;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        isRunning = false;

        setContentView(R.layout.enable_encryption_activity);

        progressBar = findViewById(R.id.enable_spinner);
        progressBar.setVisibility(View.GONE);

        enableButton = findViewById(R.id.enable_encryption_button);
        encryptionRunningTv = findViewById(R.id.enable_encryption_text);
        encryptionRunningTv.setVisibility(View.INVISIBLE);
        enableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startReencryption();
            }
        });
    }

    private void startReencryption() {
        progressBar.setVisibility(View.VISIBLE);
        dbHelper = new DBHelper(this, this);
        isRunning = true;
        encryptionRunningTv.setVisibility(View.VISIBLE);
        currentEntryPosition = 0;
        allContents = new ArrayList<>();
        dbHelper.getAllEntries();
    }

    @Override
    public void updateFinished(int updateCode) {
        switch (updateCode){
            case DetailActivityConfig.CONTENT_UPDATE_CODE:
                dbHelper.updateEntryIV(currentDate, currentIv);
                break;
            case DetailActivityConfig.IV_UPDATE_CODE:
                dbHelper.updateEntrySalt(currentDate, currentSalt);
                break;
            case DetailActivityConfig.SALT_UPDATE_CODE:
                currentEntryPosition++;
                encryptSingleEntry();
                break;
        }
    }

    @Override
    public void entryFound(Entry foundEntry) {

    }

    @Override
    public void allEntriesFound(List<Entry> allEntries) {
        this.allEntries = allEntries;
        for(Entry current : allEntries){
            allContents.add(current.getContent());
        }
        encryptSingleEntry();
    }

    private void encryptSingleEntry() {
        if(currentEntryPosition < allContents.size()){
            Log.d("Passwort", "Entry " + currentEntryPosition + " from " + allContents.size());
            StringTransformHelper.startEncryptionWithNewPw(allContents.get(currentEntryPosition), this, LoginActivity.correctPassword);
        } else{
            finishEditing();
        }
    }

    private void finishEditing() {
        Intent intent = new Intent();
        intent.putExtra(SettingsConfig.HAS_ENCRYPTED_KEY, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        Log.d("Passwort", "Encryption " + currentEntryPosition + " finished.");
        this.currentDate = allEntries.get(currentEntryPosition).getDate();
        this.currentIv = iv;
        this.currentSalt = salt;
        dbHelper.updateEntryContent(currentDate, result);
    }

    @Override
    public void onDecryptionFinished(String result) {

    }

    @Override
    public void onEncryptionFailed() {
       Log.d("Passwort", "Encryption failed!");
    }

    @Override
    public void onDecryptionFailed() {

    }

    @Override
    public void onBackPressed() {
        if(!isRunning) {
            setResult(RESULT_CANCELED);
            finish();
        } else{
            sendIsRunningToast();
        }
    }

    public void sendIsRunningToast(){
        Toast.makeText(this, "Please wait until encryption is finished", Toast.LENGTH_SHORT).show();
    }
}
