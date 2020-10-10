package ur.mi.liebestagebuch.Boxes;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;

import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.R;

public class PictureBox implements Box{

    /*
     * Klasse dren Objekte eine Bildbox repräsentieren.
     * Bilder werden als Dateipfade gespeichert, aus denen dann Bitmaps ausgelesen werden.
     */

    //Pfad zum gewählten Bilds.
    private String path;
    //Das gewählte Bild als Bitmap.
    private Bitmap pictureBitmap;

    public PictureBox(String content){
        this.path = content;
        //Umwandlung des Strings in eine Bitmap.
        pictureBitmap = BitmapFactory.decodeFile(path);
    }

    @Override
    public String getString() {
        return path;
    }

    @Override
    public Type getType() {
        return Type.PICTURE;
    }

    @Override
    public View getView(Context context) {
        LayoutInflater inflater = LayoutInflater.from(context);
        View convertView = inflater.inflate(R.layout.picture_box_layout, null);
        ImageView imageView = (ImageView) convertView.findViewById(R.id.picture_box_image);
        imageView.setImageBitmap(pictureBitmap);
        return convertView;
    }

    @Override
    public void setContent(String content) {
        this.path = content;
        pictureBitmap = BitmapFactory.decodeFile(path);
    }

}
