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
import com.squareup.picasso.Picasso;

import java.io.File;
import java.util.Calendar;


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
    //!!!
    //LinkedList< Object[] > imagePaths = new LinkedList<>();
    String[] imagePaths = new String[3];
    //&&&&&&
    //Uri[] imagePathsUri = new Uri[3];
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
            Intent photoPickerIntent = new Intent(Intent.ACTION_PICK);
            photoPickerIntent.setType("image/*");

            String tableIm = "record as R inner join photo as P on R._id = P.record_id";
            String[] columnsIm = {"P._id as _id", "P.path as Path"};
            String selectionIm = "R." + StoreDb.Record.COLUMN_ID + " = ?";
            String[] selectionArgsIm =  new String[]{String.valueOf(recordId)};
            cursor = db.query(tableIm, columnsIm, selectionIm, selectionArgsIm, null, null, null);

            //cursor = db.query(tableIm, columnsIm, null, null, null, null, null);
            if (cursor != null && cursor.getCount() > 0){
                cursor.moveToFirst();
                int i = 0;
                do {
                   Picasso.with(this)
                            //.load(Uri.parse(cursor.getString(1)))
                            //.load(Uri.fromFile(new File("/storage/emulated/0/1.jpg")))
                            .load(Uri.fromFile(new File(cursor.getString(1))))
                            //.load("/storage/emulated/0/DCIM/HD/IMG_1485777590048.jpg")
                            .placeholder(R.drawable.ic_autorenew_black_24dp)
                            .error(R.drawable.ic_sync_disabled_black_24dp)
                            .into(imageViews[i]);

                    /*TextView textUi;
                    textUi = (TextView)findViewById(R.id.textUri);
                    String text = cursor.getString(1);
                    textUi.setText(text);*/
                    //!!!!!!!!!!!!!!!!

                    //String ph[] = cursor.getString(1).split("/");
                    //Uri u = Uri.parse(Arrays.toString(ph));
/*
                    String s1 = "/storage/extSdCard/DCIM/Camera/20170105_164830.jpg";
                    File file = new File(s1);

                    String s2 = file.getAbsolutePath();*/

                    /*File file = new File("/");
                    String s = file.getAbsolutePath();
                    File[] files = file.listFiles();*/

                    /*String strs[] = new String[files.length];
                    for(int ii = 0; ii < files.length; ii++){
                        strs[ii] = files[ii].getAbsolutePath();
                        File[] files1 = files[ii].listFiles();
                        if (files1 != null) {
                            String[] strs1 = new String[files1.length];
                            for (int jj = 0; jj < files1.length; jj++) {
                                strs1[jj] = files1[jj].getAbsolutePath();
                            }
                        }
                    }
                    int br = 0;*/

                    //file.
                    //Uri u = Uri.parse(cursor.getString(1));
                    //Uri u = Uri.parse(s1);

/*
                    Picasso.with(this)
                            .load(u)
                            .placeholder(R.drawable.ic_autorenew_black_24dp)
                            .error(R.drawable.ic_sync_disabled_black_24dp)
                            .into(imageViews[i]);
                    */
                    //imageViews[i].setImageURI(Uri.fromFile(new File(s1, s2)));


                    //////////////////
                    //Uri u = Uri.fromFile(new File(cursor.getString(1)));
                    //Uri u = Uri.fromFile(new File("/storage/emulated/0/DCIM/Программа 24.11.16г..jpg"));
                    //imageViews[countImage].setImageURI(u);
                    //Uri selectedImage = Uri.parse(cursor.getString(1));
                    //&&&&&&&&&&&&&&&&
                    //imageViews[i].setImageURI(Uri.fromFile(new File(Arrays.toString(ph))));
                    i++;
                }while (cursor.moveToNext() && i < 3);
            }
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
            //!!!!!!!!
            /*for(int i = 0; i < imagePaths.size() && i<3; i++){
                Object[] o = imagePaths.get(i);
                if (o != null && o[0] != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, (String) o[1]);
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, recordId);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }*/
            for(int i = 0; i < 3; i++){
                if (imagePaths[i] != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, imagePaths[i]);
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, recordId);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }
            //&&&&&&
            /*
            for (int i = 0; i < imagePathsUri.length && i < 3; i++){
                if (imagePathsUri[i] != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, imagePathsUri[i].toString());
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, recordId);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }*/
        } else {
            long id = db.insert(StoreDb.Record.TABLE_NAME, null, cv);

            //Сохранение картинок
            //!!!
            /*
            for(int i = 0; i<imagePaths.size() && i<3; i++){
                Object[] o = imagePaths.get(i);
                if (o != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, (String) o[1]);
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, id);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }
            */
            for(int i = 0; i < 3; i++){
                if (imagePaths[i] != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, imagePaths[i]);
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, id);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }
            //&&&&&&
            /*
            for (int i = 0; i < imagePathsUri.length && i < 3; i++){
                if (imagePathsUri[i] != null){
                    ContentValues c = new ContentValues();
                    c.put(StoreDb.Photo.COLUMN_PATH, imagePathsUri[i].toString());
                    c.put(StoreDb.Photo.COLUMN_RECORD_ID, id);
                    db.insert(StoreDb.Photo.TABLE_NAME, null, c);
                }
            }*/
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
        //!!!!
        //imagePaths.remove();
        for(int i = 0; i < 3; i++){
            imagePaths[i] = null;
        }
        //&&&&&&
        /*for(int i = 0; i < 3; i++){
            imagePathsUri[i] = null;
        }*/
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
                    //!!!!
                    Uri selectedImage = imageReturnedIntent.getData();
                    String uriStr = selectedImage.toString();

                    //Сохранение полного пути

                    String[] filePathColumn = {MediaStore.Images.Media.DATA};
                    Cursor cursor = getContentResolver().query(selectedImage, filePathColumn, null, null, null);
                    cursor.moveToFirst();
                    int columnIndex = cursor.getColumnIndex(filePathColumn[0]);
                    String filePath = cursor.getString(columnIndex);
                    cursor.close();

                    //&&&&&&
                    /*
                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getContentResolver(), selectedImage);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }*/

                    //imageViews[countImage].setImageBitmap(bitmap);
                    Picasso.with(this)
                            .load(selectedImage)
                            .placeholder(R.drawable.ic_autorenew_black_24dp)
                            .error(R.drawable.ic_sync_disabled_black_24dp)
                            .into(imageViews[countImage]);
                    //!!!!
                    /*Object[] o = new Object[2];
                    o[0] = null;
                    o[1] = filePath;
                    imagePaths.add(o);*/

                    imagePaths[countImage] = filePath;
                    //&&&&&&
                    //imagePathsUri[countImage] = selectedImage;
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
