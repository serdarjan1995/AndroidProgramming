package sardor.odev3.mobil.phoneorientation;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Color;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Environment;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.os.Handler;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Date;

/**
 * An example full-screen activity that shows and hides the system UI (i.e.
 * status bar and navigation/system bar) with user interaction.
 */
public class PhoneOrientation extends AppCompatActivity implements SensorEventListener{
    private SensorManager sMgr;
    private Sensor accelerometer;
    private TextView textviewContentX;
    private TextView textviewContentY;
    private TextView textviewContentZ;
    private TextView textviewOrientaion;
    private GraphView graph;
    private Button exitButton;
    private Button saveButton;
    private boolean writeEnabled=false;
    private LineGraphSeries<DataPoint> seriesX;
    private LineGraphSeries<DataPoint> seriesY;
    private LineGraphSeries<DataPoint> seriesZ;
    private ArrayList<MyGraphPoints> data = new ArrayList<MyGraphPoints>();
    private int currentTime=-1;
    private int columnsOnGraph=0;
    private File file;
    private OutputStream outputStream;

    private static final boolean AUTO_HIDE = true;
    private static final int AUTO_HIDE_DELAY_MILLIS = 3000;
    private static final int UI_ANIMATION_DELAY = 300;
    private final Handler mHideHandler = new Handler();
    private View mContentView;
    private final Runnable mHidePart2Runnable = new Runnable() {
        @SuppressLint("InlinedApi")
        @Override
        public void run() {
            mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE
                    | View.SYSTEM_UI_FLAG_FULLSCREEN
                    | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                    | View.SYSTEM_UI_FLAG_IMMERSIVE_STICKY
                    | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION
                    | View.SYSTEM_UI_FLAG_HIDE_NAVIGATION);
        }
    };
    private final Runnable mShowPart2Runnable = new Runnable() {
        @Override
        public void run() {
            ActionBar actionBar = getSupportActionBar();
            if (actionBar != null) {
                actionBar.show();
            }
        }
    };
    private boolean mVisible;
    private final Runnable mHideRunnable = new Runnable() {
        @Override
        public void run() {
            hide();
        }
    };
    private final View.OnTouchListener mDelayHideTouchListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View view, MotionEvent motionEvent) {
            if (AUTO_HIDE) {
                delayedHide(AUTO_HIDE_DELAY_MILLIS);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_phone_orientation);
        mVisible = true;
        mContentView = findViewById(R.id.textview_orientation);
        mContentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                toggle();
            }
        });

        sMgr = (SensorManager)this.getSystemService(SENSOR_SERVICE);
        accelerometer = sMgr.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sMgr.registerListener(this,accelerometer,1000000);
        textviewContentX = (TextView)findViewById(R.id.textview_content_x);
        textviewContentY = (TextView)findViewById(R.id.textview_content_y);
        textviewContentZ = (TextView)findViewById(R.id.textview_content_z);
        textviewOrientaion = (TextView)findViewById(R.id.textview_orientation);
        exitButton = (Button)findViewById(R.id.exit_button);
        exitButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                destroyApp();
            }
        });
        saveButton = (Button)findViewById(R.id.save_file_button);
        saveButton.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                if(writeEnabled){
                    writeEnabled=false;
                    saveButton.setText("Save to file");
                    saveButton.setBackgroundColor(Color.parseColor("#ffaa66cc"));
                }
                else{
                    writeEnabled=true;
                    saveButton.setText("Stop Writing");
                    saveButton.setBackgroundColor(Color.RED);
                }

            }
        });
        graph = (GraphView) findViewById(R.id.graph);
        graph.setTitle("Accelerometer data");
        graph.setTitleColor(Color.BLUE);

        if(isExternalStorageWritable()){
            file = new File(getExternalFilesDir(null), "orientations_log.txt");
            try{
                outputStream  = new FileOutputStream(file);

            }
            catch (Exception e){
                saveButton.setEnabled(false);
                Toast.makeText(this,"Error File creating",Toast.LENGTH_LONG);
            }
        }

    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        delayedHide(100);
    }

    private void toggle() {
        if (mVisible) {
            hide();
        } else {
            show();
        }
    }

    private void hide() {
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.hide();
        }
        mVisible = false;

        mHideHandler.removeCallbacks(mShowPart2Runnable);
        mHideHandler.postDelayed(mHidePart2Runnable, UI_ANIMATION_DELAY);
    }

    @SuppressLint("InlinedApi")
    private void show() {
        mContentView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN
                | View.SYSTEM_UI_FLAG_LAYOUT_HIDE_NAVIGATION);
        mVisible = true;

        mHideHandler.removeCallbacks(mHidePart2Runnable);
        mHideHandler.postDelayed(mShowPart2Runnable, UI_ANIMATION_DELAY);
    }

    private void delayedHide(int delayMillis) {
        mHideHandler.removeCallbacks(mHideRunnable);
        mHideHandler.postDelayed(mHideRunnable, delayMillis);
    }


    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        float x = sensorEvent.values[0];
        float y = sensorEvent.values[1];
        float z = sensorEvent.values[2];
        graph.removeAllSeries();

        if (accelerometer.getType() == Sensor.TYPE_ACCELEROMETER) {
            textviewContentX.setText("X= " + x);
            textviewContentY.setText("Y= " + y);
            textviewContentZ.setText("Z= " + z);
        }
        if( (z>8.40f && z<10.9f) && (y>-2.30f && y<2.30f)){
            textviewOrientaion.setText("FLAT");
        }
        else if( (z<-8.40f && z>-10.9f) && (y>-2.30f && y<2.30f)){
            textviewOrientaion.setText("FLAT UPSIDEDOWN");
        }
        else if( (y>-4.30f && y<4.30f) && (z>-3.30f && z<3.30f) && (x>8.30f && x<10.9f) ){
            textviewOrientaion.setText("LANDSCAPE UP");
        }
        else if( (y>-4.30f && y<4.30f) && (z>-3.30f && z<3.30f) && (x<-8.30f && x>-11.1f) ){
            textviewOrientaion.setText("LANDSCAPE DOWN");
        }
        else if( (y>8.0f && y<11.8f) && (z>-3.30f && z<3.30f) && (x<3.75f && x>-3.75f) ){
            textviewOrientaion.setText("PORTRAIT UP");
        }
        else if( (y<-8.0f && y>-11.8f) && (z>-3.30f && z<3.30f) && (x<3.75f && x>-3.75f) ){
            textviewOrientaion.setText("PORTRAIT DOWN");
        }
        currentTime++;
        data.add(new MyGraphPoints((int)x,(int)y,(int)z,currentTime));
        if(outputStream!=null && writeEnabled){
            Date d = new Date();
            CharSequence s  = DateFormat.format("EEEE, MMMM d, yyyy ", d.getTime());
            String wrt = s.toString();
            wrt+=" orientation: "+textviewOrientaion.getText()+" x: "+x+" y: "+y+" z: "+z+"\n";
            try {
                outputStream.write(wrt.getBytes());
            } catch (IOException e) {
                Toast.makeText(this,"Error File writing",Toast.LENGTH_LONG);
            }
        }

        if(columnsOnGraph<5){
            columnsOnGraph++;
        }
        if(currentTime>2){
            seriesX = new LineGraphSeries<>(makeDataX(columnsOnGraph,data));
            seriesX.setTitle("X");
            seriesX.setColor(Color.GREEN);
            seriesX.setDrawDataPoints(true);
            seriesX.setDataPointsRadius(8);
            seriesX.setThickness(6);
            graph.addSeries(seriesX);

            seriesY = new LineGraphSeries<>(makeDataY(columnsOnGraph,data));
            seriesY.setTitle("Y");
            seriesY.setColor(Color.YELLOW);
            seriesY.setDrawDataPoints(true);
            seriesY.setDataPointsRadius(8);
            seriesY.setThickness(6);
            graph.addSeries(seriesY);

            seriesZ = new LineGraphSeries<>(makeDataZ(columnsOnGraph,data));
            seriesZ.setTitle("Z");
            seriesZ.setColor(Color.RED);
            seriesZ.setDrawDataPoints(true);
            seriesZ.setDataPointsRadius(8);
            seriesZ.setThickness(6);
            graph.addSeries(seriesZ);
        }

    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        sMgr.registerListener(this,accelerometer,1000000);
    }

    protected void onResume(){
        super.onResume();
        sMgr.registerListener(this,accelerometer,1000000);
    }

    protected void onPause(){
        super.onPause();
        sMgr.unregisterListener(this);
    }

    protected void destroyApp(){
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
        sMgr.unregisterListener(this);
        finish();
        System.exit(0);
    }

    public DataPoint[] makeDataX(int count, ArrayList<MyGraphPoints> data){
        DataPoint[] dt = new DataPoint[count];
        int dtIndex=0;
        for(int i=data.size()-count; i<data.size(); i++) {
            dt[dtIndex++]=new DataPoint(data.get(i).getT(), data.get(i).getX());
        }
        return dt;
    }

    public DataPoint[] makeDataY(int count, ArrayList<MyGraphPoints> data){
        DataPoint[] dt = new DataPoint[count];
        int dtIndex=0;
        for(int i=data.size()-count; i<data.size(); i++) {
            dt[dtIndex++]=new DataPoint(data.get(i).getT(), data.get(i).getY());
        }
        return dt;
    }

    public DataPoint[] makeDataZ(int count, ArrayList<MyGraphPoints> data){
        DataPoint[] dt = new DataPoint[count];
        int dtIndex=0;
        for(int i=data.size()-count; i<data.size(); i++) {
            dt[dtIndex++]=new DataPoint(data.get(i).getT(), data.get(i).getZ());
        }
        return dt;
    }

    public boolean isExternalStorageWritable() {
        String state = Environment.getExternalStorageState();
        if (Environment.MEDIA_MOUNTED.equals(state)) {
            return true;
        }
        return false;
    }
}
