package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import ur.mi.liebestagebuch.R;

public class MapBox implements Box {

    /*
     * Eine MapBox repräsentiert in der Liste eine gespeicherte GoogleMap. Da es laut Google nicht
     * wirklich möglich ist mehrere GoogleMaps in einer Activity an zu zeigen (es kann dabei zu vielen
     * Fehlern kommen) zeigt die MapBox nur die Koordinaten und die durch Geocoding erhaltene
     * Addresse an.
     *
     * Entwickelt von Jannik Wiese.
     */

    private String content;
    public LatLng coordinates;

    // Dem Konstruktor wird ein LatLng-Objekt übergeben, zu dem dann eine Stringrepräsentation erstellt wird.
    public MapBox(LatLng coordinates) {
        this.coordinates = coordinates;
        content = "Lat: - " + coordinates.latitude + " - Long: - " + coordinates.longitude;
        String[] splits = content.split(" - ");
        //Log.d("MapView", "0:" + splits[0] + " 1:" + splits[1] + " 2:" + splits[2] + " 3:" + splits[3]);
    }

    // Dem Konstruktor kann auch ein String übergeben werden, aus dem dann ein LatLng-Objekt generiert wird.
    public MapBox(String content) {
        setContent(content);
    }

    // Gespeichert wird die String-Repräsentation des LatLng-Objekts
    @Override
    public String getString() {
        return content;
    }

    @Override
    public Type getType() {
        return Type.MAP;
    }

    @Override
    public View getView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.map_box_layout, null);
        TextView latView = convertView.findViewById(R.id.map_box_lat);
        TextView lngView = convertView.findViewById(R.id.map_box_lng);
        TextView geocodingView = convertView.findViewById(R.id.map_box_geocoding);

        latView.setText("Lat: " + coordinates.latitude);
        lngView.setText("Long: " + coordinates.longitude);

        try {
            String addressString = getGeocodedString(context);

            if (addressString.equals("")) {
                geocodingView.setText("No corresponding Address found.");
            } else {
                geocodingView.setText(addressString);
            }

        } catch (IOException e) {
            geocodingView.setText("No corresponding Address found.");
            e.printStackTrace();
        }

        return convertView;
    }

    /*
     * Mithilfe eines Geocoders werden aus den Koordinaten Adressen gemacht und der Adressstring
     * sofern bestimmte Informationen vorhanden sind immer erweitert.
     * Sollte keine Adresse gefunden werden können wird der Inhalt des TextViews (in der getView()-
     * Methode) auf "No corresponding Address found." gesetzt.
     */
    private String getGeocodedString(Context context) throws IOException {
        Geocoder geocoder = new Geocoder(context);
        List<Address> adresses = geocoder.getFromLocation(coordinates.latitude, coordinates.longitude, 1);
        Address address = adresses.get(0);
        String addressString = "";

        //Log.d("Maps", "Address found: " + address.toString());

        if (address.getCountryName() != null) {
            addressString += address.getCountryName();
        }
        if (address.getLocality() != null) {
            if (addressString.equals("")) {
                addressString += address.getLocality();
            } else {
                addressString += ", " + address.getLocality();
            }
        }
        if (address.getAdminArea() != null) {
            if (addressString.equals("")) {
                addressString += address.getAdminArea();
            } else {
                addressString += ", " + address.getAdminArea();
            }
        }
        if (address.getThoroughfare() != null) {
            if (address.equals("")) {
                addressString += address.getThoroughfare();
            } else {
                addressString += ", " + address.getThoroughfare();
            }
        }
        if (address.getFeatureName() != null) {
            if (addressString.equals("")) {
                addressString += address.getFeatureName();
            } else {
                addressString += ", " + address.getFeatureName();
            }
        }

        return addressString;
    }

    /*
     * Wenn der Inhaltsstring geändert wird ändert diese Methode das LatLng-Objekt. Wichtig dabei ist
     * nur, dass das Format "Lat - <Zahl> - Long - <Zahl>" eingahalten wird.
     */
    @Override
    public void setContent(String content) {
        this.content = content;
        String[] splits = content.split(" - ");
        double lat = Double.parseDouble(splits[1]);
        double lng = Double.parseDouble(splits[3]);
        coordinates = new LatLng(lat, lng);
    }
}
