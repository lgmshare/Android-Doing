package com.example.base.db.cache;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class CacheSqliteHelper extends SQLiteOpenHelper {

    public static final String DATABASE_NAME = "impp";
    
    public static final String TABLE_CARD = "CARD";
    public static final String TABLE_CACHE = "CACHE";
    
    public static  String TABLE_CACHE_FIELD_KEY = "key";
    public static  String TABLE_CACHE_FIELD_VALUE = "value";
    
    public static final int DATABASE_VERSION = 1;

    public CacheSqliteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
    	db.execSQL("CREATE TABLE "+TABLE_CACHE+" (" +
    			"key TEXT," +
    			"value TEXT," +
    			"_id INTEGER PRIMARY KEY AUTOINCREMENT );");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + DATABASE_NAME);
        onCreate(db);
    }

}
