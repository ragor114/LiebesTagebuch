package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ur.mi.liebestagebuch.EditActivities.EditMapBoxActivity;
import ur.mi.liebestagebuch.R;

public class MapBoxDetailActivity extends AppCompatActivity implements OnMapReadyCallback {

    /*
     * Da eine Activity laut Google nicht mehrere Maps anzeigen soll, wird diese Activity genutzt,
     * um den Inhalt von MapBoxen an zu zeigen.
     * Dazu wird eine Karte mit der nicht interagiert werden kann angezeigt.
     * Per Klick auf das Stift Icon in der Action Bar gelangt man in die Bearbeitungs-Activity für Maps.
     *
     * Entwickelt von Jannik Wiese.
     */

    private MapView mapView;
    private LatLng coordinates;
    private int positionInList;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.map_box_detail_activity);

        // Aus dem Intent werden die Koordinaten und (falls bearbeitet werden soll die Position der Box geladen)
        Intent callingIntent = getIntent();
        Bundle extras = callingIntent.getExtras();
        this.coordinates = (LatLng) extras.get(DetailActivityConfig.EXISTING_CONTENT_KEY);
        this.positionInList = extras.getInt(DetailActivityConfig.POSITION_IN_LIST_KEY);

        mapView = findViewById(R.id.map_box_detail_map);
        mapView.onCreate(savedInstanceState);
        mapView.getMapAsync(this);
        mapView.setClickable(false);
    }

    // MapViews müssen über jede Änderung im Lifecycle informiert werden:
    @Override
    protected void onStart() {
        super.onStart();
        mapView.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onStop() {
        mapView.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        mapView.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory() {
        super.onLowMemory();
        mapView.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState, @NonNull PersistableBundle outPersistentState) {
        super.onSaveInstanceState(outState, outPersistentState);
        mapView.onSaveInstanceState(outState);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.map_box_acticity_action_bar, menu);
        return super.onCreateOptionsMenu(menu);
    }

    // Wird der Stift in der Action Bar geklickt wird die Bearbeitungsactivity gestartet und dieser
    // die Koordinaten und die Position der Box weiter gereicht.
    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.start_editing_map_box){
            Intent intent = new Intent(MapBoxDetailActivity.this, EditMapBoxActivity.class);
            intent.putExtra(DetailActivityConfig.EXISTING_CONTENT_KEY, coordinates);
            intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, positionInList);
            startActivityForResult(intent, DetailActivityConfig.EDIT_BOX_REQUEST_CODE);
        }
        return super.onOptionsItemSelected(item);
    }

    // Meldet sich die Bearbeitungs-Activity zurück wird das Ergebnis genau so an die DetailActivity
    // zurück übergeben.
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        setResult(resultCode, data);
        finish();
    }

    // Wurde die GoogleMap asynchron erfolgreich geladen wird die Interaktion mit der Map deaktiviert
    // und ein Marker an den Koordinaten platziert.
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapView", "Map is showing");
        MarkerOptions markerOptions = new MarkerOptions().position(coordinates)
                .title("Marker")
                .draggable(false);
        googleMap.getUiSettings().setAllGesturesEnabled(false);
        googleMap.addMarker(markerOptions);
        float zoom = 15;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
        mapView.onResume();
    }
}
