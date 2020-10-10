package ur.mi.liebestagebuch.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.Date;
import java.util.List;

import ur.mi.liebestagebuch.database.data.DBEntry;

@Dao
public interface DiaryDao {

    @Insert
    void insert (DBEntry DBEntry);

    @Query("SELECT * FROM DBEntry")
    List<DBEntry> getAll();

    @Update
    void update(DBEntry entry);

    @Query("DELETE FROM DBEntry")
    void clear();

    @Query("SELECT emotion FROM DBEntry")
    List<Integer> getAllEmotions();

    @Query("SELECT * FROM DBEntry ORDER BY uid DESC LIMIT 1")
    DBEntry getNewest();

    @Query("SELECT * FROM DBEntry WHERE date = :entryDate")
    DBEntry getEntryByDate(Date entryDate);

    @Query("UPDATE DBEntry SET content = :newContent WHERE date = :date")
    void updateContent(Date date,String newContent);

    @Query("UPDATE DBEntry SET salt = :newSalt WHERE date = :date")
    void updateSalt(Date date,byte[] newSalt);

    @Query("UPDATE DBEntry SET IV = :newIV WHERE date = :date")
    void updateIV(Date date,byte[] newIV);

    @Query("UPDATE DBEntry SET emotion = :emotion WHERE date = :date")
    void updateEmotion(Date date, int emotion);

}
