package ur.mi.liebestagebuch.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
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

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

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

    private void changePassword(){
        String oldPasswordText = oldPasswordEt.getText().toString();
        String newPasswordText = newPasswordEt.getText().toString();
        String repeatPasswordText = newPasswordRepeatEt.getText().toString();
        String correctPassword = SecurePasswordSaver.getStoredPassword(this);
        Log.d("Password", "Correct Password is: " + correctPassword + " oldPassword is: " + oldPasswordText + " newPassword is: " + newPasswordText);
        if(correctPassword.equals(oldPasswordText)){
            if(newPasswordText.equals(repeatPasswordText)){
                startReencryption(correctPassword, newPasswordText);
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

    private void startReencryption(String oldPassword, String newPassword) {
        Log.d("Password", "Starting reencryption");
        encryptionRunningView.setVisibility(View.VISIBLE);
        isReadyToFinish = false;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        dbHelper.getAllEntries();
    }

    private void finishEditing() {
        LoginActivity.correctPassword = newPassword;
        setResult(RESULT_OK);
        finish();
    }

    private void sendNotReadyToFinishMessage() {
        Toast.makeText(this, "Please wait while Entries are reencrypted with new Password.", Toast.LENGTH_SHORT).show();
    }

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

    @Override
    public void allEntriesFound(List<Entry> allEntries) {
        this.allEntries = allEntries;
        this.currentEntryPosition = 0;
        this.decryptedContents = new ArrayList<>();
        this.decryptionFinished = false;
        currentEntryReencryption();
        Log.d("Password", "Number of Entries is: " + allEntries.size());
    }

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

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        dbHelper.updateEntryContent(allEntries.get(currentEntryPosition).getDate(), result);
        this.currentEntryIv = iv;
        this.currentEntrySalt = salt;
    }

    @Override
    public void onDecryptionFinished(String result) {
        decryptedContents.add(result);
        currentEntryPosition++;
        currentEntryReencryption();
    }

    @Override
    public void onEncryptionFailed() {
        //TODO: Make usefull
    }

    @Override
    public void onDecryptionFailed() {
        //TODO: Make usefull
    }
}
