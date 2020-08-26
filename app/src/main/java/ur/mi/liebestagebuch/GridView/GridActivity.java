package ur.mi.liebestagebuch.GridView;

import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

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

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        grid = (GridView) findViewById(R.id.entries_grid_view);

    }

}
