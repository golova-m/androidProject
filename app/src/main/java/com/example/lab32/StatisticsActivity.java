package com.example.lab32;

import android.app.DatePickerDialog;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TabHost;
import android.widget.TextView;

import com.example.lab32.database.DbOpenHelper;
import com.example.lab32.database.StoreDb;

import java.util.Calendar;
import java.util.Date;

public class StatisticsActivity extends AppCompatActivity {

    //Date and Time
    TextView startDateBox;
    Calendar startDate = Calendar.getInstance();

    TextView stopDateBox;
    Calendar stopDate = Calendar.getInstance();

    Button buttonTab1;
    Button buttonTab2;
    Button buttonTab3;
    Button buttonTab4;

    TextView resultTextTab1;
    TextView resultTextTab2;
    TextView resultTextTab3;

    //Database
    DbOpenHelper sqlHelper;
    SQLiteDatabase db;
    Cursor cursor;

    //Tab3
    ListView mList;
    DbOpenHelper sqlHelperTab;
    SQLiteDatabase dbTab;
    Cursor userCursor;
    SimpleCursorAdapter userAdapter;
    Long[] idTab = new Long[1000];
    int indexIdTab = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_statistics);

        TabHost tabHost = (TabHost) findViewById(R.id.tabHost);

        tabHost.setup();

        TabHost.TabSpec tabSpec = tabHost.newTabSpec("tag1");
        tabSpec.setContent(R.id.linearLayout1);
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_arrow_drop_down_circle_black_24dp));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag2");
        tabSpec.setContent(R.id.linearLayout2);
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_arrow_drop_down_circle_black_24dp));
        tabHost.addTab(tabSpec);

        tabSpec = tabHost.newTabSpec("tag3");
        tabSpec.setContent(R.id.linearLayout3);
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_arrow_drop_down_circle_black_24dp));
        tabHost.addTab(tabSpec);

        mList = (ListView)findViewById(R.id.listTab);
        sqlHelperTab = new DbOpenHelper(getApplicationContext());
        dbTab = sqlHelperTab.getReadableDatabase();
        String table = "category as C inner join record as R on C._id = R.category_id";
        String[] columns = {"R._id as _id","C.name as Name", "R.description as Description"};
        String selection = null;  //"salary < ?";
        String[] selectionArgs = null; // {"40000"};
        userCursor = dbTab.query(table, columns, selection, selectionArgs, null, null, null);

        String[] headers = new String[] {"Name"};
        userAdapter = new SimpleCursorAdapter(this, android.R.layout.two_line_list_item,
                userCursor, headers, new int[]{android.R.id.text2}, 0);
        //header.setText("Найдено элементов: " + String.valueOf(userCursor.getCount()));
        mList.setAdapter(userAdapter);
        mList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                idTab[indexIdTab] = id;
                indexIdTab++;
                String str = "Ok";
                resultTextTab3.setText(str);
            }
        });

        tabSpec = tabHost.newTabSpec("tag4");
        tabSpec.setContent(R.id.linearLayout4);
        tabSpec.setIndicator("", getResources().getDrawable(R.drawable.ic_arrow_drop_down_circle_black_24dp));
        tabHost.addTab(tabSpec);

        tabHost.setCurrentTab(0);

        startDateBox =(TextView)findViewById(R.id.startDate);
        setInitialStartDateTime();

        stopDateBox =(TextView)findViewById(R.id.stopDate);
        Date dat = new Date(stopDate.getTimeInMillis());
        dat.setMonth(dat.getMonth() + 1);
        stopDate.setTimeInMillis( dat.getTime() );
        setInitialStopDateTime();

        buttonTab1 = (Button) findViewById(R.id.buttonTab1);
        buttonTab2 = (Button) findViewById(R.id.buttonTab2);
        buttonTab3 = (Button) findViewById(R.id.buttonTab3);
        buttonTab4 = (Button) findViewById(R.id.buttonTab4);


        resultTextTab1 = (TextView) findViewById(R.id.resultTextTab1);
        resultTextTab2 = (TextView) findViewById(R.id.resultTextTab2);
        resultTextTab3 = (TextView) findViewById(R.id.resultTextTab3);

        //database
        sqlHelper = new DbOpenHelper(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        db = sqlHelper.getWritableDatabase();
    }

    @Override
    public void onDestroy(){
        super.onDestroy();

        // Закрываем подключения
        db.close();
    }

    // отображаем диалоговое окно для выбора даты начала
    public void onStartDate(View v) {
        new DatePickerDialog(StatisticsActivity.this, d,
                startDate.get(Calendar.YEAR),
                startDate.get(Calendar.MONTH),
                startDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты начала
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startDate.set(Calendar.YEAR, year);
            startDate.set(Calendar.MONTH, monthOfYear);
            startDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialStartDateTime();
            /*if (startDate.getTimeInMillis() > stopDate.getTimeInMillis()){
                Toast toast = Toast.makeText(StatisticsActivity.this,
                        "Error. The start time must be less than end time ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                saveButton.setEnabled(false);
            } else{
                saveButton.setEnabled(true);
                setInitialCutDateTime();
            }*/
        }
    };

    // установка текста даты и времени завершения
    private void setInitialStartDateTime() {
        startDateBox.setText(DateUtils.formatDateTime(this,
                startDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    // отображаем диалоговое окно для выбора даты завершения
    public void onStopDate(View v) {
        new DatePickerDialog(StatisticsActivity.this, dd,
                stopDate.get(Calendar.YEAR),
                stopDate.get(Calendar.MONTH),
                stopDate.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // установка обработчика выбора даты завершения
    DatePickerDialog.OnDateSetListener dd = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            stopDate.set(Calendar.YEAR, year);
            stopDate.set(Calendar.MONTH, monthOfYear);
            stopDate.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialStopDateTime();
            /*if (startDate.getTimeInMillis() > stopDate.getTimeInMillis()){
                Toast toast = Toast.makeText(StatisticsActivity.this,
                        "Error. The start time must be less than end time ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                saveButton.setEnabled(false);
            } else{
                saveButton.setEnabled(true);
                setInitialCutDateTime();
            }*/
        }
    };

    // установка текста даты и времени завершения
    private void setInitialStopDateTime() {
        stopDateBox.setText(DateUtils.formatDateTime(this,
                stopDate.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR));
    }

    public void onClickTab1(View view){
        String str = "";

        Cursor cursor = db.query(StoreDb.Category.TABLE_NAME, null, null, null, null, null, null);
        int maxCount = 0;
        String nameCategory = "";


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getInt( cursor.getColumnIndex(StoreDb.Category.COLUMN_ID) );

                    String table = StoreDb.Record.TABLE_NAME;
                    String selection = StoreDb.Record.COLUMN_CATEGORY_ID + " = ?";  //cutegory_id = ?
                    String[] selectionArgs =  new String[]{String.valueOf(id)};
                    Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);
                    if (c.getCount() > maxCount){
                        maxCount = c.getCount();
                        nameCategory = cursor.getString( cursor.getColumnIndex(StoreDb.Category.COLUMN_NAME) );
                    }
                } while (cursor.moveToNext());
            }
        }

        str = "Count category " + nameCategory + "  = " + maxCount;
        resultTextTab1.setText(str);
    }

    public void onClickTab2(View view){
        String str = "";

        Cursor cursor = db.query(StoreDb.Category.TABLE_NAME, null, null, null, null, null, null);
        long maxTime = 0;
        String nameCategory = "";


        if (cursor != null) {
            if (cursor.moveToFirst()) {
                do {
                    long id = cursor.getInt( cursor.getColumnIndex(StoreDb.Category.COLUMN_ID) );

                    String table = StoreDb.Record.TABLE_NAME;
                    String selection = StoreDb.Record.COLUMN_CATEGORY_ID + " = ?";  //cutegory_id = ?
                    String[] selectionArgs =  new String[]{String.valueOf(id)};
                    Cursor c = db.query(table, null, selection, selectionArgs, null, null, null);

                    long sumTime = 0;

                    if (c != null) {
                        if (c.moveToFirst()) {
                            do {
                                long t = c.getInt( c.getColumnIndex(StoreDb.Record.COLUMN_CUT) );
                                sumTime += t;
                            } while (c.moveToNext());
                        }
                    }

                    if (sumTime > maxTime){
                        maxTime= sumTime;
                        nameCategory = cursor.getString( cursor.getColumnIndex(StoreDb.Category.COLUMN_NAME) );
                    }

                } while (cursor.moveToNext());
            }
        }

        str = "Sum time category " + nameCategory + "  = " + setInitialCutDateTime(maxTime);
        resultTextTab2.setText(str);
    }

    private String setInitialCutDateTime(long cut) {
        long h = cut / 3600000;
        long m = (cut - (h *3600000))/60000;
        return h  + "ч " + m  + "мин";
    }

    public void onClickTab3(View view){
        String str = " ";
        for (int i = 0; i < indexIdTab; i++){
            str = String.valueOf(idTab[i]) + " ";
        }
        resultTextTab3.setText(str);
    }

    public void onClickTab4(View view){
        Intent intent = new Intent(this, DiagramActivity.class);
        startActivity(intent);
    }
}
