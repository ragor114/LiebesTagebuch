package ur.mi.liebestagebuch.GridView;

import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import ur.mi.liebestagebuch.R;

public class GridActivity extends AppCompatActivity {

    /*
     * Diese Activity zeigt für jeden Tag seit der Installation einen Tagebucheintrag im Grid an,
     * sollte noch kein Eintrag in der Datenbank für das Datum vorhanden sein, wird ein neuer erstellt
     * sonst wird die Emotion aus der Datenbank abgerufen und das Gridelement entsprechend eingefärbt.
     * Jeder Tagebuch Eintrag wird durch eine Box mit Smiley und das entsprechende Datum angezeigt.
     * Zum anzeigen der Gridelemente wird der EntryGridAdapter und eine Arraylist die beim Start
     * der Activity angelegt wird genutzt.
     *
     * Entwickelt von Moritz Schnell und Jannik Wiese.
     */

    private GridView grid;
    private EntryGridAdapter gridAdapter;
    private ArrayList<Entry> entries;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        initGrid();
    }

    private void initGrid() {
        grid = (GridView) findViewById(R.id.entries_grid_view);
        setUpArrayList();
        connectAdapterToArrayList();
    }

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

    private void connectAdapterToArrayList(){
        gridAdapter = new EntryGridAdapter(entries, this);
        grid.setAdapter(gridAdapter);
        refreshGrid();
    }

    private void refreshGrid(){
        gridAdapter.notifyDataSetChanged();
    }

}
