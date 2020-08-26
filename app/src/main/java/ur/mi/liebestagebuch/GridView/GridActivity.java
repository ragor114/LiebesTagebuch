package ur.mi.liebestagebuch.GridView;

import android.os.Bundle;
import android.widget.GridView;

import androidx.appcompat.app.AppCompatActivity;

import ur.mi.liebestagebuch.R;

public class GridActivity extends AppCompatActivity {

    private GridView grid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.grid_activity);

        grid = (GridView) findViewById(R.id.entries_grid_view);
        
    }

}
