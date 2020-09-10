package ur.mi.liebestagebuch.GridView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivity;
import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.database.DBHelper;
import ur.mi.liebestagebuch.database.DatabaseListener;

public class GridActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, EmotionRequestListener, CryptoListener, DatabaseListener {

    /*
     * Diese Activity zeigt für jeden Tag seit der Installation einen Tagebucheintrag im Grid an,
     * sollte noch kein Eintrag in der Datenbank für das Datum vorhanden sein, wird ein neuer erstellt
     * sonst wird die Emotion aus der Datenbank abgerufen und das Gridelement entsprechend eingefärbt.
     * Jeder Tagebuch Eintrag wird durch eine Box mit Smiley und das entsprechende Datum angezeigt.
     * Zum anzeigen der Gridelemente wird der EntryGridAdapter und eine Arraylist die beim Start
     * der Activity angelegt wird genutzt.
     *
     * Entwickelt von Moritz Schnell und Jannik Wiese.
     *
     * TODO: getInstallationDate(): Installationsdatum aus Datei abfragen, wenn Datei nicht vorhanden, Installationsdatum speichern.
     * TODO: getDaysPast(): Vergleich des heutigen Datums mit dem gespeicherten Datum und Berechnung der vergangen Tage.
     * TODO: requestAllEmotions(): Anfrage für ArrayList aller Emotionen an die Datenbank stellen, dabei diese Activity als Listener übergeben.
     * TODO: onEmotionRequestFinished(): Empfangene Emotionen den entsprechenden Entries zuweisen und dann grid refreshen.
     * TODO: onEmotionRequestFinished(): Falls kein Datenbankeintrag für ein Datum vorhanden ist, leeren Eintrag anlegen.
     * TODO: Layout verbessern, Grafiken statt hässlicher Rechtecke.
     *
     * TODO: onItemClick(): Übergang in Detailactivity, Übergabe der nötigen Informationen (reicht nur Datum?).
     */

    //Notwendige Attribute der Gridactivity
    private GridView grid;
    private EntryGridAdapter gridAdapter;
    private ArrayList<Entry> entries;

    private DBHelper dbHelper;
    private Date lastEditedEntryDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        initGrid();
        dbHelper = new DBHelper(this, this);
    }

    // Das Grid-View wird in einer Java-Variable gespeichert, die ArrayList aufgesetzt, der
    // Adapter mit der ArrayList verbunden und diese Activity als Listener registriert.
    private void initGrid() {
        grid = (GridView) findViewById(R.id.entries_grid_view);
        setUpArrayList();
        connectAdapterToArrayList();
        grid.setOnItemClickListener(this);
    }

    /*
     * Das Installationsdatum wird angefragt und die vergangenen Tage seit dem Installations-
     * datum berechnet. Für jeden Tag nach dem Installationsdatum wird ein Entry erstellt und
     * (in rückläufiger Reihenfolge, so dass der neueste Eintrag oben steht) in die ArrayList
     * eingefügt.
     */
    private void setUpArrayList(){
        entries = new ArrayList<>();
        Date installationDate = getInstallationDate();
        int daysPasssed = getDaysPassed(installationDate);
        // SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        c.setTime(installationDate);
        c.add(Calendar.DATE, daysPasssed);
        Date maxDate = c.getTime();
        for(int i=0; i < daysPasssed; i++){
            c.setTime(maxDate);
            c.add(Calendar.DATE, -i);
            Date currentDate = c.getTime();
            Entry currentEntry = new Entry(currentDate);
            entries.add(currentEntry);
        }
    }

    //TODO: Vergleich des gespeicherten Installationsdatums mit dem aktuellen und Rückgabe der Anzahl der vergangenen Tage.
    private int getDaysPassed(Date installationDate){
        return 9;
    }

    //TODO: Abruf des in einer Datei gespeicherten Installationsdatums und Rückgabe dieses Objekts.
    private Date getInstallationDate(){
        Date currentDate = new Date();
        Calendar c = Calendar.getInstance();
        c.setTime(currentDate);
        c.add(Calendar.DATE, -9);
        Date testInstallationDate = c.getTime();
        return testInstallationDate;
    }

    /*
     * Der GridAdapter wird mit der ArrayList und dem GridView verbunden und die Ansicht initial
     * aktualisiert.
     */
    private void connectAdapterToArrayList(){
        gridAdapter = new EntryGridAdapter(entries, this);
        grid.setAdapter(gridAdapter);
        refreshGrid();
    }

    // Diese Methode kann aufgerufen werden, wenn sich etwas an der ArrayList verändert hat.
    private void refreshGrid(){
        gridAdapter.notifyDataSetChanged();
    }

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Entry clickedEntry = entries.get(position);
        Date clickedEntryDate = clickedEntry.getDate();

        Intent intent = new Intent (GridActivity.this, DetailActivity.class);
        intent.putExtra(DetailActivityConfig.ENTRY_DATE_KEY, clickedEntryDate);
        startActivityForResult(intent, DetailActivityConfig.START_DETAIL_ACTIVITY_REQUEST_CODE);
    }

    public void requestAllEmotions(){
      // Anfrage für alle Emotionen in einer ArrayList an die Datenbank übergeben
      // Da Anfrage asyncron läuft wird diese Activity als Listener übergeben.
    }


    /*
     * Die in der ArrayList gespeicherten Emotionen werden mittels Entry.setEmotion(Emotion)
     * in den korrespondierenden Entry-Objekten aus entries gespeichert.
     * Gibt es für ein Datum eines Entry-Objekts aus Entry noch keine Emotion und damit keinen
     * Datenbankeintrag muss ein entsprechender Eintrag in der Datenbank angelegt werden.
     */
    @Override
    public void onEmotionRequestFinished(ArrayList<Emotion> allEmotions) {

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DetailActivityConfig.START_DETAIL_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Date entryDate = (Date) extras.get(DetailActivityConfig.ENTRY_DATE_KEY);
                String boxListString = extras.getString(DetailActivityConfig.BOX_LIST_KEY);
                int entryEmotion = (int) extras.get(DetailActivityConfig.EMOTION_KEY);
                lastEditedEntryDate = entryDate;
                dbHelper.newEntry(entryDate, entryEmotion, "", null, null);
                StringTransformHelper.startEncryption(boxListString, this);
            }
        }
    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        dbHelper.updateEntryContent(lastEditedEntryDate, result);
        dbHelper.updateEntryIV(lastEditedEntryDate, iv);
        dbHelper.updateEntrySalt(lastEditedEntryDate, salt);
    }

    @Override
    public void onDecryptionFinished(String result) {
        return;
    }

    //TODO: Make useful.
    @Override
    public void onEncryptionFailed() {

    }

    @Override
    public void onDecryptionFailed() {
        return;
    }

    @Override
    public void updateFinished(int updateCode) {

    }

    @Override
    public void entryFound(ur.mi.liebestagebuch.database.data.Entry foundEntry) {

    }
}
