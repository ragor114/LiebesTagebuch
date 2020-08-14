package ur.mi.liebestagebuch.Encryption;

import java.util.ArrayList;

import ur.mi.liebestagebuch.Boxes.Box;
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
    * @return Stringumwandlung der Box in der Form |<Type | inhalt
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
}
