package com.example.simple_browser;

import android.provider.BaseColumns;

public class Bookmark_class  {

    private Bookmark_class(){

    }

        public static final class Bookmark_entry implements BaseColumns {

            public static final String TABLE_NAME="BookmarkList";
            public static final String Coloumn_Title="title";
            public static final String Column_link="link";
            public static final String  Column_TIMESTAMP="timestamp";
            public static final String Column_Image="image";

        }
}
