package ur.mi.liebestagebuch;

import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;

import android.view.View;

import java.util.Date;
import java.util.GregorianCalendar;

import ur.mi.liebestagebuch.database.DBHelper;


public class MainActivity extends AppCompatActivity {

    DBHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Vorgefertigter Code:
        //Jonas' Branch
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        dbHelper = new DBHelper(this);
    }

    //TESTSHIT
    /*public void clear(View view){
        dbHelper.clear();
    }*/
    public void list(View view){
        dbHelper.list();
    }
    public void add(View view){
        byte[] b = {1,2,6,9};
        dbHelper.newEntry(new Date(120, 07, 30),1,"asdawdawd",b);
    }
    public void newest(View view){
        dbHelper.newest();
    }
}
