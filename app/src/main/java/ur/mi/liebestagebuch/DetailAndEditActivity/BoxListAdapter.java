package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.List;

import ur.mi.liebestagebuch.Boxes.Box;

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

    public BoxListAdapter(ArrayList<Box> boxList, Context context){
        this.boxList = boxList;
        this.context = context;
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
