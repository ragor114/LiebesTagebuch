package ur.mi.liebestagebuch.database;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.util.Date;
import java.util.List;
import java.util.concurrent.Executors;

import ur.mi.liebestagebuch.DetailAndEditActivity.DetailActivityConfig;
import ur.mi.liebestagebuch.database.data.Entry;


public class DBHelper{

    private DiaryDatabase diaryDB;
    private Entry newEmptyEntry;
    private String updatedContent;
    private byte[] updatedSalt;
    private byte[] updatedIV;
    private Date changeDate;
    private Entry get;

    private DatabaseListener listener;

    public DBHelper(Context context, DatabaseListener listener){
        this.listener = listener;
        diaryDB = DiaryDatabase.getInstance(context);
    }

    //TODO: ASYNC DURCH RUNNABLE AUSTAUSCHEN


    public void newEntry(Date date, int emotion, String content, byte[] salt, byte[] iv){
        newEmptyEntry = new Entry(date, emotion, content, salt, iv);
        AsyncNewEmpty asyncNewEmpty = new AsyncNewEmpty(listener);
        asyncNewEmpty.execute();
    }

    public void updateEntryContent(Date date, String content){
        updatedContent =content;
        changeDate = date;
        AsyncUpdateContent asyncUpdateContent = new AsyncUpdateContent(listener);
        asyncUpdateContent.execute();
    }

    public void updateEntrySalt(Date date, byte[] salt){
        updatedSalt = salt;
        changeDate = date;
        AsyncUpdateSalt asyncUpdateSalt = new AsyncUpdateSalt(listener);
        asyncUpdateSalt.execute();
    }

    public void updateEntryIV(Date date, byte[] IV){
        updatedIV = IV;
        changeDate = date;
        AsyncUpdateIV asyncUpdateIV = new AsyncUpdateIV(listener);
        asyncUpdateIV.execute();
    }

    private class AsyncUpdateSalt extends AsyncTask<Void,Void,Void>{

        private DatabaseListener listener;

        public AsyncUpdateSalt(DatabaseListener listener){
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().updateSalt(changeDate,updatedSalt);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.SALT_UPDATE_CODE);

            return null;
        }
    }

    private class AsyncUpdateIV extends AsyncTask<Void,Void,Void>{

        private DatabaseListener listener;

        public AsyncUpdateIV(DatabaseListener listener){
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().updateIV(changeDate,updatedIV);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.IV_UPDATE_CODE);

            return null;
        }
    }

    private class AsyncUpdateContent extends AsyncTask<Void,Void,Void>{

        private DatabaseListener listener;

        public AsyncUpdateContent(DatabaseListener listener){
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {
            diaryDB.getDiaryDao().updateContent(changeDate,updatedContent);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.CONTENT_UPDATE_CODE);

            return null;
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

    private class AsyncGet extends AsyncTask<Void,Void,Entry>{
        private Date dateSearch;

        private DatabaseListener listener;

        private Entry foundEntry;

        public AsyncGet(Date date, DatabaseListener listener) {
            dateSearch = date;
            this.listener = listener;
        }

        @Override
        protected Entry doInBackground(Void... voids) {
            if(!isCancelled()) {
                Log.d("Detail", "Searching for Entry");
                try {
                    foundEntry = diaryDB.getDiaryDao().getEntryByDate(dateSearch);
                    Log.d("DB", "Found Entry");
                    Log.d("Detail", "Giving found Entry to listener, Async");
                    onPostExecute(foundEntry);
                    cancel(true);
                    return foundEntry;
                } catch (Exception e) {
                    Log.d("Detail", "Exception in AsyncGet: " + e.getMessage());
                    Log.d("DB", "NO ENTRY FOUND");
                    cancel(true);
                    return null;
                }
            }
            Log.d("Detail", "isCancelled");
            return null;
        }

        @Override
        protected void onPostExecute(Entry entry){
            listener.entryFound(foundEntry);
        }
    }



    private class AsyncNewEmpty extends android.os.AsyncTask<Void,Void,Void> {

        private DatabaseListener listener;

        public AsyncNewEmpty (DatabaseListener listener){
            this.listener = listener;
        }

        @Override
        protected Void doInBackground(Void... voids) {

            diaryDB.getDiaryDao().insert(newEmptyEntry);

            //DEBUG
            for(Entry entry: diaryDB.getDiaryDao().getAll()){
                Log.println(Log.DEBUG,"DB",entry.toString());
            }

            listener.updateFinished(DetailActivityConfig.NEW_ENTRY_UPDATE_CODE);

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