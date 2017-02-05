package com.example.lab32.database;

import android.provider.BaseColumns;

public class StoreDb {

    public static class Category implements BaseColumns{
        public static final String TABLE_NAME = "category";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_NAME = "name";
    }

    public static class Record implements BaseColumns{
        public static final String TABLE_NAME = "record";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_DESCRIPTION = "description";
        public static final String COLUMN_START_DATE_TIME = "start_date_time";
        public static final String COLUMN_STOP_DATE_TIME = "stop_date_time";
        public static final String COLUMN_CUT = "cut";
        public static final String COLUMN_CATEGORY_ID = "category_id";
    }

    public static class Photo implements BaseColumns{
        public static final String TABLE_NAME = "photo";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_PATH = "path";
        public static final String COLUMN_RECORD_ID = "record_id";
    }

    public static final String  SQL_CREATE_CATEGORY =
            "create table " + Category.TABLE_NAME + " ( "
            + Category.COLUMN_ID + " integer primary key autoincrement,"
            + Category.COLUMN_NAME + " text );";

    public static final String  SQL_CREATE_RECORD =
            "create table " + Record.TABLE_NAME + " ( "
            + Record.COLUMN_ID + " integer primary key autoincrement,"
            + Record.COLUMN_DESCRIPTION + " text,"
            + Record.COLUMN_START_DATE_TIME + " integer,"
            + Record.COLUMN_STOP_DATE_TIME + " integer,"
            + Record.COLUMN_CUT + " integer,"
            + Record.COLUMN_CATEGORY_ID +" integer );";

    public static final String  SQL_CREATE_PHOTO =
            "create table " + Photo.TABLE_NAME + " ("
            + Photo.COLUMN_ID + " integer primary key autoincrement,"
            + Photo.COLUMN_PATH + " text,"
            + Photo.COLUMN_RECORD_ID + " integer );";
}
