package ur.mi.liebestagebuch.Boxes;

import android.view.View;

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
    public View getView() {
        return null;
    }
}
