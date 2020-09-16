package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.R;

public class TypeChooserActivity extends AppCompatActivity {

    /*
     * In der TypeChooserActivity kann der Nutzer beim erstellen einer neuen Box auswählen welche
     * Art von Box er erstellen möchte. Darauf hin wird die entsprechende Activity gestartet.
     *
     * Entwickelt von Jannik Wiese.
     */

    private Button chooseText;
    private Button choosePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
    }

    private void setUpViews() {
        setContentView(R.layout.typechooser_activity);
        chooseText = findViewById(R.id.button_choose_text);
        choosePicture = findViewById(R.id.button_choose_picture);
        setOnClickListeners();
    }

    private void setOnClickListeners() {
        chooseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewTextBoxRequest();
            }
        });
        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewPictureBoxRequest();
            }
        });
    }

    private void startNewPictureBoxRequest() {
        Intent intent = new Intent(TypeChooserActivity.this, EditPictureBoxActivity.class);
        startActivityForResult(intent, DetailActivityConfig.NEW_PICTURE_BOX_REQUEST_CODE);
    }

    private void startNewTextBoxRequest() {
        Intent intent = new Intent(TypeChooserActivity.this, EditTextBoxActivity.class);
        startActivityForResult(intent, DetailActivityConfig.NEW_TEXT_BOX_REQUEST_CODE);
    }

    // Wenn die aufgerufene Activity ihr Ergebnis zurückmeldet wird dieses direkt und unverändert
    // an die Activity weitergeben, die die TypeChooserActivity aufgerufen hat.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }

}
