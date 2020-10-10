package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.content.Context;
import android.util.Log;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Boxes.SpotifyBoxReadyListener;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.R;
import ur.mi.liebestagebuch.Settings.CheckEncryptionSettingHelper;
import ur.mi.liebestagebuch.database.data.DBEntry;

public class EntryDetail implements CryptoListener {

    /*
     * Ein EntryDetail-Objekt bekommt bei der Initialisierung ein Entry-Objekt aus der Datenbank
     * übergeben und holt daraus die wichtigen Werte. Das EntryDetail verwaltet eine ArrayList von
     * Boxen, die Emotion dieses Eintrags und das Datum und bietet Methoden, um auf die entsprechenden
     * Werte in sinnvoller Form zugreifen zu können. Mithilfe des BoxListEncryptionListeners kann
     * das EntryDetail-Objekt auch der aufrufenden Activity die wieder verschlüsselte String-
     * Repräsentation der Box-Liste übermitteln.
     *
     * Entwickelt von Jannik Wiese.
     */

    private ArrayList<Box> boxList;
    private Emotion emotion;
    private Date entryDate;

    private BoxListEncryptionListener listener;
    private SpotifyBoxReadyListener spotifyListener;
    private Context context;

    //Initialisierungs-Methoden:

    public EntryDetail(DBEntry dbEntry, BoxListEncryptionListener listener, SpotifyBoxReadyListener spotifyListener, Context context){
        this.listener = listener;
        this.spotifyListener = spotifyListener;
        this.context = context;
        Log.d("Detail", "Creating Entry Detail");
        this.entryDate = dbEntry.getDate();
        setEmotion(dbEntry);

        if(CheckEncryptionSettingHelper.encryptionActivated(context)){
            startContentDecryption(dbEntry);
        } else{
            Log.d("Passwort", "Encryption in Entry Detail not activated");
            String contentString = dbEntry.getContent();
            Log.d("Passwort", "Content String is: " + contentString);
            this.boxList = StringTransformHelper.getBoxListFromString(contentString, spotifyListener, context);
            Log.d("Passwort", "Boxlist is: " + boxList.toString());
        }
    }

    // Die für die Entschlüsselung nötigen Informationen werden aus dem Datenbank-Entry geladen und
    // die asynchrone Entschlüsselung gestartet:
    private void startContentDecryption(DBEntry dbEntry) {
        String encryptedBoxString = dbEntry.getContent();
        byte[] salt = dbEntry.getSalt();
        byte[] iv = dbEntry.getIv();
        StringTransformHelper.startDecryption(encryptedBoxString, this, iv, salt, context);
    }

    // Da die Emotion in der Datenbank als int gespeichert wird muss die korrespondierende Emotion
    // über einen switch gefunden werden:
    private void setEmotion(DBEntry dbEntry) {
        int emotionInt = dbEntry.getEmotions();
        switch (emotionInt){
            case 0:
                this.emotion = Emotion.VERY_GOOD;
                break;
            case 1:
                this.emotion = Emotion.GOOD;
                break;
            case 2:
                this.emotion = Emotion.NORMAL;
                break;
            case 3:
                this.emotion = Emotion.BAD;
                break;
            case 4:
                this.emotion = Emotion.VERY_BAD;
                break;
        }
    }

    //Getter- und Setter-Methoden:
    public ArrayList<Box> getBoxList(){
        return boxList;
    }

    public Box getBoxFromBoxList(int id){
        return boxList.get(id);
    }

    public int addBoxToBoxList(Box box){
        boxList.add(box);
        return boxList.indexOf(box);
    }

    public Emotion getEmotion(){
        return emotion;
    }

    public int getEmotionInt(){
        switch(emotion){
            case VERY_GOOD:
                return 0;
            case GOOD:
                return 1;
            case NORMAL:
                return 2;
            case BAD:
                return 3;
            case VERY_BAD:
                return 4;
        }
        return 2;
    }

    public void setEmotion(Emotion emotion){
        this.emotion = emotion;
        Log.d("Detail", "Set Emotion to " + this.emotion);
    }

    public Date getDate(){
        return entryDate;
    }

    public String getDateString(){
        Log.d("Detail","Getting Datestring");
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
        return dateFormat.format(entryDate);
    }

    public String getBoxListString(){
        return StringTransformHelper.getStringFromBoxList(boxList);
    }

    //CryptoListener-Methoden:

    // Das Entry-Detail entschlüsselt nur und verschlüsselt nicht!
    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        return;
    }

    // Wenn der String aus der Datenbank entschlüsselt wurde, wird daraus eine Arraylist von Boxen
    // gemacht und dem listener mitgeteilt, dass die Entschlüsselung abgeschlossen ist.
    @Override
    public void onDecryptionFinished(String result) {
        Log.d("Detail", "Decryption finished");
        Log.d("Detail", "Decrypted String: " + result);
        this.boxList = StringTransformHelper.getBoxListFromString(result, spotifyListener, context);
        listener.onBoxListDecryptionFinished();
    }

    @Override
    public void onEncryptionFailed() {
        return;
    }

    @Override
    public void onDecryptionFailed(){
        listener.onEncryptionFailed(context.getResources().getInteger(R.integer.decryption_failed_code));
    }
}
