package ur.mi.liebestagebuch.Encryption;

import java.util.ArrayList;

import ur.mi.liebestagebuch.Boxes.Box;

public class StringTransformHelper {
    /*
    * Diese Klasse soll statische Methoden bereitstellen, die helfen Arraylists von Boxen in Strings
    * um zu wandeln und Strings in Arraylists von Boxen. Außerdem soll die Klasse statische
    * Methoden zur Verschlüsselung von Strings in einem asynchronen Task bereitstellen.
    *
    * Entwickelt von Jannik Wiese
     */

    // Wandelt eine ArrayList von Boxen in einen String um.
    // Wenn Verschlüsselung deaktiviert ist kann die Rückgabe dieser Methode in die Datenbank eingefügt werden.
    public static String getStringFromBoxList (ArrayList<Box> boxes){
        String boxListString = "";

        for(Box current : boxes){
            boxListString += getStringFromBox(current);
        }

        return boxListString;
    }

    private static String getStringFromBox(Box box){

    }
}
