package ur.mi.liebestagebuch.database;

import android.content.Context;
import android.provider.SyncStateContract;

import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import ur.mi.liebestagebuch.database.data.Entry;

@Database(entities = {Entry.class}, version = 1)
@TypeConverters({Converters.class})
public abstract class DiaryDatabase extends RoomDatabase {

    public abstract DiaryDao getDiaryDao();

}
