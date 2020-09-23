package ur.mi.liebestagebuch.EditActivities;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditMapBoxActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private MapView editMap;
    private LatLng coordinates;
    private MarkerOptions markerOptions;
    private float zoom;
    private GoogleMap googleMap;

    private ImageButton finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_mapbox_activity);

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(permissions, DetailActivityConfig.PERMISSION_REQUEST_CODE);

        editMap = findViewById(R.id.map_view_edit_box);
        editMap.onCreate(savedInstanceState);
        editMap.getMapAsync(this);

        finishButton = findViewById(R.id.finish_map_edit);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra(DetailActivityConfig.MAP_BOX_CONTENT_KEY, coordinates);
                setResult(RESULT_OK, intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart(){
        super.onStart();
        editMap.onStart();
    }

    @Override
    protected void onResume(){
        super.onResume();
        editMap.onResume();
    }

    @Override
    protected void onPause(){
        super.onPause();
        editMap.onPause();
    }

    @Override
    protected void onStop(){
        editMap.onStop();
        super.onStop();
    }

    @Override
    protected void onDestroy(){
        editMap.onDestroy();
        super.onDestroy();
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        editMap.onLowMemory();
    }

    @Override
    public void onSaveInstanceState(Bundle outState){
        super.onSaveInstanceState(outState);
        editMap.onSaveInstanceState(outState);
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapView", "Map is showing");
        coordinates = new LatLng(49,12);
        googleMap.setOnMarkerDragListener(this);
        markerOptions = new MarkerOptions().position(coordinates)
                .title("Marker")
                .draggable(true);
        googleMap.addMarker(markerOptions);
        zoom = 15;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
        this.googleMap = googleMap;
        editMap.onResume();
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        coordinates = marker.getPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        editMap.onResume();
        Log.d("MapView", "End: Lat: " + coordinates.latitude + " Long: " + coordinates.longitude);
    }
}
