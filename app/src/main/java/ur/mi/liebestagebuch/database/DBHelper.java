package ur.mi.liebestagebuch.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import androidx.room.Room;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.GridView.Emotion;
import ur.mi.liebestagebuch.database.data.Entry;


public class DBHelper{

    private static DiaryDatabase diaryDB;
    private Entry newEmptyEntry;
    private String updatedContent;
    private byte[] updatedSalt;
    private byte[] updatedIV;
    private int updatedEmotion;
    private Date changeDate;
    private Entry get;

    private static final String DATABASE_NAME = "database-diary";

    private DatabaseListener listener;

    public DBHelper(Context context, DatabaseListener listener){
        this.listener = listener;
        diaryDB = Room.databaseBuilder(context, DiaryDatabase.class, DATABASE_NAME).build();
    }


    public void newEntry(Date date, int emotion, String content, byte[] salt, byte[] iv){
        newEmptyEntry = new Entry(date, emotion, content, salt, iv);
        AsyncNewEmpty newEmpty = new AsyncNewEmpty(newEmptyEntry, listener);
        Executors.newSingleThreadExecutor().submit(newEmpty);
    }

    public void updateEntryContent(Date date, String content){
        updatedContent =content;
        changeDate = date;
        AsyncUpdateContent updateContent = new AsyncUpdateContent(changeDate, updatedContent, listener);
        Executors.newSingleThreadExecutor().submit(updateContent);
    }

    public void updateEntrySalt(Date date, byte[] salt){
        updatedSalt = salt;
        changeDate = date;
        AsyncUpdateSalt updateSalt = new AsyncUpdateSalt(changeDate, updatedSalt, listener);
        Executors.newSingleThreadExecutor().submit(updateSalt);
    }

    public void updateEntryIV(Date date, byte[] IV){
        updatedIV = IV;
        changeDate = date;
        AsyncUpdateIV updateIV = new AsyncUpdateIV(changeDate, updatedIV, listener);
        Executors.newSingleThreadExecutor().submit(updateIV);
    }

    public void updateEntryEmotion(Date date, int emotion){
        changeDate = date;
        updatedEmotion = emotion;
        AsyncUpdateEmotion updateEmotion = new AsyncUpdateEmotion(updatedEmotion, changeDate, listener);
        Executors.newSingleThreadExecutor().submit(updateEmotion);
    }

    private class AsyncUpdateEmotion implements Runnable{

        private int updateEmotion;
        private Date updateDate;
        private DatabaseListener listener;

        public AsyncUpdateEmotion(int updateEmotion, Date updateDate, DatabaseListener listener){
            this.updateEmotion = updateEmotion;
            this.updateDate = updateDate;
            this.listener = listener;
        }

        @Override
        public void run() {
            diaryDB.getDiaryDao().updateEmotion(updateDate, updateEmotion);
            listener.updateFinished(DetailActivityConfig.EMOTION_UPDATE_CODE);
        }
    }

    private class AsyncUpdateSalt implements Runnable{

        private Date updateDate;
        private byte[] updateSalt;
        private DatabaseListener listener;

        public AsyncUpdateSalt(Date updateDate,byte[] updateSalt,DatabaseListener listener){
            this.updateDate = updateDate;
            this.updateSalt = updateSalt;
            this.listener = listener;
        }


        @Override
        public void run (){
            diaryDB.getDiaryDao().updateSalt(changeDate,updatedSalt);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.SALT_UPDATE_CODE);
        }
    }

    private class AsyncUpdateIV implements Runnable{

        private Date updateDate;
        private byte[] updateIV;
        private DatabaseListener listener;

        public AsyncUpdateIV(Date updateDate, byte[] updateIV, DatabaseListener listener){
            this.updateDate = updateDate;
            this.updateIV = updateIV;
            this.listener = listener;
        }

        @Override
        public void run(){
            diaryDB.getDiaryDao().updateIV(changeDate,updatedIV);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.IV_UPDATE_CODE);

        }
    }

    private class AsyncUpdateContent implements Runnable{

        private Date updateDate;
        private String updateContent;
        private DatabaseListener listener;

        public AsyncUpdateContent(Date updateDate, String updateContent, DatabaseListener listener){
            this.updateDate = updateDate;
            this.updateContent = updateContent;
            this.listener = listener;
        }

        @Override
        public void run() {
            diaryDB.getDiaryDao().updateContent(changeDate,updatedContent);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.CONTENT_UPDATE_CODE);
        }
    }

    public void getEntryByDate(Date date){
        Log.d("Detail","Getting entry by date");

        /*
        AsyncGet asyncGet = new AsyncGet(date, listener);
        asyncGet.execute();
        try {
            return get;
        }catch (Exception e){
            return null;
        }
        */
        GetRunnable get = new GetRunnable(date, listener);
        Executors.newSingleThreadExecutor().submit(get);
    }

    private class GetRunnable implements Runnable{

        private Date dateSearch;
        private DatabaseListener listener;

        public GetRunnable(Date date, DatabaseListener listener){
            this.dateSearch = date;
            this.listener = listener;
        }

        @Override
        public void run() {
            Entry foundEntry = null;
            Log.d("Detail", "Searching for Entry");
            try {
                foundEntry = diaryDB.getDiaryDao().getEntryByDate(dateSearch);
                Log.d("DB", "Found Entry");
                Log.d("Detail", "Giving found Entry to listener, Async");
            } catch (Exception e) {
                Log.d("Detail", "Exception in AsyncGet: " + e.getMessage());
                Log.d("DB", "NO ENTRY FOUND");
            }
            final Entry finalFound = foundEntry;
            Handler mainThreadHandler = new Handler(Looper.getMainLooper());
            mainThreadHandler.post(new Runnable() {
                @Override
                public void run() {
                    listener.entryFound(finalFound);
                }
            });
        }
    }


    private class AsyncNewEmpty implements Runnable {

        private Entry updateEntry;
        private DatabaseListener listener;

        public AsyncNewEmpty (Entry updateEntry,DatabaseListener listener){
            this.updateEntry = updateEntry;
            this.listener = listener;
        }

        @Override
        public void run() {
            diaryDB.getDiaryDao().insert(newEmptyEntry);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.NEW_ENTRY_UPDATE_CODE);
        }
    }

    //DEBUG ONLY
    public void clear(){
        AsyncClear clear = new AsyncClear();
        Executors.newSingleThreadExecutor().submit(clear);
    }

    //DEBUG ONLY
    public void list(){
        AsyncGetEmotions list = new AsyncGetEmotions();
        Executors.newSingleThreadExecutor().submit(list);
    }


    private class AsyncClear implements Runnable{

        @Override
        public void run(){
            diaryDB.getDiaryDao().clear();
            Log.println(Log.DEBUG,"DB","DB cleared");
            try {
                for(Entry entry: diaryDB.getDiaryDao().getAll()){
                    Log.println(Log.DEBUG,"DB",entry.toString());
                }
            } catch (Exception e){
                Log.println(Log.DEBUG,"DB","Database empty");
            }
        }
    }

    private class AsyncGetEmotions implements Runnable{

        @Override
        public void run() {
            List<Integer> arrEmotions = diaryDB.getDiaryDao().getAllEmotions();
            Log.println(Log.DEBUG,"DB",arrEmotions.toString());
        }
    }

    public void newest(){
        AsyncGetNewest newest = new AsyncGetNewest();
        Executors.newSingleThreadExecutor().submit(newest);
    }

    private class AsyncGetNewest implements Runnable{

        @Override
        public void run() {
            Entry newest = null;
            try {
                newest = diaryDB.getDiaryDao().getNewest();
                Log.println(Log.DEBUG, "DB", newest.toString());
            } catch (Exception e){
                Log.println(Log.DEBUG,"DB","ERROR - DB empty");
            }
        }
    }

    public void getAllEntries(){
        GetAllEntries getAllEntriesRunnable = new GetAllEntries();
        Executors.newSingleThreadExecutor().submit(getAllEntriesRunnable);
    }

    private class GetAllEntries implements Runnable{
        @Override
        public void run() {
            List<Entry> allEntries = diaryDB.getDiaryDao().getAll();
            listener.allEntriesFound(allEntries);
        }
    }

}
