package ur.mi.liebestagebuch.Notification;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.LoginActivity;
import ur.mi.liebestagebuch.R;

public class RepeatingActivity extends AppCompatActivity {
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Intent intent = new Intent(RepeatingActivity.this, LoginActivity.class);
        startActivity(intent);
    }
}
