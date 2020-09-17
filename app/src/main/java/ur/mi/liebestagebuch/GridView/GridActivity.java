package ur.mi.liebestagebuch.GridView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.concurrent.TimeUnit;

import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.Settings.SettingsActivity;

public class GridActivity extends AppCompatActivity implements AdapterView.OnItemClickListener, EmotionRequestListener {

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
    private ArrayList<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        initGrid();
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

    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        Entry clickedEntry = entries.get(position);
        Date clickedEntryDate = clickedEntry.getDate();
        Emotion clickedEntryEmotion = clickedEntry.getEmotion();

        //Auslesen der Informationen über den mit dem Datum korrespondierenden Datenbankeintrag.
        //Aufruf der Detailansicht und Übergabe der (verschlüsselten) Informationen.
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


    public boolean onOptionsItemSelected(MenuItem item){
        if(item.getItemId() == R.id.settings_button){
            Intent openSettingsIntent = new Intent(GridActivity.this, SettingsActivity.class);
            startActivity(openSettingsIntent);
        }
        return true;
    }
}
