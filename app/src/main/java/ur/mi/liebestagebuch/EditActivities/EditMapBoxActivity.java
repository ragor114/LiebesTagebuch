package ur.mi.liebestagebuch.EditActivities;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

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

    /*
     * Die EditMapBoxActivity dient dazu eine MapBox zu erstellen in dem man entweder einen Ort
     * mittels Geocoding sucht oder einen Marker auf die entsprechende Position zieht.
     *
     * Entwickelt von Jannik Wiese.
     */

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
        requestPermissions(permissions, getResources().getInteger(R.integer.permission_request_code));

        Intent callingIntent = getIntent();
        extras = callingIntent.getExtras();

        setUpMapView(savedInstanceState);
    }

    // Das MapView wird gespeichert und die GoogleMap asynchron geladen.
    private void setUpMapView(Bundle savedInstanceState) {
        editMap = findViewById(R.id.map_view_edit_box);
        editMap.onCreate(savedInstanceState);
        editMap.getMapAsync(this);
    }

    /*
     * Der finishButton beendet die Activity und meldet an die aufrufende Activity entweder die
     * Koordinaten oder zur Bearbeitung die Koordinaten als String und die Position der Box
     * in der Liste zurück.
     */
    private void setUpFinishButton() {
        finishButton = findViewById(R.id.finish_map_edit);
        finishButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                if(extras != null){
                    String coordinatesString = "Lat: - " + coordinates.latitude + " - Long: - " + coordinates.longitude;
                    intent.putExtra(getString(R.string.mapbox_content_key), coordinatesString);
                    intent.putExtra(getString(R.string.position_in_list_key), extras.getInt(getString(R.string.position_in_list_key)));
                } else{
                    intent.putExtra(getString(R.string.mapbox_content_key), coordinates);
                }
                setResult(RESULT_OK, intent);
                finish();
            }
        });
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

    /*
     * Die Eingabe in die Suchleiste werden in einem String gespeichert und ein Geocoder versucht
     * auf Basis der Eingabe Koordinaten zu finden.
     * Wird eine Position gefunden, so wird die Map zurück gesetzt und ein neuer Marker an der
     * entsprechenden Position platziert und die Map auf so verschoben, dass der Marker wieder
     * in der Mitte sitzt.
     * Wird keine Position gefunden erscheint eine Toast Nachricht.
     */
    private void searchForLocation() {
        String searchString = searchBar.getText().toString();
        searchBar.setText("");
        Geocoder geocoder = new Geocoder(this);
        List<Address> foundAdresses = new ArrayList<>();
        try {
            foundAdresses = geocoder.getFromLocationName(searchString, 1);
        } catch (IOException e) {
           Log.d("Maps", e.toString());
        }
        if(foundAdresses.size() > 0){
            Address foundAdress = foundAdresses.get(0);
            double lat = foundAdress.getLatitude();
            double lng = foundAdress.getLongitude();
            coordinates = new LatLng(lat, lng);
            googleMap.clear();
            setUpMarkerOnMap();
        } else{
            Toast.makeText(this, "No Address found", Toast.LENGTH_SHORT).show();
        }
    }

    //MapsViews müssen über alle Änderungen im Lifecycle informiert werden:
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

    /*
     * Wenn die GoogleMap asynchron geladen wurde werden die Koordinaten entweder auf die übergebenen
     * Koordinaten oder die letzte bekannte Position des Geräts gesetzt. Dort wird ein Marker platziert.
     * Erst wenn die GoogleMap fertig ist, können der FinishButton und die Suchleiste verwendet
     * werden.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d("MapView", "Map is showing");

        if(extras != null){
            coordinates = (LatLng) extras.get(getString(R.string.existing_content_key));
        } else{
            setCoordinatesToDevicePosition();
        }

        this.googleMap = googleMap;
        setUpMarkerOnMap();

        setUpFinishButton();
        setUpSearchBar();
    }

    // Ein Marker wird an den gespeicherten Koordinaten auf der Map platziert und die Map auf
    // diesen zentriert.
    private void setUpMarkerOnMap() {
        googleMap.setOnMarkerDragListener(this);
        markerOptions = new MarkerOptions().position(coordinates)
                .title("Marker")
                .draggable(true);
        googleMap.addMarker(markerOptions);
        zoom = 15;
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, zoom));
        editMap.onResume();
    }

    /*
     * Sind die notwendigen Berechtigungen zum abfragen des Standorts vorhanden wird der bestmögliche
     * Provider gesucht und mit Hilfe von diesem die letzte Position des Geräts bestimmt.
     * Die gespeicherten Koordinaten werden auf diese Position gesetzt und falls eine GoogleMap schon
     * vorhanden ist wird der Marker an dieser Position platziert.
     * Sind die Berechtigungen nicht vorhanden werden die Koordinaten auf Standardwerte gesetzt und
     * eine Toast-Nachricht angezeigt.
     */
    private void setCoordinatesToDevicePosition() {
        if(ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED){
            Criteria criteria = new Criteria();
            criteria.setAccuracy(Criteria.ACCURACY_FINE);
            criteria.setPowerRequirement(Criteria.POWER_MEDIUM);
            criteria.setAltitudeRequired(false);
            criteria.setBearingRequired(false);
            criteria.setSpeedRequired(false);
            LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
            String bestProvider = locationManager.getBestProvider(criteria, true);
            Location lastLoc = locationManager.getLastKnownLocation(bestProvider);
            coordinates = new LatLng(lastLoc.getLatitude(), lastLoc.getLongitude());
            if(googleMap != null){
                googleMap.clear();
                setUpMarkerOnMap();
            }
        } else{
            coordinates = new LatLng(49,12);
            Toast.makeText(this, "Please grant location permissions.", Toast.LENGTH_SHORT).show();
        }
    }

    /*
     * Wenn die Berechtigungsanfrage für den Standort beantwortet wurde und keine Koordinaten übergeben
     * wurden, werden die Koordinaten auf die letzte bekannte Position gesetzt.
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == getResources().getInteger(R.integer.permission_request_code)){
            if(extras == null) {
                setCoordinatesToDevicePosition();
            }
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
    }

    @Override
    public void onMarkerDrag(Marker marker) {
    }

    /*
     * Wenn der Marker bewegt wurde, wird die Position des Markers gespeichert und die Karte auf den
     * Marker zentriert.
     */
    @Override
    public void onMarkerDragEnd(Marker marker) {
        coordinates = marker.getPosition();
        googleMap.moveCamera(CameraUpdateFactory.newLatLng(coordinates));
        editMap.onResume();
        Log.d("MapView", "End: Lat: " + coordinates.latitude + " Long: " + coordinates.longitude);
    }
}
