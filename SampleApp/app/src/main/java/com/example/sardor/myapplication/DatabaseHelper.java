package com.example.sardor.myapplication;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;

/**
 * Created by Sardor on 24/04/2017.
 */

public class DatabaseHelper extends SQLiteOpenHelper{
    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "complaintsDB";
    private static final String TABLE_COMPLAINTS = "complaintsTable";

    private static final String COLUMN_ID = "id";
    private static final String COLUMN_HEADER = "header";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_PERSONAL_ID = "personal_id";
    private static final String COLUMN_PHONE_NO = "phone_number";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_CONTACTS_TABLE = "CREATE TABLE " + TABLE_COMPLAINTS + "( "
                + COLUMN_ID + " INTEGER PRIMARY KEY, " + COLUMN_HEADER + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT, " + COLUMN_PERSONAL_ID + " INTEGER, "
                + COLUMN_PHONE_NO + " TEXT " + ")";
        db.execSQL(CREATE_CONTACTS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_COMPLAINTS);
        onCreate(db);
    }

    public void addComplaint(String header, String description, int personalId, String phoneNumber){

        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(COLUMN_ID, getLastIndex()+1);
        values.put(COLUMN_HEADER, header);
        values.put(COLUMN_DESCRIPTION, description);
        values.put(COLUMN_PERSONAL_ID, personalId);
        values.put(COLUMN_PHONE_NO, phoneNumber);
        db.insert(TABLE_COMPLAINTS, null, values);
        db.close();
    }

    public ArrayList<String> getAllComplaints() {
        ArrayList <String> arr = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur =  db.rawQuery( "select * from " + TABLE_COMPLAINTS, null );

        cur.moveToFirst();
        while(!cur.isAfterLast()){
            String ss="Complaint ID:" + cur.getString(0) + "\n" + "Header:" + cur.getString(1) + "\n" +
                    "Description:" + cur.getString(2) + "\n\n" + "Who Send:" + cur.getString(3) + "\n\n" +
                    "Phone num: " + cur.getString(4);
            arr.add(ss);
            cur.moveToNext();
        }

        return arr;
    }

    /*public ArrayList< ArrayList<String> > getAllComplaints() {
        ArrayList <String> arr = new ArrayList<String>();
        ArrayList< ArrayList <String> > array_list = new ArrayList< ArrayList <String> >();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur =  db.rawQuery( "select * from " + TABLE_COMPLAINTS, null );

        cur.moveToFirst();
        while(!cur.isAfterLast()){
            arr.add(cur.getString(0));
            arr.add(cur.getString(1));
            arr.add(cur.getString(2));
            arr.add(cur.getString(3));
            arr.add(cur.getString(4));
            array_list.add(arr);
            arr.clear();
            cur.moveToNext();
        }

        return array_list;
    }*/

    public ArrayList<String> getComplaint(int id){
        ArrayList <String> arr = new ArrayList<String>();
        SQLiteDatabase db = this.getReadableDatabase();
        //Cursor cur = db.rawQuery( "select * from " + TABLE_COMPLAINTS + " where " + COLUMN_ID + "=" + id, null );
        Cursor cur = db.rawQuery( "select * from " + TABLE_COMPLAINTS + " order by " + COLUMN_ID + " desc limit 1", null );
        cur.moveToFirst();
        arr.add(cur.getString(0));
        arr.add(cur.getString(1));
        arr.add(cur.getString(2));
        arr.add(cur.getString(3));
        arr.add(cur.getString(4));

        return arr;
    }

    public int getLastIndex(){
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cur = db.rawQuery("select " + COLUMN_ID + " from " + TABLE_COMPLAINTS + " order by " + COLUMN_ID + " desc limit 1", null);
        if (cur.moveToFirst()){
            return cur.getInt(0);
        }
        else{
            return 0;
        }
    }


}
