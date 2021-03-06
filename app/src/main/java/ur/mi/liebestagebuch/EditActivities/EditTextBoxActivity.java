package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

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
                String content = editText.getText().toString();
                if (!content.contains("|") && !content.contains("<")) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.textbox_content_key), content);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showWarningForIllegalChars();
                }
            }
        });

        if (extras != null) {
            setUpForEdit(extras);
        }
    }

    private void showWarningForIllegalChars() {
        Toast.makeText(this, "Please do not use '<' or '|' in your Text", Toast.LENGTH_SHORT).show();
    }

    /*
     * Wenn der Activity Extras übergeben wurden, dann weiß sie, dass sie zum Bearbeiten statt
     * zum neu Erstellen aufgerufen wurde. Daher wird der Inhalt des EditTextes auf den übergebenen
     * String gesetzt und der OnClickListener des Buttons so überschrieben, dass auch die Position
     * der Box zurück gegeben wird.
     */
    private void setUpForEdit(Bundle extras) {
        editText.setText(extras.getString(getString(R.string.existing_content_key)));
        final int positionInList = extras.getInt(getString(R.string.position_in_list_key));
        doneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String content = editText.getText().toString();
                if (!content.contains("|") && !content.contains("<")) {
                    Intent intent = new Intent();
                    intent.putExtra(getString(R.string.textbox_content_key), content);
                    intent.putExtra(getString(R.string.position_in_list_key), positionInList);
                    setResult(RESULT_OK, intent);
                    finish();
                } else {
                    showWarningForIllegalChars();
                }
            }
        });
    }
}
