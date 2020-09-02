package ur.mi.liebestagebuch.database;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.Query;
import androidx.room.Update;

import java.util.ArrayList;
import java.util.List;

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
}
