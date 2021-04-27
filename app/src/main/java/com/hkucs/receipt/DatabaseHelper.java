package com.hkucs.receipt;

import android.content.ContentValues;
import android.content.Context;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.Nullable;


public class DatabaseHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "Receipts.db";
    public static final String TABLE_NAME = "Receipt_table";
    public static final String COL_1 = "ID";
    public static final String COL_2 = "Name";
    public static final String COL_3 = "Date_of_purchase";
    public static final String COL_4 = "Price";
    public static final String COL_5 = "Image";
    public static final String COL_6 = "Warranty_period";
    public static final String COL_7 = "Category";

    public DatabaseHelper(@Nullable Context context) {
        super(context, DATABASE_NAME, null, 1);

    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table Receipt_table (ID INTEGER PRIMARY KEY AUTOINCREMENT,Name TEXT NOT NULL, Date_of_purchase DATE NOT NULL, Price DECIMAL(7,2) NOT NULL,Image BLOB NOT NULL,Warranty_period INTEGER NOT NULL, Category TEXT NOT NULL);");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion){
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public void delete(String Name) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_NAME, "Name = ?", new String[]{Name});
    }

    public boolean insertData(String Name, String Date_of_purchase, String Price, byte[] Image, String Warranty_period, String Category ) throws SQLException {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_2,Name);
        contentValues.put(COL_3, Date_of_purchase);
        contentValues.put(COL_4,Price);
        contentValues.put(COL_5, Image);
        contentValues.put(COL_6,Warranty_period);
        contentValues.put(COL_7,Category);
        long result = db.insert(TABLE_NAME, null, contentValues);
        if (result==-1){
            return false;
        }else{
            return true;
        }
    }
}
