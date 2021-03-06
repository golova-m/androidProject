package com.example.lab32;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.lab32.database.DbOpenHelper;

public class MainActivity extends ActionBarActivity  {

    ListView mList;
    TextView header;
    DbOpenHelper sqlHelper;

    SQLiteDatabase db;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        header = (TextView)findViewById(R.id.header);

        mList = (ListView)findViewById(R.id.list);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(getApplicationContext(), EditRecordActivity.class);
                EditRecordActivity.imagePaths = null;
                intent.putExtra("id", id);
                startActivity(intent);
            }
        });
        sqlHelper = new DbOpenHelper(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();
        // открываем подключение
        db = sqlHelper.getReadableDatabase();

        //получаем данные из бд
        String table = "category as C inner join record as R on C._id = R.category_id";
        String[] columns = {"R._id as _id","C.name as Name", "R.description as Description", "R.stop_date_time as Date"};
        String selection = null;  //"salary < ?";
        String[] selectionArgs = null; // {"40000"};
        userCursor = db.query(table, columns, selection, selectionArgs, null, null, null);

        String[] headers = new String[] {"Description", "Name", "Date"};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено элементов: " + String.valueOf(userCursor.getCount()));
        mList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключения
        db.close();
        userCursor.close();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_item_photo) {
            Intent intent = new Intent(getApplicationContext(), PhotoActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    //добавление записи
    public void onEditRecord(View view){
        EditRecordActivity.imagePaths = null;
        Intent intent = new Intent(this, EditRecordActivity.class);
        startActivity(intent);
    }

    public void onStatistics(View view){
        Intent intent = new Intent(this, StatisticsActivity.class);
        startActivity(intent);
    }
}