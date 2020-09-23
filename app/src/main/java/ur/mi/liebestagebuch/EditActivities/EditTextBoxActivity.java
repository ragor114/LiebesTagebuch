package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditTextBoxActivity extends AppCompatActivity {

    /*
     * Die EditTextBoxActivity dient dazu eine Textbox zu erstellen oder zu bearbeiten.
     * Dazu wird der Inhalt der Box in einem EditText bearbeitet und die Bearbeitung durch
     * Drücken des daneben liegenden Fertigstellen-Knopfes beendet und der aufrufenden Activity
     * der Inhalt des EditTextes übergeben.
     *
     * Entwickelt von Jannik Wiese.
     */

    private EditText editText;
    private ImageButton doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setUpViews();

    }

    private void setUpViews() {
        setContentView(R.layout.create_textbox_activity);
        editText = findViewById(R.id.new_textbox_edittext);
        doneButton = findViewById(R.id.button_finish_textbox);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();

        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String content = editText.getText().toString();
                intent.putExtra(DetailActivityConfig.TEXTBOX_CONTENT_KEY, content);
                setResult(RESULT_OK, intent);
                finish();
            }
        });

        if (extras != null) {
            setUpForEdit(extras);
        }
    }

    /*
     * Wenn der Activity Extras übergeben wurden, dann weiß sie, dass sie zum Bearbeiten statt
     * zum neu Erstellen aufgerufen wurde. Daher wird der Inhalt des EditTextes auf den übergebenen
     * String gesetzt und der OnClickListener des Buttons so überschrieben, dass auch die Position
     * der Box zurück gegeben wird.
     */
    private void setUpForEdit(Bundle extras) {
        editText.setText(extras.getString(DetailActivityConfig.EXISTING_CONTENT_KEY));
        final int positionInList = extras.getInt(DetailActivityConfig.POSITION_IN_LIST_KEY);
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                String content = editText.getText().toString();
                intent.putExtra(DetailActivityConfig.TEXTBOX_CONTENT_KEY, content);
                intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, positionInList);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }
}
