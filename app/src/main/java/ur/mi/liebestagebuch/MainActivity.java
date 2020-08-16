package ur.mi.liebestagebuch;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import java.util.ArrayList;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.Encryption.TestConfig;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Vorgefertigter Code:
        //Janniks 1. Branch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //StringTransformHelper Test Code:
        ArrayList<Box> testBoxList = StringTransformHelper.getBoxListFromString(TestConfig.TEST_BOXLIST_STRING);
        String testBoxListString = StringTransformHelper.getStringFromBoxList(testBoxList);
        System.out.println(testBoxListString);
    }
}
