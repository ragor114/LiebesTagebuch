package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;

import java.util.ArrayList;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Boxes.Type;
import ur.mi.liebestagebuch.R;

public class BoxListAdapter extends BaseAdapter {

    /*
     * Der BoxListAdapter schließt eine Liste von Boxen an ein ListView an.
     * Dafür gibt getView() für jedes Listenlement den Rückgabewert von getView der Box
     * zurück.
     *
     * Entwickelt von Jannik Wiese.
     */

    private ArrayList<Box> boxList;
    private Context context;
    private boolean lastMapViewReady;
    private MapView lastMapView;

    public BoxListAdapter(ArrayList<Box> boxList, Context context){
        this.boxList = boxList;
        this.context = context;
        lastMapViewReady = true;
    }

    @Override
    public int getCount() {
        return boxList.size();
    }

    @Override
    public Object getItem(int position) {
        return boxList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        return boxList.get(position).getView(context);
    }

}
