package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditHeaderBoxActivity extends AppCompatActivity {

    /*
     * Mit hilfe der EditHeaderBoxActivity kann eine neue Überschrift erzeugt werden, die dann in der
     * Liste angezeigt wird.
     *
     * Entwickelt von Jannik Wiese.
     */

    private EditText headerInputET;
    private ImageButton headerFinishedButton;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
    }

    private void setUpViews() {
        setContentView(R.layout.create_header_boxactivity);
        headerInputET = (EditText) findViewById(R.id.new_header_edittext);
        headerFinishedButton = (ImageButton) findViewById(R.id.button_finish_header);

        setUpForEditing();

        /*
         * Wird der Fertigstellen Button geklickt wird überprüft, ob der eingegebene Text '<' oder
         * '|' enthält, was zu Problemen beim Speichern führen könnte. Falls das der Fall ist wird
         * ein Toast angezeigt, falls nicht wird der Inhalt des EditTexts und eventuell die Position
         * der zubearbeitenden Box an die aufrufende Activity zurück übergeben.
         */
        headerFinishedButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String input = headerInputET.getText().toString();
                if(input != null && !input.equals("")){
                    if(input.contains("|") || input.contains("<")){
                        showInvalidCharsError();
                    } else{
                        finishHeader(input);
                    }
                }
            }
        });
    }

    private void setUpForEditing() {
        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();

        if(extras != null){
            String existingHeader = extras.getString(getString(R.string.existing_content_key));
            headerInputET.setText(existingHeader);
        }
    }

    private void showInvalidCharsError() {
        Toast.makeText(this, "Please do not use '<' or '|' in Header", Toast.LENGTH_SHORT).show();
    }

    private void finishHeader(String input) {
        Intent intent = new Intent();
        intent.putExtra(getString(R.string.headerbox_content_key), input);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        if(extras != null){
            int positionInList = extras.getInt(getString(R.string.position_in_list_key));
            intent.putExtra(getString(R.string.position_in_list_key), positionInList);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
