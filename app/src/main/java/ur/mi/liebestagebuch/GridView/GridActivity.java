package ur.mi.liebestagebuch.GridView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.concurrent.TimeUnit;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivity;
import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.Settings.CheckEncryptionSettingHelper;
import ur.mi.liebestagebuch.Settings.SettingsActivity;
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
     * Check: getDaysPast(): Vergleich des heutigen Datums mit dem gespeicherten Datum und Berechnung der vergangen Tage.
     * TODO: requestAllEmotions(): Anfrage für ArrayList aller Emotionen an die Datenbank stellen, dabei diese Activity als Listener übergeben.
     * TODO: onEmotionRequestFinished(): Empfangene Emotionen den entsprechenden Entries zuweisen und dann grid refreshen.
     * TODO: onEmotionRequestFinished(): Falls kein Datenbankeintrag für ein Datum vorhanden ist, leeren Eintrag anlegen.
     * Check: Layout verbessern, Grafiken statt hässlicher Rechtecke.
     *
     * TODO: onItemClick(): Übergang in Detailactivity, Übergabe der nötigen Informationen (reicht nur Datum?).
     */

    //Notwendige Attribute der Gridactivity
    private GridView grid;
    private EntryGridAdapter gridAdapter;
    private ArrayList<GridEntry> entries;

    private DBHelper dbHelper;
    private Date lastEditedEntryDate;
    private String lastEditedBoxListString;
    private byte[] lastEditedIV;
    private byte[] lastEditedSalt;
    private int lastEditedEntryEmotion;

    private boolean savingLastEntryFinished;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        savingLastEntryFinished = true;

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
            GridEntry currentGridEntry = new GridEntry(currentDate);
            entries.add(currentGridEntry);
        }
    }

    //Check: Vergleich des gespeicherten Installationsdatums mit dem aktuellen und Rückgabe der Anzahl der vergangenen Tage.
    private int getDaysPassed(Date installationDate){
        Date currentDate = new Date();
        int daysPassed = 0;
        if (installationDate == currentDate){
            return daysPassed;
        } else {
            daysPassed = (int) (currentDate.getTime() - installationDate.getTime());
            return (int) TimeUnit.DAYS.convert((daysPassed), TimeUnit.MILLISECONDS);
        }
    }

    // TODO: Abruf des in einer Datei gespeicherten Installationsdatums und Rückgabe dieses Objekts.
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

    // Wird ein Element des Grids geklickt wird das Datum des entsprechenden Eintrags gesucht und
    // dieses der DetailActivity als Extra übergeben, die forResult gestartet wird.
    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if(savingLastEntryFinished) {
            GridEntry clickedGridEntry = entries.get(position);
            Date clickedEntryDate = clickedGridEntry.getDate();
            Emotion clickedEntryEmotion = clickedGridEntry.getEmotion();

            Intent intent = new Intent(GridActivity.this, DetailActivity.class);
            intent.putExtra(DetailActivityConfig.ENTRY_DATE_KEY, DateUtil.setToMidnight(clickedEntryDate));
            startActivityForResult(intent, DetailActivityConfig.START_DETAIL_ACTIVITY_REQUEST_CODE);
        } else{
            Toast.makeText(this, "Please wait while last Entry is saved, this can take a minute.", Toast.LENGTH_SHORT).show();
        }
    }

    public void requestAllEmotions(){
        Emotion values[] =Emotion.values();
        ArrayList allEmotions = new ArrayList<>();
       // for (Emotion.values()) {
       //     allEmotions.add(allEmotions.get(values));
       // }

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

    //Inflate Menü-Zahnrad oben rechts
    public boolean onCreateOptionsMenu(Menu menu){
        getMenuInflater().inflate(R.menu.settings_button, menu);
        return true;
    }


    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.settings_button) {
            Intent openSettingsIntent = new Intent(GridActivity.this, SettingsActivity.class);
            startActivity(openSettingsIntent);
        }
        return true;
    }
    /*
     * Meldet sich die DetailActivty mit einem Ergebnis zurück und ist der resultCode OK, dann
     * werden die übergebenen Informationen in lokalen Variablen zwischen gespeichert und nach und
     * nach in die Datenbank rückgespeichert.
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == DetailActivityConfig.START_DETAIL_ACTIVITY_REQUEST_CODE){
            if(resultCode == RESULT_OK){
                Bundle extras = data.getExtras();
                Date entryDate = (Date) extras.get(DetailActivityConfig.ENTRY_DATE_KEY);
                String boxListString = extras.getString(DetailActivityConfig.BOX_LIST_KEY);
                Emotion entryEmotion = (Emotion) extras.get(DetailActivityConfig.EMOTION_KEY);
                int emotionInt = getEmotionInt(entryEmotion);

                // Die Informationen des EntryDetails müssen zwischengespeichert werden, während
                // asynchron Datenbank- und Verschlüsselungsoperationen durchgeführt werden.
                Log.d("Date", "Before formatting milliseconds: " + entryDate.getTime());
                lastEditedEntryDate = DateUtil.setToMidnight(entryDate);
                Log.d("Date", "After formattting milliseconds: " + entryDate.getTime());
                lastEditedBoxListString = boxListString;
                lastEditedEntryEmotion = emotionInt;
                savingLastEntryFinished = false;
                dbHelper.newEntry(entryDate, emotionInt, "", null, null);
            }
        }
    }

    private int getEmotionInt(Emotion entryEmotion) {
        int emotionInt = 0;
        switch (entryEmotion){
            case VERY_GOOD:
                emotionInt = 0;
                break;
            case GOOD:
                emotionInt = 1;
                break;
            case NORMAL:
                emotionInt = 2;
                break;
            case BAD:
                emotionInt = 3;
                break;
            case VERY_BAD:
                emotionInt = 4;
                break;
        }
        return emotionInt;
    }

    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        lastEditedIV = iv;
        lastEditedSalt = salt;
        dbHelper.updateEntryContent(lastEditedEntryDate, result);
    }

    @Override
    public void onDecryptionFinished(String result) {
        return;
    }

    @Override
    public void onEncryptionFailed() {
        Toast.makeText(this, "Encryption failed, if this keeps happening, change password", Toast.LENGTH_SHORT);
    }

    @Override
    public void onDecryptionFailed() {
        return;
    }

    /*
     * Die Informationen aus der DetailActivity werden eine nach der anderen in die Datenbank
     * gespeichert.
     */
    @Override
    public void updateFinished(int updateCode) {
        switch (updateCode){
            case DetailActivityConfig.NEW_ENTRY_UPDATE_CODE:
                if(CheckEncryptionSettingHelper.encryptionActivated(this)){
                    StringTransformHelper.startEncryption(lastEditedBoxListString, this);
                    lastEditedBoxListString = "";
                } else{
                    dbHelper.updateEntryContent(lastEditedEntryDate, lastEditedBoxListString);
                    lastEditedBoxListString = "";
                }
                break;
            case DetailActivityConfig.CONTENT_UPDATE_CODE:
                if(CheckEncryptionSettingHelper.encryptionActivated(this)){
                    dbHelper.updateEntryIV(lastEditedEntryDate, lastEditedIV);
                } else{
                    byte[] emptyIv = new byte[]{00,00};
                    dbHelper.updateEntryIV(lastEditedEntryDate, emptyIv);
                }
                break;
            case DetailActivityConfig.IV_UPDATE_CODE:
                lastEditedIV = null;
                if(CheckEncryptionSettingHelper.encryptionActivated(this)){
                    dbHelper.updateEntrySalt(lastEditedEntryDate, lastEditedSalt);
                } else{
                    byte[] emptySalt = new byte[]{00,00};
                    dbHelper.updateEntrySalt(lastEditedEntryDate, emptySalt);
                }
                break;
            case DetailActivityConfig.SALT_UPDATE_CODE:
                lastEditedSalt = null;
                dbHelper.updateEntryEmotion(lastEditedEntryDate, lastEditedEntryEmotion);
                break;
            case DetailActivityConfig.EMOTION_UPDATE_CODE:
                Log.d("Detail", "Entry update complete");
                lastEditedEntryEmotion = 0;
                savingLastEntryFinished = true;
                break;
        }
    }

    @Override
    public void entryFound(ur.mi.liebestagebuch.database.data.Entry foundEntry) {
        return;
    }

    @Override
    public void allEntriesFound(List<ur.mi.liebestagebuch.database.data.Entry> allEntries) {

    }
}
