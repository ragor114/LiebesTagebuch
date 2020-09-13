package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.lang.reflect.Array;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;
import ur.mi.liebestagebuch.database.data.Entry;

public class DetailActivity extends AppCompatActivity implements CryptoListener, DatabaseListener, BoxListEncryptionListener {

    /*
     * In der DetailActivity werden das Datum, die ausgewählte Emotion und der Inhalt in Form von
     * Boxen eines Eintrags angezeigt.
     * Die nötigen Informationen werden aus der Datenbank abgerufen (bzw. ein neuer Datenbankeintrag
     * angelegt) und in einem EntryDetail-Objekt gespeichert.
     * In einem TextView ganz oben wird das Datum des Eintrags angezeigt.
     * Darunter liegen 5 Knöpfe, die mit den 5 auswählbaren Emotionen korrespondieren.
     * Darunter findet sich ein ListView in dem die einzelnen Boxen angezeigt werden.
     * Über einen Plus-Button können neue Einträge hinzugefügt werden.
     * Durch langes halten eines Eintrags kann dieser bearbeitet werden.
     * Ist man mit der Bearbeitung/Ansicht fertig kann über den zurück-Knopf oder den entsprechenden
     * Button in der ActionBar zurück in die Grid-Activity gelangt werden, in der die Änderungen
     * in der Datenbank gespeichert und Verschlüsselt werden. Dazu werden die Informationen aus dem
     * EntryDetail ausgelesen und per Intent an die GridActivity übergeben.
     *
     * Entwickelt von Jannik Wiese.
     */

    private TextView dateTextView;
    private ListView boxListView;
    private ImageButton[] emotionButtons;

    private DBHelper dbHelper;
    private EntryDetail entryDetail;
    private Date entryDate;
    private boolean isReadyToFinish;

    private BoxListAdapter boxListAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isReadyToFinish = true;
        setContentView(R.layout.detail_activity);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        this.entryDate = (Date) extras.get(DetailActivityConfig.ENTRY_DATE_KEY);
        Log.d("Detail", "Date loaded: " + entryDate.toString());
        dbHelper = new DBHelper(this, this);
        dbHelper.getEntryByDate(this.entryDate);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.detail_activity_action_bar, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item){
        int id = item.getItemId();
        switch (id){
            case R.id.back_button:
                finishDetail();
                break;
            case R.id.add_box_menu_item:
                startAddingNewBox();
                break;
        }
        return true;
    }

    private void setUpViews() {
        Log.d("Detail", "Setting up Views");
        dateTextView = (TextView) findViewById(R.id.datum_text_view);
        boxListView = (ListView) findViewById(R.id.box_list_view);
        emotionButtons = new ImageButton[5];
        emotionButtons[0] = (ImageButton) findViewById(R.id.button_very_good);
        emotionButtons[1] = (ImageButton) findViewById(R.id.button_good);
        emotionButtons[2] = (ImageButton) findViewById(R.id.button_normal);
        emotionButtons[3] = (ImageButton) findViewById(R.id.button_bad);
        emotionButtons[4] = (ImageButton) findViewById(R.id.button_very_bad);

        dateTextView.setText(entryDetail.getDateString());

        FloatingActionButton fab = findViewById(R.id.floating_action_button);
        fab.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                startAddingNewBox();
            }
        });

        Log.d("Detail", "setUpViews finished");
    }

    private void finishDetail(){
        if(isReadyToFinish == true){
            Intent returnIntent = new Intent();

            Date entryDate = entryDetail.getDate();
            Emotion entryEmotion = entryDetail.getEmotion();
            String boxListString = entryDetail.getBoxListString();

            returnIntent.putExtra(DetailActivityConfig.ENTRY_DATE_KEY, entryDate);
            returnIntent.putExtra(DetailActivityConfig.EMOTION_KEY, entryEmotion);
            returnIntent.putExtra(DetailActivityConfig.BOX_LIST_KEY, boxListString);

            setResult(RESULT_OK, returnIntent);
            finish();
        } else{
            //TODO: Toast-Message.
        }
    }

    @Override
    public void onBackPressed(){
        finishDetail();
    }


    private void startAddingNewBox(){
        Intent intent = new Intent(DetailActivity.this, TypeChooserActivity.class);
        startActivityForResult(intent, DetailActivityConfig.TYPE_CHOOSER_REQUEST_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(resultCode == RESULT_OK){
            Log.d("Detail", "Result in Detail OK");
        }

    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt){
        Log.d("Detail", "Encryption finished");
        Log.d("Encryption", "IV length at Encryption is " + iv.length);
        dbHelper.newEntry(entryDate, 2, result, salt, iv);
        Log.d("Encryption", iv.toString());
    }

    @Override
    public void onDecryptionFinished(String result) {
        return;
    }

    //TODO: Make useful
    @Override
    public void onEncryptionFailed() {

    }

    @Override
    public void onDecryptionFailed() {
        return;
    }

    @Override
    public void updateFinished(int updateCode) {
        switch (updateCode){
            case DetailActivityConfig.NEW_ENTRY_UPDATE_CODE:
                Log.d("Detail", "New Entry created");
                dbHelper.getEntryByDate(entryDate);
                break;
        }
    }

    @Override
    public void entryFound(Entry foundEntry) {
        if(foundEntry == null){
            Log.d("Detail", "No Entry found");
            isReadyToFinish = false;
            String emptyContent = "|<Text | Schreib deine Erlebnisse auf.";
            StringTransformHelper.startEncryption(emptyContent, this);
        } else{
            Log.d("Detail", "Entry found");
            isReadyToFinish = true;
            Log.d("Encryption", "found Entry: " + foundEntry.toString());
            //Debug:
            byte[] contentBytes = Base64.decode(foundEntry.getContent(), Base64.DEFAULT);
            Log.d("Encryption", "Content bytes:" + Arrays.toString(contentBytes));
            entryDetail = new EntryDetail(foundEntry, this);
            setUpViews();
        }
    }

    @Override
    public void onBoxListEncrypted(String encryptedBoxListString, byte[] iv, byte[] salt) {
        return;
    }

    @Override
    public void onBoxListDecryptionFinished() {
        //TODO: boxListView an Adapter anschließen.
        boxListAdapter = new BoxListAdapter(entryDetail.getBoxList(), this);
        boxListView.setAdapter(boxListAdapter);
        boxListAdapter.notifyDataSetChanged();
    }
}
