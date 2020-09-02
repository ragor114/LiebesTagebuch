package ur.mi.liebestagebuch.database;

import android.content.Context;
import android.os.AsyncTask;
import android.util.Log;

import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import ur.mi.liebestagebuch.database.data.Entry;


public class DBHelper {

    private DiaryDatabase diaryDB;
    private Entry newEmptyEntry;

    public DBHelper(Context context){
        diaryDB = DiaryDatabase.getInstance(context);
    }

    public void newEntry(Date date, int emotion, String content, byte[] salt){
        newEmptyEntry = new Entry(date, emotion, content, salt);
        AsyncNewEmpty asyncNewEmpty = new AsyncNewEmpty();
        asyncNewEmpty.execute();
    }

    //ASYNC DURCH RUNNABLE AUSTAUSCHEN

    //BESTIMMTEN EINTRAG SUCHEN

    private class AsyncNewEmpty extends android.os.AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            diaryDB.getDiaryDao().insert(newEmptyEntry);

            //DEBUG ONLY
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            return null;
        }
    }

    //DEBUG ONLY
    public void clear(){
        AsyncClear clear = new AsyncClear();
        clear.execute();
    }

    //DEBUG ONLY
    public void list(){
        AsyncGetEmotions list = new AsyncGetEmotions();
        list.execute();
    }


    private class AsyncClear extends android.os.AsyncTask<Void,Void,Void>{

        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().clear();
            Log.println(Log.DEBUG,"DB","DB cleared");
            try {
                for(Entry entry: diaryDB.getDiaryDao().getAll()){
                    Log.println(Log.DEBUG,"DB",entry.toString());
                }
            } catch (Exception e){
                Log.println(Log.DEBUG,"DB","Database empty");
            }

            return null;
        }
    }

    private class AsyncGetEmotions extends android.os.AsyncTask<Void,Void,List<Integer>>{

        @Override
        protected List<Integer> doInBackground(Void... voids) {
            List<Integer> arrEmotions = diaryDB.getDiaryDao().getAllEmotions();
            Log.println(Log.DEBUG,"DB",arrEmotions.toString());
            return arrEmotions;
        }
    }

    public void newest(){
        AsyncGetNewest newest = new AsyncGetNewest();
        newest.execute();
    }

    private class AsyncGetNewest extends AsyncTask<Void,Void,Entry>{

        @Override
        protected Entry doInBackground(Void... voids) {
            Entry newest = null;
            try {
                newest = diaryDB.getDiaryDao().getNewest();
                Log.println(Log.DEBUG, "DB", newest.toString());
            } catch (Exception e){
                Log.println(Log.DEBUG,"DB","ERROR - DB empty");
            }
            return newest;
        }
    }

}
