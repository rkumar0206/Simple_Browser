package com.example.simple_browser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.example.simple_browser.Bookmark_class.*;
import androidx.annotation.Nullable;


public class BookmarkDBHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="BookmarList.db";
    public static final int DATABASE_VERSION=5;

    public BookmarkDBHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_BOOKMARKLIST_TABLE= "CREATE TABLE "+
                Bookmark_entry.TABLE_NAME + "(" +
                Bookmark_entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                Bookmark_entry.Coloumn_Title + " TEXT NOT NULL, "+
                Bookmark_entry.Column_link +" TEXT NOT NULL, "+
                Bookmark_entry.Column_Image +" BLOB, "+
                Bookmark_entry.Column_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_BOOKMARKLIST_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ Bookmark_entry.TABLE_NAME);
        onCreate(db);

    }
}
