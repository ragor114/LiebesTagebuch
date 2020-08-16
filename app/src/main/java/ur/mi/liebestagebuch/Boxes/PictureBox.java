package ur.mi.liebestagebuch.Boxes;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

public class PictureBox implements Box{

    /*
     * Klasse dren Objekte eine Bildbox repräsentieren.
     * getView() muss noch implementiert werden!
     * Umwandlung des Strings in eine Uri und ein Bitmap muss implementiert werden!
     */

    //String repräsentation des Dateipfads zum gewählten Bild.
    private String content;
    //Uri zum gewählten Bild.
    private Uri pictureUri;
    //Das gewählte Bild als Bitmap.
    private Bitmap pictureBitmap;

    public PictureBox(String content){
        this.content = content;
        //Umwandlung des Strings in eine Uri und eine Bitmap.
    }

    @Override
    public String getString() {
        return content;
    }

    @Override
    public Type getType() {
        return Type.PICTURE;
    }

    //muss noch implementiert werden!
    @Override
    public View getView() {
        return null;
    }

}
