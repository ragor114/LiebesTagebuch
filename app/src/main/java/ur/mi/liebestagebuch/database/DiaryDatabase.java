package ur.mi.liebestagebuch.database;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ur.mi.liebestagebuch.database.data.DBEntry;

@Database(entities = {DBEntry.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DiaryDatabase extends RoomDatabase {

    public abstract DiaryDao getDiaryDao();

}
