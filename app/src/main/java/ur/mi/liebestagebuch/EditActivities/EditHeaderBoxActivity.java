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

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();

        if(extras != null){
            String existingHeader = extras.getString(DetailActivityConfig.EXISTING_CONTENT_KEY);
            headerInputET.setText(existingHeader);
        }

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

    private void showInvalidCharsError() {
        Toast.makeText(this, "Please do not use '<' or '|' in Header", Toast.LENGTH_SHORT).show();
    }

    private void finishHeader(String input) {
        Intent intent = new Intent();
        intent.putExtra(DetailActivityConfig.HEADER_BOX_CONTENT_KEY, input);

        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        if(extras != null){
            int positionInList = extras.getInt(DetailActivityConfig.POSITION_IN_LIST_KEY);
            intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, positionInList);
        }

        setResult(RESULT_OK, intent);
        finish();
    }
}
