package com.example.base.db;

import java.util.ArrayList;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.example.base.type.User;

public class DatabaseImpl implements Database{

	private static final String DB_NAME = "db_base";
	
	private static final String TABLE_RECENT_RADIOS = "recent_radios";
	private Context context;
	
	public DatabaseImpl(Context context){
		this.context = context;
		create();
	}
	
	private void create(){
		SQLiteDatabase db = context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
		// create tables if necessary
		db.execSQL("CREATE TABLE IF NOT EXISTS "
				+ TABLE_RECENT_RADIOS
				+ " (key VARCHAR UNIQUE,"
				+ " value VARCHAR,"
				+ " _id INTEGER PRIMARY KEY AUTOINCREMENT);");
		db.close();
	}
	
	private SQLiteDatabase getDb(){
		return context.openOrCreateDatabase(DB_NAME, Context.MODE_PRIVATE, null);
	}

	@Override
	public void addUserToRecent(User radio) {
		SQLiteDatabase db = getDb();
		// put radio data into the table
		ContentValues values = new UserDataBaseBuilder().deconstruct(radio);
		
		String[] whereArgs = {""+radio.getId()};
		int row_count = db.update(TABLE_RECENT_RADIOS, values, "radio_id=?", whereArgs);
		if(row_count == 0){
			db.insert(TABLE_RECENT_RADIOS, null, values);
		}
		
		db.close();
	}

	@Override
	public ArrayList<User> getRecentUsers(int limit) {
		ArrayList<User> radios = new ArrayList<User>(); 
		SQLiteDatabase db = getDb();
		
		String[] columns = {"radio_id","radio_idstr","radio_name","radio_image"};
		Cursor query = db.query(TABLE_RECENT_RADIOS, columns, "", null, null, null, "radio_date DESC", ""+limit);
		
		if(query != null){
			query.moveToFirst();
			
			while(!query.isAfterLast()){
				User radio = new UserDataBaseBuilder().build(query);
				radios.add(radio);
				query.moveToNext();
			}
		}
		db.close();
		return radios;
	}
	
}
