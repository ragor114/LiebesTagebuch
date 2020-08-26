package ur.mi.liebestagebuch.GridView;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import java.util.ArrayList;

public class EntryGridAdapter extends BaseAdapter {

    /*
     * Der EntryGridAdapter schließt eine Arraylist von Entries an ein GridView an und gibt für jedes
     * Entry-Element der Arraylist ein auf grid_element.xml basierenden View zurück.
     *
     * Entwickelt von Moritz Schnell und Jannik Wiese.
     */

    private ArrayList<Entry> entries;
    private Context context;

    public EntryGridAdapter(ArrayList <Entry> entries, Context context){
        this.entries = entries;
        this.context = context;
    }

    @Override
    public int getCount() {
        return entries.size();
    }

    @Override
    public Object getItem(int position) {
        return entries.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {


        return null;
    }
}
