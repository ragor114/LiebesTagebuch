package ur.mi.liebestagebuch.GridView;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ur.mi.liebestagebuch.R;

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


    //TODO: EmotionColorView muss durch einen Smiley in der entsprechenden Farbe ersetzt werden.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Entry currentEntry = entries.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        View gridElementView = inflater.inflate(R.layout.grid_element, null);

        TextView dateTextView = (TextView) gridElementView.findViewById(R.id.date_text);
        View emotionColorView = gridElementView.findViewById(R.id.emotion_color_view);

        emotionColorView.setBackgroundColor(getColorResourceForEmotion(currentEntry.getEmotion()));

        Date entryDate = currentEntry.getDate();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(entryDate);
        dateTextView.setText(dateString);

        return gridElementView;
    }

    private int getColorResourceForEmotion(Emotion emotion){
        int colorId = 0;
        switch(emotion){
            case VERY_GOOD:
                colorId = R.color.emotion_very_good;
                break;
            case GOOD:
                colorId = R.color.emotion_good;
                break;
            case BAD:
                colorId = R.color.emotion_bad;
                break;
            case VERY_BAD:
                colorId = R.color.emotion_very_bad;
                break;
            default:
                colorId = R.color.emotion_normal;
                break;
        }
        return colorId;
    }
}
