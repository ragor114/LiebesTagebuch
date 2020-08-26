package ur.mi.liebestagebuch;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import ur.mi.liebestagebuch.GridView.GridActivity;

public class LoginActivity extends AppCompatActivity {

    Button loginButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        //Vorgefertigter Code:
        //Kommentar
        // halt die klappe jannikkk
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        loginButton = (Button) findViewById(R.id.login_button);
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent switchActivityIntent = new Intent(LoginActivity.this, GridActivity.class);
                startActivity(switchActivityIntent);
            }
        });
    }
}
