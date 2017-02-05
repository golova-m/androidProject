package com.example.lab32;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.ContentValues;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.lab32.database.DbOpenHelper;
import com.example.lab32.database.StoreDb;

import java.io.IOException;
import java.util.Calendar;
import java.util.LinkedList;

public class EditRecordActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener{
    long recordId=0;

    //category spinner
    Spinner categorySpinner;
    String categoryId;

    //view
    EditText descriptionBox;

    //Date and Time
    TextView startDateTimeBox;
    Calendar startDateTime = Calendar.getInstance();

    TextView stopDateTimeBox;
    Calendar stopDateTime = Calendar.getInstance();

    TextView cutBox;

    //Image
    int countImage = 0;
    ImageView[] imageViews = new ImageView[3];
    LinkedList< Object[] > imagePaths = new LinkedList<>();
    static final int GALLERY_REQUEST = 1;

    //Button
    Button loadImageButton;
    Button deleteImageButton;
    Button saveButton;
    Button delButton;

    //Database
    DbOpenHelper sqlHelper;
    SQLiteDatabase db;
    Cursor cursor;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_record);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            recordId = extras.getLong("id");
        }

        categorySpinner = (Spinner) findViewById(R.id.category);
        categorySpinner.setOnItemSelectedListener(this);

        descriptionBox = (EditText) findViewById(R.id.description);

        startDateTimeBox =(TextView)findViewById(R.id.startDateTime);
        setInitialStartDateTime();
        stopDateTimeBox =(TextView)findViewById(R.id.stopDateTime);
        setInitialStopDateTime();

        cutBox = (TextView) findViewById(R.id.textCut);
        setInitialCutDateTime();

        //image
        imageViews[0] = (ImageView) findViewById(R.id.imageCanve0);
        imageViews[1] = (ImageView) findViewById(R.id.imageCanve1);
        imageViews[2] = (ImageView) findViewById(R.id.imageCanve2);

        loadImageButton = (Button) findViewById(R.id.loadImageButton);
        deleteImageButton = (Button) findViewById(R.id.deleteImageButton);
        deleteImageButton.setEnabled(false);
        saveButton = (Button) findViewById(R.id.save);
        delButton = (Button) findViewById(R.id.delete);

        //database
        sqlHelper = new DbOpenHelper(getApplicationContext());
    }

    @Override
    public void onResume() {
        super.onResume();

        db = sqlHelper.getWritableDatabase();

        //СОЗДАНИЕ СПИСКА ДЛЯ SPINNER
        cursor = db.query(StoreDb.Category.TABLE_NAME, null, null, null, null, null, null);
        String[] categoryNames = null;
        if (cursor != null) {
            categoryNames = new String[ cursor.getCount() ];
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    categoryNames[i] = cursor.getString(1);
                    i++;
                } while (cursor.moveToNext());
            }
        }
        // Создадим ArrayAdapter, используя строковый массив и разметку с элементом выпадающего списка
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(this, android.R.layout.simple_spinner_item, categoryNames);
        // Указываем разметку, которая используется при раскрытии списка
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        // Применяем адаптер к списку
        categorySpinner.setAdapter(adapter);

        // если 0, то добавление
        if (recordId > 0) {
            // получаем элемент по id из бд
            String table = "category as C inner join record as R on C._id = R.category_id";
            String[] columns = {
                    "R." + StoreDb.Record.COLUMN_ID + " as _id", //0
                    "R." + StoreDb.Record.COLUMN_DESCRIPTION + " as Description",  //1
                    "R." + StoreDb.Record.COLUMN_START_DATE_TIME + " as Start", //2
                    "R." + StoreDb.Record.COLUMN_STOP_DATE_TIME + " as Stop", //3
                    "R." + StoreDb.Record.COLUMN_CUT + " as Cut", //4
                    "C." + StoreDb.Category.COLUMN_NAME + " as Name", //5
                    "C." + StoreDb.Category.COLUMN_ID + " as CategoryId" //6
                };
            String selection = "R." + StoreDb.Record.COLUMN_ID + " = ?";
            String[] selectionArgs =  new String[]{String.valueOf(recordId)};
            cursor = db.query(table, columns, selection, selectionArgs, null, null, null);
            cursor.moveToFirst();

            //Заполнение полей
            categoryId = cursor.getString(0);
            descriptionBox.setText( cursor.getString(1) );

            startDateTime.setTimeInMillis( cursor.getLong(2) );
            setInitialStartDateTime();

            stopDateTime.setTimeInMillis( cursor.getLong(3) );
            setInitialStopDateTime();

            setInitialCutDateTime();

            //Задание значения Spinner по умолчанию
            int positionInSpinner = adapter.getPosition( cursor.getString(5) );
            categorySpinner.setSelection( positionInSpinner );

            //Загрузка изображений
            /*String tableIm = "record as R inner join photo as P on R._id = P.record_id";
            String[] columnsIm = {"P._id as _id", "P.path as Path"};
            cursor = db.query(tableIm, columnsIm, null, null, null, null, null);
            if (cursor != null) {
                if (cursor.moveToFirst()) {
                    int i = 0;
                    do {
                        Object[] o = new Object[2];
                        o[0] = cursor.getString(0);
                        o[1] = cursor.getString(1);
                        imagePaths.add(o);

                        //Загрузка изображений на страницу

                        //Uri u = Uri.fromFile(new File(cursor.getString(1)));
                        //Uri u = Uri.fromFile(new File("/storage/emulated/0/DCIM/Программа 24.11.16г..jpg"));
                        //imageViews[countImage].setImageURI(u);
                        //Uri selectedImage = Uri.parse(cursor.getString(1));
                        try {
                            bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        imageViews[countImage].setImageBitmap(bitmap);
                        countImage++;
                        i++;
                    } while (cursor.moveToNext() && i<3);
                }
            }*/


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

    //SPINNER
    public void onItemSelected(AdapterView<?> parent, View view, int pos, long id) {
        // Получаем выбранный объект
        Object item = parent.getItemAtPosition(pos);
        String categoryName = item.toString();

        String table = StoreDb.Category.TABLE_NAME;
        String selection = StoreDb.Category.COLUMN_NAME + " = ?";
        String[] selectionArgs =  new String[]{categoryName};
        cursor = db.query(table, null, selection, selectionArgs, null, null, null);

        cursor.moveToFirst();
        categoryId = cursor.getString(0);
    }

    public void onNothingSelected(AdapterView<?> parent) {
        // Обработка события
    }
    ////

    public void onSave(View view){
        ContentValues cv = new ContentValues();
        cv.put(StoreDb.Record.COLUMN_DESCRIPTION, descriptionBox.getText().toString()); //довалили описание
        cv.put(StoreDb.Record.COLUMN_START_DATE_TIME, startDateTime.getTimeInMillis() ); //доваление время начала
        cv.put(StoreDb.Record.COLUMN_STOP_DATE_TIME, stopDateTime.getTimeInMillis() ); //доваление время конца
        cv.put(StoreDb.Record.COLUMN_CUT, stopDateTime.getTimeInMillis() - startDateTime.getTimeInMillis() ); //доваление промежутка времени
        cv.put(StoreDb.Record.COLUMN_CATEGORY_ID, Long.valueOf(categoryId));

        if (recordId > 0) {
            db.update(StoreDb.Record.TABLE_NAME, cv, StoreDb.Category.COLUMN_ID + "=" + String.valueOf(recordId), null);

            //Сохранение картинок
            /*for(int i = 0; i<imagePaths.size() && i<3; i++){
                Object[] o = imagePaths.get(i);
                if (o != null && o[0] != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, (String) o[1]);
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, recordId);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }*/
        } else {
            long id = db.insert(StoreDb.Record.TABLE_NAME, null, cv);

            //Сохранение картинок
            for(int i = 0; i<imagePaths.size() && i<3; i++){
                Object[] o = imagePaths.get(i);
                if (o != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, (String) o[1]);
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, id);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }
        }
        goMain();
    }

    public void onDelete(View view) {
        db.delete(StoreDb.Record.TABLE_NAME, "_id = ?", new String[]{String.valueOf(recordId)});
        goMain();
    }


    public void goMain(){
        // закрываем подключение
        db.close();
        // переход к главной activity
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        startActivity(intent);
    }

    // отображаем диалоговое окно для выбора даты начала
    public void setStartDate(View v) {
        new DatePickerDialog(EditRecordActivity.this, d,
                startDateTime.get(Calendar.YEAR),
                startDateTime.get(Calendar.MONTH),
                startDateTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени начала
    public void setStartTime(View v) {
        new TimePickerDialog(EditRecordActivity.this, t,
                startDateTime.get(Calendar.HOUR_OF_DAY),
                startDateTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка текста даты и времени начала
    private void setInitialStartDateTime() {
        startDateTimeBox.setText(DateUtils.formatDateTime(this,
                startDateTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени начала
    TimePickerDialog.OnTimeSetListener t = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            startDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            startDateTime.set(Calendar.MINUTE, minute);
            setInitialStartDateTime();
            if (startDateTime.getTimeInMillis() > stopDateTime.getTimeInMillis()){
                Toast toast = Toast.makeText(EditRecordActivity.this,
                        "Error. The start time must be less than end time ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                saveButton.setEnabled(false);
            } else{
                saveButton.setEnabled(true);
                setInitialCutDateTime();
            }
        }
    };

    // установка обработчика выбора даты начала
    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            startDateTime.set(Calendar.YEAR, year);
            startDateTime.set(Calendar.MONTH, monthOfYear);
            startDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialStartDateTime();
            if (startDateTime.getTimeInMillis() > stopDateTime.getTimeInMillis()){
                Toast toast = Toast.makeText(EditRecordActivity.this,
                        "Error. The start time must be less than end time ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                saveButton.setEnabled(false);
            } else{
                saveButton.setEnabled(true);
                setInitialCutDateTime();
            }
        }
    };

    // отображаем диалоговое окно для выбора даты завершения
    public void setStopDate(View v) {
        new DatePickerDialog(EditRecordActivity.this, dd,
                stopDateTime.get(Calendar.YEAR),
                stopDateTime.get(Calendar.MONTH),
                stopDateTime.get(Calendar.DAY_OF_MONTH))
                .show();
    }

    // отображаем диалоговое окно для выбора времени завершения
    public void setStopTime(View v) {
        new TimePickerDialog(EditRecordActivity.this, tt,
                stopDateTime.get(Calendar.HOUR_OF_DAY),
                stopDateTime.get(Calendar.MINUTE), true)
                .show();
    }

    // установка текста даты и времени завершения
    private void setInitialStopDateTime() {
        stopDateTimeBox.setText(DateUtils.formatDateTime(this,
                stopDateTime.getTimeInMillis(),
                DateUtils.FORMAT_SHOW_DATE | DateUtils.FORMAT_SHOW_YEAR
                        | DateUtils.FORMAT_SHOW_TIME));
    }

    // установка обработчика выбора времени завершения
    TimePickerDialog.OnTimeSetListener tt = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
            stopDateTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            stopDateTime.set(Calendar.MINUTE, minute);
            setInitialStopDateTime();
            if (startDateTime.getTimeInMillis() > stopDateTime.getTimeInMillis()){
                Toast toast = Toast.makeText(EditRecordActivity.this,
                        "Error. The start time must be less than end time ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                saveButton.setEnabled(false);
            } else{
                saveButton.setEnabled(true);
                setInitialCutDateTime();
            }
        }
    };

    // установка обработчика выбора даты завершения
    DatePickerDialog.OnDateSetListener dd = new DatePickerDialog.OnDateSetListener() {
        public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
            stopDateTime.set(Calendar.YEAR, year);
            stopDateTime.set(Calendar.MONTH, monthOfYear);
            stopDateTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
            setInitialStopDateTime();
            if (startDateTime.getTimeInMillis() > stopDateTime.getTimeInMillis()){
                Toast toast = Toast.makeText(EditRecordActivity.this,
                        "Error. The start time must be less than end time ", Toast.LENGTH_SHORT);
                toast.setGravity(Gravity.CENTER, 0, 0);
                toast.show();
                saveButton.setEnabled(false);
            } else{
                saveButton.setEnabled(true);
                setInitialCutDateTime();
            }
        }
    };

    private void setInitialCutDateTime() {
        long cut = stopDateTime.getTimeInMillis() - startDateTime.getTimeInMillis();
        long h = cut / 3600000;
        long m = (cut - (h *3600000))/60000;
        String str = h  + "ч " + m  + "мин";
        cutBox.setText(str);
    }

    //IMAGE
    public void onLoadImage(View v) {
        Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
        photoPickerIntent.setType("image/*");
        startActivityForResult(photoPickerIntent, GALLERY_REQUEST);
    }

    public void onDeleteImage(View v) {
        countImage--;
        imageViews[countImage].setImageDrawable(null);
        imagePaths.remove();

        if (countImage < 3){
            loadImageButton.setEnabled(true);
        }

        if (countImage == 0){
            deleteImageButton.setEnabled(false);
        }

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent imageReturnedIntent) {
        super.onActivityResult(requestCode, resultCode, imageReturnedIntent);

        Bitmap bitmap = null;

        switch(requestCode) {
            case GALLERY_REQUEST:
                if(resultCode == RESULT_OK){
                    Uri selectedImage = imageReturnedIntent.getData();
                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();

                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }

                    imageViews[countImage].setImageBitmap(bitmap);

                    Object[] o = new Object[2];
                    o[0] = null;
                    o[1] = filePath;
                    imagePaths.add(o);
                    countImage++;

                    if (countImage == 3){
                        loadImageButton.setEnabled(false);
                    }

                    if (countImage > 0){
                        deleteImageButton.setEnabled(true);
                    }
                }
        }
    }
}
