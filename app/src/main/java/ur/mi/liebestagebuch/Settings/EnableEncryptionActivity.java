package ur.mi.liebestagebuch.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.enable_encryption_activity);
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
        dbHelper = new DBHelper(this, this);
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
            StringTransformHelper.startEncryption(allContents.get(currentEntryPosition), this);
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
        //TODO: Make useful.
    }

    @Override
    public void onDecryptionFailed() {

    }
}
