package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.app.ActionBar;
import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.data.Entry;

public class DetailActivity extends AppCompatActivity implements BoxListEncryptionListener {

    private TextView dateTextView;
    private ListView boxListView;
    private ImageButton[] emotionButtons;

    private DBHelper dbHelper;
    private EntryDetail entryDetail;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        Date entryDate = (Date) extras.get(DetailActivityConfig.ENTRY_DATE_KEY);
        dbHelper = new DBHelper(this);
        Entry dbEntry = dbHelper.getEntryByDate(entryDate);
        entryDetail = new EntryDetail(dbEntry, this);

        findViews();
    }

    private void findViews() {
        dateTextView = (TextView) findViewById(R.id.datum_text_view);
        boxListView = (ListView) findViewById(R.id.box_list_view);
        emotionButtons = new ImageButton[5];
        emotionButtons[0] = (ImageButton) findViewById(R.id.button_very_good);
        emotionButtons[1] = (ImageButton) findViewById(R.id.button_good);
        emotionButtons[2] = (ImageButton) findViewById(R.id.button_normal);
        emotionButtons[3] = (ImageButton) findViewById(R.id.button_bad);
        emotionButtons[4] = (ImageButton) findViewById(R.id.button_very_bad);
    }

    private void finishDetail(){
        Intent returnIntent = new Intent();

        Date entryDate = entryDetail.getDate();
        Emotion entryEmotion = entryDetail.getEmotion();
        String boxListString = entryDetail.getBoxListString();

        returnIntent.putExtra(DetailActivityConfig.ENTRY_DATE_KEY, entryDate);
        returnIntent.putExtra(DetailActivityConfig.EMOTION_KEY, entryEmotion);
        returnIntent.putExtra(DetailActivityConfig.BOX_LIST_KEY, boxListString);

        setResult(RESULT_OK, returnIntent);
        finish();
    }

    @Override
    public void onBackPressed(){
        finishDetail();
    }

    @Override
    public void onBoxListEncrypted(String encryptedBoxListString, byte[] iv, byte[] salt) {

    }
}
