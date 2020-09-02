package ur.mi.liebestagebuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.EncryptionConfig;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.Encryption.TestConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Vorgefertigter Code:
        //Janniks 1. Branch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

}
