package ur.mi.liebestagebuch.Settings;

import android.content.Intent;
import android.os.Bundle;
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
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;
import ur.mi.liebestagebuch.database.data.DBEntry;

public class DisableEncryptionActivity extends AppCompatActivity implements DatabaseListener, CryptoListener {

    /*
     * Ist die Verschlüsselung derzeit aktiviert wird diese Activity gestartet, um sie zu deaktivieren.
     * Grund dafür ist, dass alle Einträge entschlüsselt und rückgespeichert werden müssen, da es
     * in der Datenbank keinen Hinweis darauf gibt, ob der aktuelle Eintrag verschlüsselt ist oder nicht.
     * Da die Entschlüsselung und Rückspeicherung jedes Eintrags ca. 1 Sekunde dauert, vergeht eine Weile
     * bis alle Einträge entschlüsselt sind. In dieser Zeit kann nicht der Zurückknopf betätigt
     * werden, da ansonsten einige Einträge entschlüsselt wären und andere nicht, was beim Abruf eines
     * entschlüsselten Eintrags einen Absturz zur Folge hätte.
     *
     * Entwickelt von Jannik Wiese.
     */
    private Button disableButton;
    private TextView decryptionRunningView;

    private List<DBEntry> allEntries;
    private DBHelper dbHelper;
    private ArrayList<String> allContents;
    private int currentEntryPosition;
    private boolean decrypted;

    private boolean isRunning;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.disable_encryption_activity);

        setupViews();
    }

    private void setupViews() {
        progressBar = findViewById(R.id.disable_spinner);
        progressBar.setVisibility(View.GONE);

        isRunning = false;

        disableButton = findViewById(R.id.disable_decryption_button);
        decryptionRunningView = findViewById(R.id.disable_running_tv);
        decryptionRunningView.setVisibility(View.INVISIBLE);
        disableButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isRunning == false) {
                    startDecryption();
                    isRunning = true;
                }
            }
        });
    }

    /*
     * Wird die Entschlüsselung gestartet wird ein bestimmter Hinweis dazu in der Activity sichtbar
     * und dann alle Einträge in Form einer List<> aus der Datenbank abgefragt.
     */
    private void startDecryption() {
        progressBar.setVisibility(View.VISIBLE);
        currentEntryPosition = 0;
        decrypted = false;
        decryptionRunningView.setVisibility(View.VISIBLE);
        allContents = new ArrayList<>();
        dbHelper = new DBHelper(this, this);
        dbHelper.getAllEntries();
    }

    @Override
    public void updateFinished(int updateCode) {
        if (updateCode == getResources().getInteger(R.integer.content_update_code)) {
            currentEntryPosition++;
            decryptSingleEntry();
        }
    }

    @Override
    public void entryFound(DBEntry foundEntry) {

    }

    // Sind alle Einträge gefunden worden, werden sie nach und nach entschlüsselt.
    @Override
    public void allEntriesFound(List<DBEntry> allEntries) {
        this.allEntries = allEntries;
        decryptSingleEntry();
    }

    /*
     * Solange nicht alle Einträge entschlüsselt worden sind, werden die nötigen Information aus dem
     * entsprechendem Entry-Objekt abgerufen und die Entschlüsselung gestartet.
     * Wurden alle Einträge entschlüsselt werden die Ergebnisse der Reihe nach in die Datenbank
     * zurückgespeichert.
     * Wurden alle Einträge zurückgespeichert wird die Activity geschlossen.
     */
    private void decryptSingleEntry() {
        if (!decrypted && currentEntryPosition < allEntries.size()) {
            byte[] currentIv = allEntries.get(currentEntryPosition).getIv();
            byte[] currentSalt = allEntries.get(currentEntryPosition).getSalt();
            String content = allEntries.get(currentEntryPosition).getContent();
            StringTransformHelper.startDecryption(content, this, currentIv, currentSalt, this);
        } else if (!decrypted) {
            decrypted = true;
            currentEntryPosition = 0;
            decryptSingleEntry();
        } else if (decrypted && currentEntryPosition < allEntries.size()) {
            DBEntry currentEntry = allEntries.get(currentEntryPosition);
            String decryptedContent = allContents.get(currentEntryPosition);
            saveEntryBack(currentEntry, decryptedContent);
        } else {
            finishEditing();
        }
    }

    private void saveEntryBack(DBEntry currentEntry, String content) {
        Date entryDate = currentEntry.getDate();
        dbHelper.updateEntryContent(entryDate, content);
    }

    private void finishEditing() {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.has_decrypted_key), true);
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

    // Der Zurückknopf kann nur genutzt werden, wenn gerade keine Entschlüsselung läuft.
    @Override
    public void onBackPressed() {
        if (!isRunning) {
            setResult(RESULT_CANCELED);
            finish();
        } else {
            sendIsRunningToast();
        }
    }

    public void sendIsRunningToast() {
        Toast.makeText(this, "Please wait until decryption is finished", Toast.LENGTH_SHORT).show();
    }
}
