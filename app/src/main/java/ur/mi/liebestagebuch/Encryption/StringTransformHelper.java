package ur.mi.liebestagebuch.Encryption;

import android.os.Handler;
import android.os.Looper;

import java.util.ArrayList;
import java.util.concurrent.Executors;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Boxes.PictureBox;
import ur.mi.liebestagebuch.Boxes.TextBox;
import ur.mi.liebestagebuch.Boxes.Type;

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
        String boxString = "|<";

        Type boxType = box.getType();
        //Log.d("TestConfigTest", "" + boxType);
        switch (boxType){
            case PICTURE:
                boxString += "Picture";
                break;
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
    public static ArrayList<Box> getBoxListFromString(String boxListString){
        //Log.d("TestConfigTest", "getBoxListFromString started");
        ArrayList<Box> boxList = new ArrayList<>();

        String[] singleBoxStrings = boxListString.split("\\Q|\\E<");
        //Log.d("TestConfigTest", "splitted" + singleBoxStrings[1]);
        for(String current : singleBoxStrings){
            if(current.length() > 0) {
                Box currentNewBox = getSingleBoxFromString(current);
                boxList.add(currentNewBox);
            }
            else continue;
        }

        return boxList;
    }

    /*
     * Umwandlung eines Strings der Form |<Typ | Inhalt in eine Box.
     * Neue Boxtypen müssen ergänzt werden
     *
     * @param String repräsentation einer Box
     * @return eine Box des entsprechenden Typs.
     */
    private static Box getSingleBoxFromString(String current) {
        //Log.d("StringTransformHelper", current);
        String[] parts = current.split("\\Q|\\E");
        if(parts[0].contains("Picture")) {
            PictureBox newPictureBox = new PictureBox(parts[1]);
            return newPictureBox;
        }

        TextBox newTextBox = new TextBox(parts[1]);

        return newTextBox;
    }

    /*
     * Erstellen eines Handlers für den Main-Thread und eines AsyncEncryptor-Objekts, dass die
     * Verschlüsselung in einem neuen Thread durchführt und den übergebenen Listener
     * über die Fertigstellung informiert.
     */
    public static void startEncryption (String toEncrypt, CryptoListener listener){
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        AsyncEncryptor encryptor = new AsyncEncryptor(mainThreadHandler, listener, toEncrypt, TestConfig.TEST_ENCRYPTED_PASSWORD);
        Executors.newSingleThreadExecutor().submit(encryptor);
    }

    /*
     * Erstellen eines Handlers für den Main-Thread und eines AsyncDecryptor-Objekts, dass die
     * Entschlüsselung in einem neuen Thread durchführt und den übergebenen Listener
     * über die Fertigstellung informiert.
     */
    public static void startDecryption (String toDecrypt, CryptoListener listener, byte[] iv, byte[] salt){
        Handler mainThreadHandler = new Handler(Looper.getMainLooper());
        AsyncDecryptor decryptor = new AsyncDecryptor(mainThreadHandler, listener, toDecrypt, TestConfig.TEST_ENCRYPTED_PASSWORD, iv, salt);
        Executors.newSingleThreadExecutor().submit(decryptor);
    }

}
