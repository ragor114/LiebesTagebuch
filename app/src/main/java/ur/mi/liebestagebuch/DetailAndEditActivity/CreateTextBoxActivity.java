package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.R;

public class CreateTextBoxActivity extends AppCompatActivity {

    private EditText editText;
    private ImageButton doneButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.create_textbox_activity);
        editText = findViewById(R.id.new_textbox_edittext);
        doneButton = findViewById(R.id.button_finish_textbox);
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
    }

}
