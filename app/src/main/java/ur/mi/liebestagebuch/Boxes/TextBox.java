package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import ur.mi.liebestagebuch.R;

public class TextBox implements Box{

    /*
     * Klasse, deren Objekte eine TextBox repräsentieren.
     * getView() muss noch implementiert werden!
     */

    // Inhalt der Textbox:
    private String content;

    public TextBox(String content){
        this.content = content;
    }

    @Override
    public String getString() {
        return content;
    }

    @Override
    public Type getType() {
        return Type.TEXT;
    }

    //Muss noch implementiert werden!
    @Override
    public View getView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.text_box_layout, null);
        TextView textView = (TextView) convertView.findViewById(R.id.text_box_text);
        textView.setText(content);
        return convertView;
    }

    @Override
    public void setContent(String content) {
        this.content = content;
    }
}
