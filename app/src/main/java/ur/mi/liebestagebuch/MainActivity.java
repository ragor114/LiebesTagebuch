package ur.mi.liebestagebuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;

import java.util.ArrayList;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.Encryption.TestConfig;

public class MainActivity extends AppCompatActivity implements CryptoListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Vorgefertigter Code:
        //Janniks 1. Branch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //StringTransformHelper Test Code:
        ArrayList<Box> testBoxList = StringTransformHelper.getBoxListFromString(TestConfig.TEST_BOXLIST_STRING);
        String testBoxListString = StringTransformHelper.getStringFromBoxList(testBoxList);
        Log.d("StringTransformHelper", testBoxListString);

        //Verschlüsselungs Test-Code:
        Log.d("Encryption", "Start Encryption: " + TestConfig.TEST_STRING_TO_EN_AND_DE_CRYPT);
        StringTransformHelper.startEncryption(TestConfig.TEST_STRING_TO_EN_AND_DE_CRYPT, this);
    }


    //Test-Implementierungen der Crypto-Listener-Methoden:
    @Override
    public void onEncryptionFinished(String result) {
        Log.d("Encryption", "Encrypted: " + result);
        StringTransformHelper.startDecryption(result, this);
    }

    @Override
    public void onDecryptionFinished(String result) {
        Log.d("Encryption", "Decrypted: " + result);
    }
}
