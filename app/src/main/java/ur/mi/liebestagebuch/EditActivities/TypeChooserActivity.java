package ur.mi.liebestagebuch.EditActivities;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
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
    private Button chooseMap;
    private Button chooseMusic;
    private Button chooseHeader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setUpViews();
    }

    private void setUpViews() {
        setContentView(R.layout.typechooser_activity);
        chooseText = findViewById(R.id.button_choose_text);
        choosePicture = findViewById(R.id.button_choose_picture);
        chooseMap = findViewById(R.id.button_choose_map);
        chooseMusic = findViewById(R.id.button_choose_music);
        chooseHeader = findViewById(R.id.button_choose_header);
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
        chooseMap.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewMapBoxRequest();
            }
        });
        chooseMusic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewMusicBoxRequest();
            }
        });
        chooseHeader.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startNewHeaderBoxRequest();
            }
        });
    }

    private void startNewHeaderBoxRequest() {
        Intent intent = new Intent(TypeChooserActivity.this, EditHeaderBoxActivity.class);
        startActivityForResult(intent, DetailActivityConfig.NEW_HEADER_BOX_REQUEST_CODE);
    }

    private void startNewMusicBoxRequest() {
        Intent intent = new Intent(TypeChooserActivity.this, EditMusicBoxActivity.class);
        startActivityForResult(intent, DetailActivityConfig.NEW_MUSIC_BOX_REQUEST_CODE);
    }

    private void startNewMapBoxRequest() {
        Intent intent = new Intent(TypeChooserActivity.this, EditMapBoxActivity.class);
        startActivityForResult(intent, DetailActivityConfig.NEW_MAP_BOX_REQUEST_CODE);
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
