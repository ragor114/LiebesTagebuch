package ur.mi.liebestagebuch.Encryption;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.util.Base64;
import android.util.Log;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.concurrent.Executors;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Boxes.HeaderBox;
import ur.mi.liebestagebuch.Boxes.MapBox;
import ur.mi.liebestagebuch.Boxes.PictureBox;
import ur.mi.liebestagebuch.Boxes.SpotifyBox;
import ur.mi.liebestagebuch.Boxes.SpotifyBoxReadyListener;
import ur.mi.liebestagebuch.Boxes.TextBox;
import ur.mi.liebestagebuch.Boxes.Type;
import ur.mi.liebestagebuch.LoginActivity;

public class StringTransformHelper {
    /*
    * Diese Klasse soll statische Methoden bereitstellen, die helfen Arraylists von Boxen in Strings
    * um zu wandeln und Strings in Arraylists von Boxen. Außerdem soll die Klasse statische
    * Methoden zur Verschlüsselung von Strings in einem asynchronen Task bereitstellen.
    *
    * Entwickelt von Jannik Wiese
     */


    /*
     * Wandelt eine ArrayList von Boxen in einen String um.
     * Wenn Verschlüsselung deaktiviert ist kann die Rückgabe dieser Methode in die Datenbank eingefügt werden.
     * Eine neue Box beginnt immer mit |< und | trennt Typ von Inhalt .
     * @param Liste von Boxen die umgewandelt werden soll
     * @return Stringumwandlung der übergebenen Boxliste
    */
    public static String getStringFromBoxList (ArrayList<Box> boxes){
        String boxListString = "";

        for(Box current : boxes){
            boxListString += getStringFromBox(current);
        }

        return boxListString;
    }

    /*
    * Wandelt eine Box in die Form "|<Typ | Stringinhalt" um.
    * Neue Boxtypen müssen ergänzt werden, hier Verbesserung möglich.
    * Ist die Box von einem unbekannten Typ wird sie als Text gespeichert.
    * @param um zu wandelnde Box
    * @return Stringumwandlung der Box in der Form |<getType() | getString()
     */
    private static String getStringFromBox(Box box){
        String boxString = " < ";

        Type boxType = box.getType();
        //Log.d("TestConfigTest", "" + boxType);
        switch (boxType){
            case PICTURE:
                boxString += "Picture";
                break;
            case MAP:
                boxString += "Map";
                break;
            case MUSIC:
                boxString += "Music";
            case HEADER:
                boxString += "Header";
            default:
                boxString += "Text";
                break;
        }
        boxString += " | ";
        boxString += box.getString();

        return boxString;
    }

    /*
    * Gibt eine Arraylist von Boxen auf Basis eines Strings der richtigen Formattierung zurück
    * Der übergebene String muss unverschlüsselt sein.
    *
    * @param Liste der Boxen in String Form
    * @return Liste von Boxen auf Basis des Strings
     */
    public static ArrayList<Box> getBoxListFromString(String boxListString, SpotifyBoxReadyListener spotifyListener, Context context){
        Log.d("Detail", "Getting BoxList");
        //Log.d("TestConfigTest", "getBoxListFromString started");
        ArrayList<Box> boxList = new ArrayList<>();

        String[] singleBoxStrings = boxListString.split("\\Q < \\E");
        Log.d("Detail", "splitted 0: " + singleBoxStrings[0]);
        for(String current : singleBoxStrings){
            if(current.length() > 0) {
                Box currentNewBox = getSingleBoxFromString(current, spotifyListener, context);
                boxList.add(currentNewBox);
            }
            else continue;
        }

        Log.d("Detail", "Got Boxlist");

        return boxList;
    }

    /*
     * Umwandlung eines Strings der Form |<Typ | Inhalt in eine Box.
     * Neue Boxtypen müssen ergänzt werden
     *
     * @param String repräsentation einer Box
     * @return eine Box des entsprechenden Typs.
     */
    private static Box getSingleBoxFromString(String current, SpotifyBoxReadyListener spotifyListener, Context context) {
        Log.d("StringTransformHelper", current);
        String[] parts = current.split(" \\Q|\\E ");
        Log.d("Detail", "parts 0: " + parts[0]);
        Log.d("Detail", "parts 1: " + parts[1]);
        //Log.d("Detail", "parts 2: " + parts[2]);
        if(parts[0].contains("Picture")) {
            PictureBox newPictureBox = new PictureBox(parts[1]);
            return newPictureBox;
        } else if(parts[0].contains("Map")){
            MapBox newMapBox = new MapBox(parts[1]);
            return newMapBox;
        } else if(parts[0].contains("Music")){
            SpotifyBox newSpotifyBox = new SpotifyBox(parts[1], context, spotifyListener);
            return newSpotifyBox;
        } else if(parts[0].contains("Header")){
            HeaderBox newHeaderBox = new HeaderBox(parts[1]);
            return newHeaderBox;
        }

        TextBox newTextBox = new TextBox(parts[1]);
        return newTextBox;
    }

    /*
     * Erstellen eines Handlers für den Main-Thread und eines AsyncEncryptor-Objekts, dass die
     * Verschlüsselung in einem neuen Thread durchführt und den übergebenen Listener
     * über die Fertigstellung informiert.
     */
    public static void startEncryption (String toEncrypt, CryptoListener listener, Context context){
        startEncryptionWithNewPw(toEncrypt, listener, LoginActivity.correctPassword, context);
    }

    public static void startEncryptionWithNewPw(String toEncrypt, CryptoListener listener, String newPw, Context context){
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        AsyncEncryptor encryptor = new AsyncEncryptor(mainThreadHandler, listener, toEncrypt, newPw, context);
        Executors.newSingleThreadExecutor().submit(encryptor);
    }

    /*
     * Erstellen eines Handlers für den Main-Thread und eines AsyncDecryptor-Objekts, dass die
     * Entschlüsselung in einem neuen Thread durchführt und den übergebenen Listener
     * über die Fertigstellung informiert.
     */
    public static void startDecryption (String toDecrypt, CryptoListener listener, byte[] iv, byte[] salt, Context context){
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        AsyncDecryptor decryptor = new AsyncDecryptor(mainThreadHandler, listener, toDecrypt, LoginActivity.correctPassword, iv, salt, context);
        Executors.newSingleThreadExecutor().submit(decryptor);
    }

    /*
     * Zur Speicherung in der Datenbank wird das als Bitmap gespeicherte Bild komprimiert und in
     * ein Byte-Array umgewandelt, dass dann in einen String konvertiert wird, der in der Datenbank
     * gespeichert werden kann.
     */
    public static String convertBitmapToBase64String (Bitmap bitmap){
        Log.d("Detail", "Converting startetd");
        Bitmap bitmapCopy = bitmap.copy(Bitmap.Config.RGB_565, false);
        Log.d("Detail", "Copy made");
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmapCopy.compress(Bitmap.CompressFormat.PNG, 100, baos);
        Log.d("Detail", "Compressed");
        byte[] bitmapAsBytes = baos.toByteArray();
        String base64BitmapString = Base64.encodeToString(bitmapAsBytes, Base64.DEFAULT);
        Log.d("Detail", "Encoded to String");

        //Aufräumen um Speicher zu sparen:
        try {
            baos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        //bitmap.recycle();
        //bitmapCopy.recycle();
        return base64BitmapString;
    }

    /*
     * Aus einem String der durch die Methode convertBitmaoToBase64String generiert wurde kann mit
     * dieser Methode wieder ein Bild generiert werden, dass in einem ImageView angezeigt werden
     * kann.
     */
    public static Bitmap convertBase64StringToBitmap (String bitmapString){
        byte[] bitmapAsBytes = Base64.decode(bitmapString, Base64.DEFAULT);
        Bitmap bitmap = BitmapFactory.decodeByteArray(bitmapAsBytes, 0, bitmapAsBytes.length);
        return bitmap;
    }

}
