package ur.mi.liebestagebuch.GridView;

import java.util.Date;

public class Entry {

    private Emotion emotion;
    private Date date;

    public Entry(Date date){
        this.date = date;
        emotion = Emotion.NORMAL;
    }

    public Date getDate(){
        return date;
    }

    public void setEmotion (Emotion emotion){
        this.emotion = emotion;
    }

    public Emotion getEmotion (){
        return emotion;
    }

}
