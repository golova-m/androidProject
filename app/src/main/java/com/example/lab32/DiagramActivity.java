package com.example.lab32;

import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

import com.example.lab32.database.DbOpenHelper;
import com.example.lab32.database.StoreDb;

import org.achartengine.ChartFactory;
import org.achartengine.model.CategorySeries;
import org.achartengine.renderer.DefaultRenderer;
import org.achartengine.renderer.SimpleSeriesRenderer;

public class DiagramActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
    long values[];
    String names[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = buildIntent();

        startActivity(intent);
    }

    public Intent buildIntent() {

        // шаг 2
        calculateData();

        int[] colors = new int[] { Color.BLUE, Color.GREEN, Color.MAGENTA,

                Color.YELLOW, Color.CYAN, Color.GRAY, Color.RED, Color.WHITE };



        CategorySeries series = new CategorySeries("Pie Chart");  // шаг 3

        DefaultRenderer dr = new DefaultRenderer();   // шаг 4

        for (int v=0; v < names.length; v++){    // шаг 5

            if (values[v] != 0){
                series.add(names[v], values[v]);

                SimpleSeriesRenderer r = new SimpleSeriesRenderer();

                r.setColor(colors[v]);

                dr.addSeriesRenderer(r);
            }
        }

        dr.setZoomButtonsVisible(true);

        dr.setZoomEnabled(true);

        dr.setChartTitleTextSize(100);

        return ChartFactory.getPieChartIntent(    // шаг 6

                this, series, dr, "Pie of bars");

    }

    private void calculateData() {
        DbOpenHelper sqlHelper = new DbOpenHelper(getApplicationContext());
        SQLiteDatabase db = sqlHelper.getWritableDatabase();

        Cursor cursor = db.query(StoreDb.Category.TABLE_NAME, null, null, null, null, null, null);

        values = new long[ cursor.getCount() ];
        names = new String[ cursor.getCount() ];

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    long id = cursor.getInt( cursor.getColumnIndex(StoreDb.Category.COLUMN_ID) );
                    names[i] = cursor.getString( cursor.getColumnIndex(StoreDb.Category.COLUMN_NAME) );

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

                    values[i] = sumTime;
                    i++;
                } while (cursor.moveToNext());
            }
        }
    }
}

//public class DiagramActivity extends AppCompatActivity {
    /** Called when the activity is first created. */
  /*  long values[];
    String names[];

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diagram);

        LinearLayout linear = (LinearLayout) findViewById(R.id.linear);
        calculateData();
        linear.addView(new MyGraphview(this, names, values));
    }

    private void calculateData() {
        DbOpenHelper sqlHelper = new DbOpenHelper(getApplicationContext());
        SQLiteDatabase db = sqlHelper.getWritableDatabase();

        Cursor cursor = db.query(StoreDb.Category.TABLE_NAME, null, null, null, null, null, null);

        values = new long[ cursor.getCount() ];
        names = new String[ cursor.getCount() ];

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int i = 0;
                do {
                    long id = cursor.getInt( cursor.getColumnIndex(StoreDb.Category.COLUMN_ID) );
                    names[i] = cursor.getString( cursor.getColumnIndex(StoreDb.Category.COLUMN_NAME) );

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

                    values[i] = sumTime;
                    i++;
                } while (cursor.moveToNext());
            }
        }
    }

    public class MyGraphview extends View
    {
        private Paint paint=new Paint(Paint.ANTI_ALIAS_FLAG);
        private float[] value_degree;
        private int[] COLORS={
                Color.BLACK,
                Color.BLUE,
                Color.CYAN,
                Color.DKGRAY,
                Color.GRAY,
                Color.GREEN,
                Color.LTGRAY,
                Color.MAGENTA,
                Color.RED,
                Color.TRANSPARENT,
                Color.WHITE,
                Color.YELLOW
            };
        RectF rectf = new RectF (10, 10, 200, 200);
        int temp = 0;

        public MyGraphview(Context context, String[] names, long[] values) {

            super(context);
            value_degree = new float[values.length];
            for(int i = 0; i < values.length; i++) {
                long v = values[i]/60000;
                value_degree[i] = Float.valueOf( String.valueOf(v));
            }
        }

        @Override
        protected void onDraw(Canvas canvas) {
            // TODO Auto-generated method stub
            super.onDraw(canvas);
            float temp = 0;
            for (int i = 0; i < value_degree.length && i < 12; i++) {
                if (i == 0) {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, 0, value_degree[i], true, paint);
                } else {
                    paint.setColor(COLORS[i]);
                    canvas.drawArc(rectf, temp, value_degree[i], true, paint);
                    temp += (float) value_degree[i - 1];
                }
            }
        }

    }
}*/
