package ur.mi.liebestagebuch.Encryption;

import java.util.ArrayList;

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
    private static ArrayList<Box> getBoxListFromString(String boxListString){
        ArrayList<Box> boxList = new ArrayList<>();

        String[] singleBoxStrings = boxListString.split("\\Q|\\E<");
        for(String current : singleBoxStrings){
            Box currentNewBox = getSingleBoxFromString(current);
            boxList.add(currentNewBox);
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
        String[] parts = current.split(" \\Q|\\E ");
        if(parts[0].equals("Picture")) {
            PictureBox newPictureBox = new PictureBox(parts[1]);
            return newPictureBox;
        }

        TextBox newTextBox = new TextBox(parts[1]);

        return newTextBox;
    }
}
