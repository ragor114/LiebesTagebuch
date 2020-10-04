package ur.mi.liebestagebuch.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.Date;
import java.util.List;
import java.util.concurrent.ThreadPoolExecutor;

import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.SecurePasswordSaver;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;
import ur.mi.liebestagebuch.database.data.Entry;

public class ChangePasswordActivity extends AppCompatActivity implements DatabaseListener, CryptoListener {

    private EditText oldPasswordEt;
    private EditText newPasswordEt;
    private EditText newPasswordRepeatEt;
    private ImageButton finishButton;

    private boolean isReadyToFinish;

    private DBHelper dbHelper;

    private String oldPassword;
    private String newPassword;
    private List<Entry> allEntries;
    private Date currentEntryDate;
    private int currentEntryPosition;

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
        isReadyToFinish = false;
        this.oldPassword = oldPassword;
        this.newPassword = newPassword;
        dbHelper.getAllEntries();
    }

    private void finishEditing() {
        setResult(RESULT_OK);
        finish();
    }

    private void sendNotReadyToFinishMessage() {
        Toast.makeText(this, "Please wait while Entries are reencrypted with new Password.", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void updateFinished(int updateCode) {

    }

    @Override
    public void entryFound(Entry foundEntry) {

    }

    @Override
    public void allEntriesFound(List<Entry> allEntries) {
        this.allEntries = allEntries;
        this.currentEntryPosition = 0;
        currentEntryReencryption();
    }

    private void currentEntryReencryption() {
        Entry currentEntry = allEntries.get(currentEntryPosition);
        this.currentEntryDate = currentEntry.getDate();
        StringTransformHelper.startDecryption(currentEntry.getContent(), this, currentEntry.getIv(), currentEntry.getSalt());
    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {

    }

    @Override
    public void onDecryptionFinished(String result) {
        //TODO: Reencrypt with new Password.
    }

    @Override
    public void onEncryptionFailed() {

    }

    @Override
    public void onDecryptionFailed() {

    }
}
