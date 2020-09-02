package ur.mi.liebestagebuch.database;

import android.os.Build;

import androidx.annotation.RequiresApi;
import androidx.room.TypeConverter;

import java.util.Base64;
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

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static byte[] toByte(String string){
        byte[] bytes = Base64.getDecoder().decode(string);
        return bytes;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    @TypeConverter
    public static String fromByte(byte[] bytes){
        String string = Base64.getEncoder().encodeToString(bytes);
        return string;
    }
}
