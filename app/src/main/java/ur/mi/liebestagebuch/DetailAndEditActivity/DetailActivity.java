package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import ur.mi.liebestagebuch.R;

public class DetailActivity extends AppCompatActivity {

    private TextView dateTextView;
    private ListView boxListView;
    private ImageButton[] emotionButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.detail_activity);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        Date entryDate = (Date) extras.get(DetailActivityConfig.ENTRY_DATE_KEY);

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

}
