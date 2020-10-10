package ur.mi.liebestagebuch.Settings;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.lang.annotation.AnnotationTypeMismatchException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.SecurePasswordSaver;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.LoginActivity;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;
import ur.mi.liebestagebuch.database.data.Entry;

public class ChangePasswordActivity extends AppCompatActivity implements DatabaseListener, CryptoListener {

    /*
     * Da die Verschlüsselung auf dem gespeicherten, vom Nutzer festgelegten Passwort basiert, müssen,
     * wenn das Passwort geändert wird, alle Einträge in der Datenbank entschlüsselt und neu verschlüsselt
     * werden, bevor das alte Passwort vergessen ist. Solange kann die Activity nicht verlassen werden,
     * da sonst Einträge verbleiben, die mit dem alten Passwort verschlüsselt wurden.
     * Da eine Entschlüsselung ca. 1 Sekunde und eine Verschlüsselung ca. 1,5 Sekunden dauert, kann
     * es eine Weile dauern, bis der Vorgang abgeschlossen ist.
     * Darüber hinaus überprüft die Activity, ob man das neue Passwort richtig eingegeben hat (da
     * man es wiederholen muss) und, ob man das alte Passwort kennt.
     *
     * Entwickelt von Jannik Wiese.
     */

    private EditText oldPasswordEt;
    private EditText newPasswordEt;
    private EditText newPasswordRepeatEt;
    private ImageButton finishButton;
    private TextView encryptionRunningView;

    private boolean isReadyToFinish;

    private DBHelper dbHelper;

    private String oldPassword;
    private String newPassword;
    private List<Entry> allEntries;
    private int currentEntryPosition;
    private boolean decryptionFinished;
    private ArrayList<String> decryptedContents;
    private byte[] currentEntryIv;
    private byte[] currentEntrySalt;

    private ProgressBar progressBar;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

        isReadyToFinish = true;
        dbHelper = new DBHelper(this, this);

        setupViews();
    }

    private void setupViews() {
        progressBar = findViewById(R.id.change_password_spinner);
        progressBar.setVisibility(View.GONE);

        isReadyToFinish = true;
        dbHelper = new DBHelper(this, this);

        oldPasswordEt = findViewById(R.id.change_password_old_password);
        newPasswordEt = findViewById(R.id.change_password_new_password);
        newPasswordRepeatEt = findViewById(R.id.change_password_repeat);
        finishButton = findViewById(R.id.change_password_finish);
        encryptionRunningView = findViewById(R.id.encryption_running_tv);
        encryptionRunningView.setVisibility(View.INVISIBLE);

        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(isReadyToFinish){
                    changePassword();
                } else{
                    sendNotReadyToFinishMessage();
                }
            }
        });
    }

    /*
     * Wenn das Passwort geändert werden soll, werden die Nutzereingaben und das korrekte aktuelle
     * Passwort geladen. Falls das korrekte Passwort mit dem eingegeben Passwort übereinstimmt und
     * die beiden Wiederholungen übereinstimmen wird mit der Neuverschlüsselung begonnen. Ansonsten
     * werden Fehler als Toasts gesendet.
     */
    private void changePassword(){
        String oldPasswordText = oldPasswordEt.getText().toString();
        String newPasswordText = newPasswordEt.getText().toString();
        String repeatPasswordText = newPasswordRepeatEt.getText().toString();
        String correctPassword = SecurePasswordSaver.getStoredPassword(this);
        Log.d("Password", "Correct Password is: " + correctPassword + " oldPassword is: " + oldPasswordText + " newPassword is: " + newPasswordText);
        if(correctPassword.equals(oldPasswordText)){
            if(newPasswordText.equals(repeatPasswordText)){
                startReencryption(newPasswordText);
                progressBar.setVisibility(View.VISIBLE);
            } else{
                sendPasswordDoesNotEqualRepeatMessage();
            }
        } else{
            sendIncorrectPasswordMessage();
        }
    }

    private void sendIncorrectPasswordMessage() {
        Toast.makeText(this, "Old password is not correct", Toast.LENGTH_SHORT).show();
    }

    private void sendPasswordDoesNotEqualRepeatMessage() {
        Toast.makeText(this, "New password and repitition are not equal", Toast.LENGTH_SHORT).show();
    }

    /*
     * Falls die Eingaben korrekt sind und die Verschlüsselung aktiviert ist, werden alle Einträge
     * neu verschlüsselt. Dazu wird ein entsprechender Hinweis sichtbar gemacht und alle Einträge aus
     * der Datenbank geladen.
     * Falls die Einträge nicht verschlüsselt sind, wird das neue Passwort einfach
     * gespeichert udn die Activity beendet.
     */
    private void startReencryption(String newPassword) {
        Log.d("Password", "Starting reencryption");
        boolean isEncrypted = CheckEncryptionSettingHelper.encryptionActivated(this);
        if(isEncrypted) {
            encryptionRunningView.setVisibility(View.VISIBLE);
            isReadyToFinish = false;
            this.newPassword = newPassword;
            dbHelper.getAllEntries();
        } else{
            Log.d("Password", "Entries are not encrypted");
            SecurePasswordSaver.storePasswordSecure(newPassword, this);
            LoginActivity.correctPassword = newPassword;
            finishEditing();
        }
    }

    private void finishEditing() {
        if(newPassword != null){
            LoginActivity.correctPassword = newPassword;
        }
        setResult(RESULT_OK);
        finish();
    }

    private void sendNotReadyToFinishMessage() {
        Toast.makeText(this, "Please wait while Entries are reencrypted with new Password.", Toast.LENGTH_SHORT).show();
    }

    // Bei der Neuverschlüsselung müssen Inhalt, Iv und Salt gespeichert werden.
    @Override
    public void updateFinished(int updateCode) {
        switch (updateCode){
            case DetailActivityConfig.CONTENT_UPDATE_CODE:
                dbHelper.updateEntryIV(allEntries.get(currentEntryPosition).getDate(), currentEntryIv);
                break;
            case DetailActivityConfig.IV_UPDATE_CODE:
                dbHelper.updateEntrySalt(allEntries.get(currentEntryPosition).getDate(), currentEntrySalt);
                break;
            case DetailActivityConfig.SALT_UPDATE_CODE:
                currentEntryPosition ++;
                currentEntryReencryption();
                break;
        }
    }

    @Override
    public void entryFound(Entry foundEntry) {

    }

    /*
     * Wenn alle Einträge gefunden wurden, werden Sie der Reihe nach entschlüsselt.
     */
    @Override
    public void allEntriesFound(List<Entry> allEntries) {
        this.allEntries = allEntries;
        this.currentEntryPosition = 0;
        this.decryptedContents = new ArrayList<>();
        this.decryptionFinished = false;
        currentEntryReencryption();
        Log.d("Password", "Number of Entries is: " + allEntries.size());
    }

    /*
     * Im ersten Durchgang werden die Inhalte aller Einträge entschlüsselt und die entschlüsselten
     * Inhalte in der gleichen Reihenfolge in einer Arraylist abgelegt.
     * Im zweiten Durchgang werden alle Inhalte neu verschlüsselt und der verschlüsselte Inhalt, Iv
     * und Salt in der Datenbank gespeichert.
     * Wenn beide male alle Einträge durchgegangen wurden, wird das neue Passwort gespeichert und
     * die Activity beeendet.
     */
    private void currentEntryReencryption() {
        if(!decryptionFinished && currentEntryPosition < allEntries.size()){
            Log.d("Password", "Decrypting " + currentEntryPosition);
            Entry currentEntry = allEntries.get(currentEntryPosition);
            StringTransformHelper.startDecryption(currentEntry.getContent(), this, currentEntry.getIv(), currentEntry.getSalt());
        } else if(!decryptionFinished){
            Log.d("Password", "All decryptions finished");
            decryptionFinished = true;
            currentEntryPosition = 0;
            currentEntryReencryption();
        } else if(decryptionFinished && currentEntryPosition < allEntries.size()){
            Log.d("Password", "Encrypting " + currentEntryPosition);
            StringTransformHelper.startEncryptionWithNewPw(decryptedContents.get(currentEntryPosition), this, newPassword);
        } else{
            Log.d("Password", "Saving new Password");
            SecurePasswordSaver.storePasswordSecure(newPassword, this);
            isReadyToFinish = true;
            finishEditing();
        }
    }

    // Bei der Verschlüsselung müssen iv und salt kurzzeitig in Instanzvariablen zwischengespeichert werden.
    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        dbHelper.updateEntryContent(allEntries.get(currentEntryPosition).getDate(), result);
        this.currentEntryIv = iv;
        this.currentEntrySalt = salt;
    }

    // Die entschlüsselten Inhalte werden in einer Arraylist gespeichert und dann die Entschlüsselung
    // des nächsten Eintrags gestartet.
    @Override
    public void onDecryptionFinished(String result) {
        decryptedContents.add(result);
        currentEntryPosition++;
        currentEntryReencryption();
    }

    @Override
    public void onEncryptionFailed() {
        Toast.makeText(this, "Encryption failed, try again later", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onDecryptionFailed() {
        Toast.makeText(this, "Decryption failed, try again later", Toast.LENGTH_SHORT).show();
    }

    // Die Activity kann nur verlassen werden wenn keine Ent- oder Verschlüsselung läuft.
    @Override
    public void onBackPressed() {
        if(isReadyToFinish){
            super.onBackPressed();
        } else{
            sendNotReadyToFinishMessage();
        }
    }
}
