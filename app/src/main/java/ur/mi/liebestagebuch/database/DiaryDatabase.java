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

    private static DiaryDatabase diaryDB;

    public static DiaryDatabase getInstance(Context context){
        if(null == diaryDB){
            diaryDB = buildDBInstance(context);
        }
        return diaryDB;
    }

    private static DiaryDatabase buildDBInstance(Context context){
        return Room.databaseBuilder(context,DiaryDatabase.class,"database-diary").build();
    }
}
