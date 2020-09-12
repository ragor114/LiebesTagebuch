package ur.mi.liebestagebuch.database.data;

import android.util.Log;

import androidx.room.ColumnInfo;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

import java.util.Arrays;
import java.util.Date;


@Entity(tableName = "diary")
public class Entry{

    @PrimaryKey(autoGenerate = true)
    private int uid;

    @ColumnInfo(name = "date")
    private Date date;

    @ColumnInfo(name = "emotion")
    private int emotions;

    @ColumnInfo(name = "content")
    private String content;

    @ColumnInfo(name = "salt")
    private byte[] salt;

    @ColumnInfo(name = "IV")
    private byte[] iv;

    public Entry(Date date, int emotions, String content, byte[] salt, byte[] iv) {
        this.date = date;
        this.emotions = emotions;
        this.content = content;
        this.salt = salt;
        this.iv = iv;
        Log.d("Encryption", "IV-Length before Store is " + iv.length);
    }

    public int getEmotions() {
        return emotions;
    }

    public void setEmotions(int emotions) {
        this.emotions = emotions;
    }

    public void setUid(int uid) {
        this.uid = uid;
    }

    public int getUid() {
        return uid;
    }

    public Date getDate() {
        return date;
    }

    public void setDate(Date date) {
        this.date = date;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public byte[] getSalt() {
        return salt;
    }

    public void setSalt(byte[] salt) {
        this.salt = salt;
    }

    public byte[] getIv() {
        return iv;
    }

    public void setIv(byte[] iv) {
        this.iv = iv;
    }

    @Override
    public String toString() {
        return "Entry{" +
                "uid=" + uid +
                ", date=" + date +
                ", emotions=" + emotions +
                ", content='" + content + '\'' +
                ", salt=" + Arrays.toString(salt) +
                ", iv=" + Arrays.toString(iv) +
                '}';
    }
}
