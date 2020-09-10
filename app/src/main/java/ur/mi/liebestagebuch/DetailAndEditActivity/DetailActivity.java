package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.Date;

import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.data.Entry;

public class DetailActivity extends AppCompatActivity implements BoxListEncryptionListener, CryptoListener {

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

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        isReadyToFinish = true;
        setContentView(R.layout.detail_activity);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        this.entryDate = (Date) extras.get(DetailActivityConfig.ENTRY_DATE_KEY);
        dbHelper = new DBHelper(this);
        Entry dbEntry = dbHelper.getEntryByDate(this.entryDate);

        if(dbEntry == null){
            isReadyToFinish = false;
            String emptyContent = "|<Text | Schreib deine Erlebnisse auf.";
            StringTransformHelper.startEncryption(emptyContent, this);
        } else{
            entryDetail = new EntryDetail(dbEntry, this);
            setUpViews();
        }

    }

    private void setUpViews() {
        dateTextView = (TextView) findViewById(R.id.datum_text_view);
        boxListView = (ListView) findViewById(R.id.box_list_view);
        emotionButtons = new ImageButton[5];
        emotionButtons[0] = (ImageButton) findViewById(R.id.button_very_good);
        emotionButtons[1] = (ImageButton) findViewById(R.id.button_good);
        emotionButtons[2] = (ImageButton) findViewById(R.id.button_normal);
        emotionButtons[3] = (ImageButton) findViewById(R.id.button_bad);
        emotionButtons[4] = (ImageButton) findViewById(R.id.button_very_bad);

        dateTextView.setText(entryDetail.getDateString());

        //TODO: boxListView an Adapter anschließen.
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

    @Override
    public void onBoxListEncrypted(String encryptedBoxListString, byte[] iv, byte[] salt) {

    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        dbHelper.newEntry(entryDate, 2, result, iv, salt);
        isReadyToFinish = true;
        Entry createdDbEntry = dbHelper.getEntryByDate(entryDate);
        entryDetail = new EntryDetail(createdDbEntry, this);
        setUpViews();
        //notifyDatasetChanged!
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
}
