package com.example.simple_browser;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;

public class History_functions {

    long id;
    Context context;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    History_functions(Context context) {
        this.context = context;

    }


    public void insert(String title, String link, String date) {

        insertingAsyncTask insertingAsyncTask = new insertingAsyncTask(title, link, date);
        try {
            insertingAsyncTask.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public void delete(long id) {

        deletingAsyncTask deletingAsyncTask = new deletingAsyncTask(id);

        try {

            deletingAsyncTask.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    int countAll() {

        int n = 0;
        countingAllAsyncTask countingAllAsyncTask = new countingAllAsyncTask();
        try {

            n = countingAllAsyncTask.execute(context).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return n;

    }

    void deleteAll() {

        SQLiteDatabase mdatabase;
        History_db_helper dbHelper = new History_db_helper(context);

        mdatabase = dbHelper.getWritableDatabase();

        mdatabase.delete(History_class.History_entry.TABLE_NAME, null, null);
    }

}

//for inserting element
class insertingAsyncTask extends AsyncTask<Context, Void, Void> {

    private String title, link, date;


    insertingAsyncTask(String title, String link, String Date) {
        this.title = title;
        this.link = link;
        this.date = Date;

    }

    @Override
    protected Void doInBackground(Context... contexts) {

        SQLiteDatabase mdataBase;
        History_db_helper dbHelper = new History_db_helper(contexts[0]);
        mdataBase = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(History_class.History_entry.Coloumn_Title, title);
            values.put(History_class.History_entry.Column_link, link);
            values.put(History_class.History_entry.Column_date, date);


            mdataBase.insert(History_class.History_entry.TABLE_NAME, null, values);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


}


//for deleting element
class deletingAsyncTask extends AsyncTask<Context, Void, Void> {

    private long id;


    deletingAsyncTask(long id) {
        this.id = id;
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        SQLiteDatabase mdataBase;
        History_db_helper dbHelper = new History_db_helper(contexts[0]);
        mdataBase = dbHelper.getWritableDatabase();

        try {
            mdataBase.delete(History_class.History_entry.TABLE_NAME, History_class.History_entry._ID + "=" + id, null);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


}


//counting all elements
class countingAllAsyncTask extends AsyncTask<Context, Void, Integer> {

    @Override
    protected Integer doInBackground(Context... contexts) {

        int i;
        SQLiteDatabase db;
        History_db_helper dbHelper = new History_db_helper(contexts[0]);

        db = dbHelper.getReadableDatabase();
        try {
            @SuppressLint("Recycle") Cursor cursor = db.query(History_class.History_entry.TABLE_NAME,
                    null, null,
                    null,
                    null,
                    null,
                    History_class.History_entry.Column_TIMESTAMP);

            i = cursor.getCount();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}