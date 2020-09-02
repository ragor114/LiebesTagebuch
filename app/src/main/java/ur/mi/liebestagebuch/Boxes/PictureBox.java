package ur.mi.liebestagebuch.Boxes;

import android.graphics.Bitmap;
import android.net.Uri;
import android.view.View;

import ur.mi.liebestagebuch.Encryption.StringTransformHelper;

public class PictureBox implements Box{

    /*
     * Klasse dren Objekte eine Bildbox repräsentieren.
     * getView() muss noch implementiert werden!
     * Umwandlung des Strings in eine Uri und ein Bitmap muss implementiert werden!
     */

    //String Repräsentation des gewählten Bilds.
    private String content;
    //Das gewählte Bild als Bitmap.
    private Bitmap pictureBitmap;

    public PictureBox(String content){
        this.content = content;
        //Umwandlung des Strings in eine Bitmap.
        pictureBitmap = StringTransformHelper.convertBase64StringToBitmap(content);
    }

    @Override
    public String getString() {
        return StringTransformHelper.convertBitmapToBase64String(pictureBitmap);
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
