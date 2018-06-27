package semesterproject.mobileprogramming.com.trackme;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;


public class DatabaseHelper extends SQLiteOpenHelper{
    private static int DATABASE_VERSION = 2;
    private static final String DATABASE_NAME = "location";
    private static final String TABLE_NAME = "geolocation";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_ALTITUDE = "altitude";
    private static final String COLUMN_LONGITUDE = "longitude";
    private static final String COLUMN_LATITUDE = "latitude";
    private static final String COLUMN_TIME = "time";
    private static final String COLUMN_ACTIVITY_TYPE = "activity";
    private static final String CREATE_LOCATION_TABLE = "CREATE TABLE " + TABLE_NAME + "( "
            + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_ALTITUDE + " REAL,"
            + COLUMN_LONGITUDE + " REAL, " + COLUMN_LATITUDE + " REAL, "
            + COLUMN_TIME + " TEXT, " + COLUMN_ACTIVITY_TYPE + " TEXT " + ")";


    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_LOCATION_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
        DATABASE_VERSION = newVersion;
    }

    public void addGeolocation(double altitude, double longitude, double latitude, String time, String activity){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getLastIndex()+1);
        values.put(COLUMN_ALTITUDE, altitude);
        values.put(COLUMN_LONGITUDE, longitude);
        values.put(COLUMN_LATITUDE, latitude);
        values.put(COLUMN_TIME, time);
        values.put(COLUMN_ACTIVITY_TYPE, activity);
        db.insert(TABLE_NAME, null, values);
        db.close();
    }

    public ArrayList<LocationInfo> getAllRecords(){
        ArrayList <LocationInfo> arr = new ArrayList<LocationInfo>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur =  db.rawQuery( "select * from " + TABLE_NAME, null );

        cur.moveToFirst();
        LocationInfo locInfo;
        while(!cur.isAfterLast()){
            locInfo = new LocationInfo(cur.getFloat(1),cur.getFloat(2),cur.getFloat(3),cur.getString(4),cur.getString(5));
            arr.add(locInfo);
            cur.moveToNext();
            //System.out.println(arr.get(arr.size()-1).toString());
        }

        return arr;
    }

    public LocationInfo getLastLocInfo(){
        ArrayList <LocationInfo> arr;
        if((arr=getAllRecords()).size()!=0){
            return arr.get(arr.size()-1);
        }
        return null;
    }

    public int getLastIndex(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select " + COLUMN_ID + " from " + TABLE_NAME + " order by " + COLUMN_ID + " desc limit 1", null);
        if (cur.moveToFirst()){
            return cur.getInt(0);
        }
        else{
            return 0;
        }
    }

    public void resetData(){
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        db.execSQL(CREATE_LOCATION_TABLE);
    }

}
