package com.example.lab32.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.example.lab32.database.StoreDb;

public class DbOpenHelper extends SQLiteOpenHelper{

    private static final String DATABASE_NAME = "userstore.db"; // название бд
    private static final int SCHEMA = 1; // версия базы данных

    //static final String TABLE = "users"; // название таблицы в бд
    // названия столбцов
    //public static final String COLUMN_ID = "_id";
    //public static final String COLUMN_NAME = "name";

    public DbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, SCHEMA);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL(StoreDb.SQL_CREATE_CATEGORY);
        db.execSQL(StoreDb.SQL_CREATE_RECORD);
        db.execSQL(StoreDb.SQL_CREATE_PHOTO);

        // добавление начальных данных
        db.execSQL("INSERT INTO "+ StoreDb.Category.TABLE_NAME + " ( "
                + StoreDb.Category.COLUMN_ID + ", "
                + StoreDb.Category.COLUMN_NAME
                + " ) VALUES (1, 'Учёба');");
        db.execSQL("INSERT INTO "+ StoreDb.Category.TABLE_NAME + " ( "
                + StoreDb.Category.COLUMN_ID + ", "
                + StoreDb.Category.COLUMN_NAME
                + " ) VALUES (2, 'Работа');");
        db.execSQL("INSERT INTO "+ StoreDb.Category.TABLE_NAME + " ( "
                + StoreDb.Category.COLUMN_ID + ", "
                + StoreDb.Category.COLUMN_NAME
                + " ) VALUES (3, 'Отдых');");

        db.execSQL("INSERT INTO "+ StoreDb.Record.TABLE_NAME + " ("
                + StoreDb.Record.COLUMN_ID + ", "
                + StoreDb.Record.COLUMN_DESCRIPTION + ", "
                + StoreDb.Record.COLUMN_CATEGORY_ID
                + ") VALUES (1, 'Начать учиться', 1);");
        db.execSQL("INSERT INTO "+ StoreDb.Record.TABLE_NAME + " ("
                + StoreDb.Record.COLUMN_ID + ", "
                + StoreDb.Record.COLUMN_DESCRIPTION + ", "
                + StoreDb.Record.COLUMN_CATEGORY_ID
                + ") VALUES (2, 'Сходить на работу', 2);");

        db.execSQL("INSERT INTO "+ StoreDb.Photo.TABLE_NAME + " ("
                + StoreDb.Photo.COLUMN_ID + ", "
                + StoreDb.Photo.COLUMN_PATH + ", "
                + StoreDb.Photo.COLUMN_RECORD_ID
                + ") VALUES (1, 'D:/img', 1);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion,  int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+ StoreDb.Category.TABLE_NAME);
        onCreate(db);
    }
}
