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
    private String updatedContent;
    private byte[] updatedSalt;
    private byte[] updatedIV;
    private Date changeDate;
    private Entry get;

    public DBHelper(Context context){
        diaryDB = DiaryDatabase.getInstance(context);
    }

    //TODO: ASYNC DURCH RUNNABLE AUSTAUSCHEN


    public void newEntry(Date date, int emotion, String content, byte[] salt, byte[] iv){
        newEmptyEntry = new Entry(date, emotion, content, salt, iv);
        AsyncNewEmpty asyncNewEmpty = new AsyncNewEmpty();
        asyncNewEmpty.execute();
    }

    public void updateEntryContent(Date date, String content){
        updatedContent =content;
        changeDate = date;
        AsyncUpdateContent asyncUpdateContent = new AsyncUpdateContent();
        asyncUpdateContent.execute();
    }

    public void updateEntrySalt(Date date, byte[] salt){
        updatedSalt = salt;
        changeDate = date;
        AsyncUpdateSalt asyncUpdateSalt = new AsyncUpdateSalt();
        asyncUpdateSalt.execute();
    }

    public void updateEntryIV(Date date, byte[] IV){
        updatedIV = IV;
        changeDate = date;
        AsyncUpdateIV asyncUpdateIV = new AsyncUpdateIV();
        asyncUpdateIV.execute();
    }

    private class AsyncUpdateSalt extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().updateSalt(changeDate,updatedSalt);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            return null;
        }
    }

    private class AsyncUpdateIV extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().updateIV(changeDate,updatedIV);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            return null;
        }
    }

    private class AsyncUpdateContent extends AsyncTask<Void,Void,Void>{
        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().updateContent(changeDate,updatedContent);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            return null;
        }
    }

    public Entry getEntryByDate(Date date){
        AsyncGet asyncGet = new AsyncGet(date);
        asyncGet.execute();
        try {
            return get;
        }catch (Exception e){
            return null;
        }
    }

    private class AsyncGet extends AsyncTask<Void,Void,Entry>{
        private Date dateSearch;

        public AsyncGet(Date date) {
            dateSearch = date;
        }

        @Override
        protected Entry doInBackground(Void... voids) {
            try {
                get = diaryDB.getDiaryDao().getEntryByDate(dateSearch);
                Log.println(Log.DEBUG, "DB", "Found: " + get.toString());
                return get;
            }catch(Exception e){
                Log.println(Log.DEBUG, "DB", "NO ENTRY FOUND");
                return null;
            }
        }
    }



    private class AsyncNewEmpty extends android.os.AsyncTask<Void,Void,Void> {

        @Override
        protected Void doInBackground(Void... voids) {

            diaryDB.getDiaryDao().insert(newEmptyEntry);

            //DEBUG
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