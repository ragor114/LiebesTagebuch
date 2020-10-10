package ur.mi.liebestagebuch.GridView;

import java.util.Date;

public class GridEntry {

    private Emotion emotion;
    private Date date;

    public GridEntry(Date date) {
        this.date = date;
        emotion = Emotion.NORMAL;
    }

    public Date getDate() {
        return date;
    }

    public void setEmotion(Emotion emotion) {
        this.emotion = emotion;
    }

    public Emotion getEmotion() {
        return emotion;
    }

}
