package com.example.simple_browser;

import android.provider.BaseColumns;

public class History_class {

    private History_class(){

    }

        public static final class History_entry implements BaseColumns {

            public static final String TABLE_NAME="HistoryList";
            public static final String Coloumn_Title="title";
            public static final String Column_link="link";
            public static final String Column_date="date";
            public static final String  Column_TIMESTAMP="timestamp";

        }
}
