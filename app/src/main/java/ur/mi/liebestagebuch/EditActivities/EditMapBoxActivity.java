package ur.mi.liebestagebuch.EditActivities;

import android.Manifest;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditMapBoxActivity extends AppCompatActivity implements OnMapReadyCallback, GoogleMap.OnMarkerDragListener {

    private MapView editMap;
    private LatLng coordinates;
    private MarkerOptions markerOptions;
    private float zoom;
    private GoogleMap googleMap;

    private ImageButton finishButton;
    private EditText searchBar;
    private ImageButton searchButton;

    private Bundle extras;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_mapbox_activity);

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(permissions, DetailActivityConfig.PERMISSION_REQUEST_CODE);

        Intent callingIntent = getIntent();
        extras = callingIntent.getExtras();

        setUpMapView(savedInstanceState);
    }

    private void setUpMapView(Bundle savedInstanceState) {
        editMap = findViewById(R.id.map_view_edit_box);
        editMap.onCreate(savedInstanceState);
        editMap.getMapAsync(this);
    }

    private void setUpFinishButton() {
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
        if(extras != null){
            finishButton.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Intent intent = new Intent();
                    String coordinatesString = "Lat: - " + coordinates.latitude + " - Long: - " + coordinates.longitude;
                    intent.putExtra(DetailActivityConfig.MAP_BOX_CONTENT_KEY, coordinatesString);
                    intent.putExtra(DetailActivityConfig.POSITION_IN_LIST_KEY, extras.getInt(DetailActivityConfig.POSITION_IN_LIST_KEY));
                    setResult(RESULT_OK, intent);
                    finish();
                }
            });
        }
    }

    private void setUpSearchBar() {
        searchBar = findViewById(R.id.map_box_search_bar);
        searchButton = findViewById(R.id.map_box_search_button);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                searchForLocation();
            }
        });
    }

    private void searchForLocation() {
        String searchString = searchBar.getText().toString();
        searchBar.setText("");
        Geocoder geocoder = new Geocoder(this);
        List<Address> foundAdresses = new ArrayList<>();
        try {
            foundAdresses = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        if(foundAdresses.size() > 0){
            Address foundAdress = foundAdresses.get(0);
            double lat = foundAdress.getLatitude();
            double lng = foundAdress.getLongitude();
            coordinates = new LatLng(lat, lng);
            googleMap.clear();
            markerOptions.position(coordinates);
            googleMap.addMarker(markerOptions);
            googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
            editMap.onResume();
        } else{
            Toast.makeText(this, "No Address found", Toast.LENGTH_SHORT).show();
        }
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

        if(extras != null){
            coordinates = (LatLng) extras.get(DetailActivityConfig.EXISTING_CONTENT_KEY);
        }

        googleMap.setOnMarkerDragListener(this);
        markerOptions = new MarkerOptions().position(coordinates)
                .title("Marker")
                .draggable(true);
        googleMap.addMarker(markerOptions);
        zoom = 15;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
        this.googleMap = googleMap;

        setUpFinishButton();
        setUpSearchBar();

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
