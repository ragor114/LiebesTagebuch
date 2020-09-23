package ur.mi.liebestagebuch.EditActivities;

import android.Manifest;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.R;

public class EditMapBoxActivity extends AppCompatActivity implements OnMapReadyCallback {

    private MapView editMap;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.create_mapbox_activity);

        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION};
        requestPermissions(permissions, DetailActivityConfig.PERMISSION_REQUEST_CODE);

        editMap = findViewById(R.id.map_view_edit_box);
        editMap.onCreate(savedInstanceState);
        editMap.getMapAsync(this);
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
        LatLng coordinates = new LatLng(49,12);
        googleMap.addMarker(new MarkerOptions().position(coordinates).title("Marker"));
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinates, 15));
        editMap.onResume();
    }
}
