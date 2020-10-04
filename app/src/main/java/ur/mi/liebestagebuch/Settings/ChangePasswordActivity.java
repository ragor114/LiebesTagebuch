package ur.mi.liebestagebuch.Settings;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import java.util.concurrent.ThreadPoolExecutor;

import ur.mi.liebestagebuch.Encryption.SecurePasswordSaver;
import ur.mi.liebestagebuch.R;

public class ChangePasswordActivity extends AppCompatActivity {

    private EditText oldPasswordEt;
    private EditText newPasswordEt;
    private EditText newPasswordRepeatEt;
    private ImageButton finishButton;

    private boolean isReadyToFinish;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.change_password_activity);

        isReadyToFinish = true;

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
    }

    private void finishEditing() {
        setResult(RESULT_OK);
        finish();
    }

    private void sendNotReadyToFinishMessage() {
        Toast.makeText(this, "Please wait while Entries are reencrypted with new Password.", Toast.LENGTH_SHORT).show();
    }

}
