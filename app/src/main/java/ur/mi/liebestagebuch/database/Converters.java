package ur.mi.liebestagebuch.database;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class Converters {

    @TypeConverter
    public static Date toDate(Long value){
        return value == null ? null: new Date(value);
    }

    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    @TypeConverter
    public static byte[] toByte(String string){
        byte[] bytes = Base64.decode(string, Base64.DEFAULT);
        return bytes;
    }

    @TypeConverter
    public static String fromByte(byte[] bytes){
        String string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
}