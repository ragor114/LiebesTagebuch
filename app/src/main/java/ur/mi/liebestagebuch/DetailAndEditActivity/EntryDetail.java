package ur.mi.liebestagebuch.DetailAndEditActivity;

import android.util.Log;
import android.view.View;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ur.mi.liebestagebuch.Boxes.Box;
import ur.mi.liebestagebuch.Encryption.CryptoListener;
import ur.mi.liebestagebuch.Encryption.StringTransformHelper;
import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.database.data.Entry;

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

    //Initialisierungs-Methoden:

    public EntryDetail(Entry dbEntry, BoxListEncryptionListener listener){
        this.listener = listener;
        Log.d("Detail", "Creating Entry Detail");
        this.entryDate = dbEntry.getDate();
        setEmotion(dbEntry);
        startContentDecryption(dbEntry);
        /*if(isNew == false) {
            Log.d("Detail", "Entry is not new");
            startContentDecryption(dbEntry);
        } else {
            Log.d("Detail", "Entry is New");
            this.boxList = StringTransformHelper.getBoxListFromString(dbEntry.getContent());
        }
        */
    }

    private void startContentDecryption(Entry dbEntry) {
        String encryptedBoxString = dbEntry.getContent();
        byte[] salt = dbEntry.getSalt();
        byte[] iv = dbEntry.getIv();
        StringTransformHelper.startDecryption(encryptedBoxString, this, iv, salt);
    }

    private void setEmotion(Entry dbEntry) {
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

    public void setBoxList(ArrayList<Box> boxList){
        this.boxList = boxList;
    }

    public Box getBoxFromBoxList(int id){
        return boxList.get(id);
    }

    public void addBoxToBoxList(Box box){
        boxList.add(box);
    }

    /*public View getViewFromBox(int id){
        return boxList.get(id).getView();
    }
     */

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

    public void encryptBoxList(){
        String boxListString = getBoxListString();
        StringTransformHelper.startEncryption(boxListString, this);
    }

    public String getBoxListString(){
        return StringTransformHelper.getStringFromBoxList(boxList);
    }

    //CryptoListener-Methoden:

    //Hier unnötig
    @Override
    public void onEncryptionFinished(String result, byte[] iv, byte[] salt) {
        //nignweou
    }

    @Override
    public void onDecryptionFinished(String result) {
        Log.d("Detail", "Decryption finished");
        Log.d("Detail", "Decrypted String: " + result);
        this.boxList = StringTransformHelper.getBoxListFromString(result);
        listener.onBoxListDecryptionFinished();
    }

    //TODO: Make usefull
    @Override
    public void onEncryptionFailed() {

    }

    //TODO: Make helpful
    @Override
    public void onDecryptionFailed() {

    }
}
