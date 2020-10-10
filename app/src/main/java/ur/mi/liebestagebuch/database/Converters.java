package ur.mi.liebestagebuch.database;

import android.os.Build;
import android.util.Base64;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.Calendar;
import java.util.Date;

public class Converters {

    //Konvertiert Longs aus der Datenbank in Dates
    @TypeConverter
    public static Date toDate(Long value){
        return value == null ? null: new Date(value);
    }

    //Konvertiert Dates in Longs für die Datenbank
    @TypeConverter
    public static Long fromDate(Date date) {
        return date == null ? null : date.getTime();
    }

    //Konvertiert Strings aus der Datenbank in Byte-Arrays
    @TypeConverter
    public static byte[] toByte(String string){
        byte[] bytes = Base64.decode(string, Base64.DEFAULT);
        return bytes;
    }

    //Konvertiert Byte-Arrays in Strings für die Datenbank
    @TypeConverter
    public static String fromByte(byte[] bytes){
        String string = Base64.encodeToString(bytes, Base64.DEFAULT);
        return string;
    }
}