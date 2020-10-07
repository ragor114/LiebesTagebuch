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

    private ArrayList<GridEntry> entries;
    private Context context;

    public EntryGridAdapter(ArrayList <GridEntry> entries, Context context){
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


    //Check: EmotionColorView muss durch einen Smiley in der entsprechenden Farbe ersetzt werden.
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        GridEntry currentGridEntry = entries.get(position);

        LayoutInflater inflater = LayoutInflater.from(context);
        View gridElementView = inflater.inflate(R.layout.grid_element, null);

        TextView dateTextView = (TextView) gridElementView.findViewById(R.id.date_text);
        View emotionColorView = gridElementView.findViewById(R.id.emotion_color_view);

        emotionColorView.setBackground(context.getDrawable(getColorResourceForEmotion(currentGridEntry.getEmotion())));
        //emotionColorView.setBackgroundColor(context.getColor(getColorResourceForEmotion(currentEntry.getEmotion())));


        Date entryDate = currentGridEntry.getDate();
        DateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        String dateString = dateFormat.format(entryDate);
        dateTextView.setText(dateString);

        return gridElementView;
    }

    private int getColorResourceForEmotion(Emotion emotion){
        int colorId = 0;
        switch(emotion){
            case VERY_GOOD:
                colorId = R.drawable.emoji_sehr_glucklich;
                break;
            case GOOD:
                colorId = R.drawable.emoji_glucklich;
                break;
            case BAD:
                colorId = R.drawable.emoji_traurig;
                break;
            case VERY_BAD:
                colorId = R.drawable.emoji_sehr_traurig;
                break;
            default:
                colorId = R.drawable.emoji_neutral;
                break;
        }
        return colorId;
    }

}
