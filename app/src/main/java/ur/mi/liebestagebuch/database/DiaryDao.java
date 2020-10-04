package ur.mi.liebestagebuch.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.database.data.Entry;

@Dao
public interface DiaryDao {

    @Insert
    void insert (Entry entry);

    @Query("SELECT * FROM diary")
    List<Entry> getAll();

    @Update
    void update(Entry entry);

    @Query("DELETE FROM diary")
    void clear();

    @Query("SELECT emotion FROM diary")
    List<Integer> getAllEmotions();

    @Query("SELECT * FROM diary ORDER BY uid DESC LIMIT 1")
    Entry getNewest();

    @Query("SELECT * FROM diary WHERE date = :entryDate")
    Entry getEntryByDate(Date entryDate);

    @Query("UPDATE diary SET content = :newContent WHERE date = :date")
    void updateContent(Date date,String newContent);

    @Query("UPDATE diary SET salt = :newSalt WHERE date = :date")
    void updateSalt(Date date,byte[] newSalt);

    @Query("UPDATE diary SET IV = :newIV WHERE date = :date")
    void updateIV(Date date,byte[] newIV);

    @Query("UPDATE diary SET emotion = :emotion WHERE date = :date")
    void updateEmotion(Date date, int emotion);

}
