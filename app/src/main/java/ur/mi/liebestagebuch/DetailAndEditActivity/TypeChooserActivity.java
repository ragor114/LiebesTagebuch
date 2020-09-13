package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.R;

public class TypeChooserActivity extends AppCompatActivity {

    private Button chooseText;
    private Button choosePicture;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.typechooser_activity);
        chooseText = findViewById(R.id.button_choose_text);
        choosePicture = findViewById(R.id.button_choose_picture);
        chooseText.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(TypeChooserActivity.this, CreateTextBoxActivity.class);
                startActivityForResult(intent, DetailActivityConfig.NEW_TEXT_BOX_REQUEST_CODE);
            }
        });
        choosePicture.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }

}
