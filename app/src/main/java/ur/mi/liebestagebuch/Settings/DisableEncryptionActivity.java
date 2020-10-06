package ur.mi.liebestagebuch.Settings;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.reflect.Array;
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

public class DisableEncryptionActivity extends AppCompatActivity implements DatabaseListener, CryptoListener {

    private Button disableButton;
    private TextView decryptionRunningView;

    private List<Entry> allEntries;
    private DBHelper dbHelper;
    private ArrayList<String> allContents;
    private int currentEntryPosition;
    private boolean decrypted;

    private boolean isRunning;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disable_encryption_activity);

        isRunning = false;

        disableButton = findViewById(R.id.disable_decryption_button);
        decryptionRunningView = findViewById(R.id.disable_running_tv);
        decryptionRunningView.setVisibility(View.INVISIBLE);
        disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isRunning == false) {
                    startDecryption();
                    isRunning = true;
                }
            }
        });
    }

    private void startDecryption() {
        currentEntryPosition = 0;
        decrypted = false;
        decryptionRunningView.setVisibility(View.VISIBLE);
        allContents = new ArrayList<>();
        dbHelper = new DBHelper(this, this);
        dbHelper.getAllEntries();
    }

    @Override
    public void updateFinished(int updateCode) {
        switch (updateCode){
            case DetailActivityConfig.CONTENT_UPDATE_CODE:
                currentEntryPosition++;
                decryptSingleEntry();
                break;
        }
    }

    @Override
    public void entryFound(Entry foundEntry) {

    }

    @Override
    public void allEntriesFound(List<Entry> allEntries) {
        this.allEntries = allEntries;
        decryptSingleEntry();
    }

    private void decryptSingleEntry() {
        if(!decrypted && currentEntryPosition < allEntries.size()){
            byte[] currentIv = allEntries.get(currentEntryPosition).getIv();
            byte[] currentSalt = allEntries.get(currentEntryPosition).getSalt();
            String content = allEntries.get(currentEntryPosition).getContent();
            StringTransformHelper.startDecryption(content, this, currentIv, currentSalt);
        } else if(!decrypted){
            decrypted = true;
            currentEntryPosition = 0;
            decryptSingleEntry();
        } else if(decrypted && currentEntryPosition < allEntries.size()){
            Entry currentEntry = allEntries.get(currentEntryPosition);
            String decryptedContent = allContents.get(currentEntryPosition);
            saveEntryBack(currentEntry, decryptedContent);
        } else{
            finishEditing();
        }
    }

    private void saveEntryBack(Entry currentEntry, String content) {
        Date entryDate = currentEntry.getDate();
        dbHelper.updateEntryContent(entryDate, content);
    }

    private void finishEditing() {
        Intent intent = new Intent();
        intent.putExtra(SettingsConfig.HAS_DECRYPTED_KEY, true);
        setResult(RESULT_OK, intent);
        finish();
    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {

    }

    @Override
    public void onDecryptionFinished(String result) {
        allContents.add(result);
        currentEntryPosition++;
        decryptSingleEntry();
    }

    @Override
    public void onEncryptionFailed() {

    }

    @Override
    public void onDecryptionFailed() {

    }
}
