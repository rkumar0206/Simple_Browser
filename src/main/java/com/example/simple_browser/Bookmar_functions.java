package com.example.simple_browser;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.widget.Toast;

import java.util.concurrent.ExecutionException;

public class Bookmar_functions {


    private Context context;
    private long id;


    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    Bookmar_functions(Context context) {
        this.context = context;
    }

    void insert(String title, String link,byte[] image) {
        try {
            insertAsyncTask insertAsyncTask = new insertAsyncTask(title, link,image);
            insertAsyncTask.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    void delete(long id) {

        try {
            deleteAsyncTask deleteAsyncTask = new deleteAsyncTask(id);
            deleteAsyncTask.execute(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    boolean search(String link) {



        /*searchAsyncTask searchAsyncTask = new searchAsyncTask(link);

        try {

            return searchAsyncTask.execute(context).get();

        } catch (Exception e) {
            e.printStackTrace();
        }
        return true;

         */

        SQLiteDatabase db;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(context);
        db = dbHelper.getReadableDatabase();


        try {

            @SuppressLint("Recycle") Cursor cursor = db.query(Bookmark_class.Bookmark_entry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Bookmark_class.Bookmark_entry.Column_TIMESTAMP + " DESC"
            );

            if (cursor.getCount() != 0) {
                cursor.moveToFirst();

                /*while (cursor != null) {


                    if (cursor.getString(cursor.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link)).equals(link)) {
                        setId(cursor.getLong(cursor.getColumnIndex(Bookmark_class.Bookmark_entry._ID)));
                        return false;
                    }
                    cursor.moveToNext();
                }

                 */

                do {
                    if (cursor.getString(cursor.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link)).equals(link)) {

                        setId(cursor.getLong(cursor.getColumnIndex(Bookmark_class.Bookmark_entry._ID)));
                        return false;

                    }

                } while (cursor.moveToNext());

            } else {
                return true;
            }


        } catch (Exception e) {
            e.printStackTrace();
        }

        return true;


    }


    int countall() {

        int n = 0;
        countAllAsyncTask countAllAsyncTask = new countAllAsyncTask();
        try {

            n = countAllAsyncTask.execute(context).get();
        } catch (Exception e) {
            e.printStackTrace();
        }

        return n;
    }

    void deleteAll() {

        SQLiteDatabase mdatabase;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(context);

        mdatabase = dbHelper.getWritableDatabase();

        mdatabase.delete(Bookmark_class.Bookmark_entry.TABLE_NAME, null, null);
        Toast.makeText(context, "deleted", Toast.LENGTH_SHORT).show();
    }

    boolean update(String title, String link, long id) {

        updateAsyncTask updateAsyncTask = new updateAsyncTask(title, link, id);
        try {
            return updateAsyncTask.execute(context).get();
        } catch (ExecutionException | InterruptedException e) {
            e.printStackTrace();
        }

        return false;
    }


}

//for inserting element
class insertAsyncTask extends AsyncTask<Context, Void, Void> {

    private String title, link;
    byte[] image;

    insertAsyncTask(String title, String link,byte[] image) {
        this.title = title;
        this.link = link;
        this.image=image;
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        SQLiteDatabase mdataBase;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(contexts[0]);
        mdataBase = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Bookmark_class.Bookmark_entry.Coloumn_Title, title);
            values.put(Bookmark_class.Bookmark_entry.Column_link, link);
            values.put(Bookmark_class.Bookmark_entry.Column_Image,image);
            mdataBase.insert(Bookmark_class.Bookmark_entry.TABLE_NAME, null, values);


        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


}


//for deleting element
class deleteAsyncTask extends AsyncTask<Context, Void, Void> {

    private long id;


    deleteAsyncTask(long id) {
        this.id = id;
    }

    @Override
    protected Void doInBackground(Context... contexts) {

        SQLiteDatabase mdataBase;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(contexts[0]);
        mdataBase = dbHelper.getWritableDatabase();

        try {
            mdataBase.delete(Bookmark_class.Bookmark_entry.TABLE_NAME, Bookmark_class.Bookmark_entry._ID + "=" + id, null);

        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }


}


class updateAsyncTask extends AsyncTask<Context, Void, Boolean> {

    private String title, link;
    private long id;

    updateAsyncTask(String title, String link, long id) {
        this.title = title;
        this.link = link;
        this.id = id;
    }

    @Override
    protected Boolean doInBackground(Context... contexts) {

        SQLiteDatabase mdataBase;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(contexts[0]);
        mdataBase = dbHelper.getWritableDatabase();

        try {
            ContentValues values = new ContentValues();
            values.put(Bookmark_class.Bookmark_entry.Coloumn_Title, title);
            values.put(Bookmark_class.Bookmark_entry.Column_link, link);

            mdataBase.update(Bookmark_class.Bookmark_entry.TABLE_NAME, values, Bookmark_class.Bookmark_entry._ID + "=" + id, null);
            return true;

        } catch (Exception e) {
            e.printStackTrace();
        }

        return false;
    }


}


//for getting the total elements
class countAllAsyncTask extends AsyncTask<Context, Void, Integer> {

    @Override
    protected Integer doInBackground(Context... contexts) {

        int i;
        SQLiteDatabase db;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(contexts[0]);

        db = dbHelper.getReadableDatabase();
        try {
            @SuppressLint("Recycle") Cursor cursor = db.query(Bookmark_class.Bookmark_entry.TABLE_NAME,
                    null, null,
                    null,
                    null,
                    null,
                    Bookmark_class.Bookmark_entry.Column_TIMESTAMP);

            i = cursor.getCount();
            return i;
        } catch (Exception e) {
            e.printStackTrace();
        }


        return null;
    }
}


//for searching element

/*class searchAsyncTask extends AsyncTask<Context, Void, Boolean> {

    String link;

    searchAsyncTask(String link) {
        this.link = link;

    }

    @Override
    protected Boolean doInBackground(Context... contexts) {
        SQLiteDatabase db;
        BookmarkDBHelper dbHelper = new BookmarkDBHelper(contexts[0]);
        db = dbHelper.getReadableDatabase();
        Bookmar_functions bookmar_functions = new Bookmar_functions(contexts[0]);

        try {
            Cursor c = db.query(Bookmark_class.Bookmark_entry.TABLE_NAME,
                    null,
                    null,
                    null,
                    null,
                    null,
                    Bookmark_class.Bookmark_entry.Column_TIMESTAMP + " DESC"
            );

            if (c.getCount() != 0) {
                c.moveToFirst();

               // while (c != null) {
               //     if (c.getString(c.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link)).equals(link)) {

               //         bookmar_functions.setId(c.getLong(c.getColumnIndex(Bookmark_class.Bookmark_entry._ID)));  //is not able to set id
               //         return false;
               //     }
               //     c.moveToNext();
               // }

                do {
                    if (c.getString(c.getColumnIndex(Bookmark_class.Bookmark_entry.Column_link)).equals(link)) {

                        bookmar_functions.setId(c.getLong(c.getColumnIndex(Bookmark_class.Bookmark_entry._ID)));
                        return false;
                    }

                } while (c.moveToNext());

            } else {
                return true;
            }


        } catch (
                Exception e) {
            e.printStackTrace();
        }


        return true;
    }
}*/








