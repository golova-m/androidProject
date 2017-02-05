package com.example.lab32;

import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.example.lab32.database.DbOpenHelper;
import com.example.lab32.database.StoreDb;

public class EditCategoryActivity extends AppCompatActivity {
    long categoryId=0;

    //view
    EditText categoryBox;
    Button saveButton;
    Button delButton;

    //database
    DbOpenHelper sqlHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_category);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            categoryId = extras.getLong("id");
        }
        categoryBox = (EditText) findViewById(R.id.category);
        saveButton = (Button) findViewById(R.id.save);
        delButton = (Button) findViewById(R.id.delete);

        //database
        sqlHelper = new DbOpenHelper(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        db = sqlHelper.getWritableDatabase();

        // если 0, то добавление
        if (categoryId > 0) {
            // получаем элемент по id из бд
            String table = "category";
            String[] columns = null;
            String selection = "_id = ?";
            String[] selectionArgs =  new String[]{String.valueOf(categoryId)};
            cursor = db.query(table, columns, selection, selectionArgs, null, null, null);

            cursor.moveToFirst();
            categoryBox.setText(cursor.getString(1));
            cursor.close();
        } else {
            // скрываем кнопку удаления
            delButton.setVisibility(View.GONE);
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        // Закрываем подключения
        db.close();
    }

    public void onDelete(View view) {
        db.delete(StoreDb.Category.TABLE_NAME, "_id = ?", new String[]{String.valueOf(categoryId)});
        goCategory();
    }

    public void onSave(View view){
        ContentValues cv = new ContentValues();
        cv.put(StoreDb.Category.COLUMN_NAME, categoryBox.getText().toString());
        if (categoryId > 0) {
            db.update(StoreDb.Category.TABLE_NAME, cv, StoreDb.Category.COLUMN_ID + "=" + String.valueOf(categoryId), null);
        } else {
            db.insert(StoreDb.Category.TABLE_NAME, null, cv);
        }
        goCategory();
    }


    public void goCategory(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

}
