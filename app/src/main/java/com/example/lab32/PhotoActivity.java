package com.example.lab32;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import com.example.lab32.database.DbOpenHelper;

public class PhotoActivity extends AppCompatActivity {

    ListView mList;
    TextView header;
    DbOpenHelper sqlHelper;

    SQLiteDatabase db;
    Cursor cursor;
    SimpleCursorAdapter userAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photo);

        header = (TextView)findViewById(R.id.header);

        mList = (ListView)findViewById(R.id.list);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //Intent intent = new Intent(getApplicationContext(), EditRecordActivity.class);
                //intent.putExtra("id", id);
                //startActivity(intent);
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
        String table = "record as R inner join photo as P on R._id = P.record_id";
        String[] columns = {"P._id as _id", "P.path as Path", "R.description as Description"};
        cursor = db.query(table, columns, null, null, null, null, null);

        String[] headers = new String[] {"Path", "Description"};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                cursor, headers, new int[]{android.R.id.text1, android.R.id.text2}, 0);
        header.setText("Найдено элементов: " + String.valueOf(cursor.getCount()));
        mList.setAdapter(userAdapter);
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        // Закрываем подключения
        db.close();
        cursor.close();
    }

    //MENU
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        if (id == R.id.menu_item_record) {
            Intent intent = new Intent(getApplicationContext(), MainActivity.class);
            startActivity(intent);
            return true;
        } else if (id == R.id.menu_item_category) {
            Intent intent = new Intent(getApplicationContext(), CategoryActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /*public void onAddNote(View view){
        Intent intent = new Intent(this, UserActivity.class);
        startActivity(intent);
    }*/

    /*public void onEditRecord(View view){
        Intent intent = new Intent(this, EditRecordActivity.class);
        startActivity(intent);
    }*/
}
