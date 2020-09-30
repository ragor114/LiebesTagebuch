package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import retrofit.http.HEAD;
import ur.mi.liebestagebuch.R;

public class HeaderBox implements Box {

    /*
     * Eine HeaderBox beinhaltet eine Überschrift in Form eines TextViews.
     *
     * Entwickelt von Jannik Wiese.
     */

    //Die Überschrift:
    private String header;

    // Der Text der Überschrift wird im Konsruktor übergeben.
    public HeaderBox(String header){
        this.header = header;
    }

    //In die Datenbank wird der Text der Überschrift gespeichert.
    @Override
    public String getString() {
        return header;
    }

    @Override
    public Type getType() {
        return Type.HEADER;
    }

    // Das Layout einer Headerbox wird inflated und der Inhalt des TextViews auf den Inhalt der Headerbox gesetzt.
    @Override
    public View getView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.header_box_layout, null);
        TextView headerView = convertView.findViewById(R.id.header_box_text);
        headerView.setText(header);

        return convertView;
    }

    @Override
    public void setContent(String content) {
        this.header = content;
    }
}
