package com.example.simple_browser;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;

import com.example.simple_browser.History_class.History_entry;


public class History_db_helper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME="HistoryList.db";
    public static final int DATABASE_VERSION=2;

    public History_db_helper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        final String SQL_CREATE_BOOKMARKLIST_TABLE= "CREATE TABLE "+
                History_entry.TABLE_NAME + "(" +
                History_entry._ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "+
                History_entry.Coloumn_Title + " TEXT NOT NULL, "+
                History_entry.Column_link +" TEXT NOT NULL, "+
                History_entry.Column_date +" TEXT NOT NULL, "+
                History_entry.Column_TIMESTAMP + " TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                ");";

        db.execSQL(SQL_CREATE_BOOKMARKLIST_TABLE);


    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        db.execSQL("DROP TABLE IF EXISTS "+ History_entry.TABLE_NAME);
        onCreate(db);

    }
}
