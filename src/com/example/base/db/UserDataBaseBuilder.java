package com.example.base.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.example.base.type.User;

public class UserDataBaseBuilder extends DatabaseBuilder<User>{

	@Override
	public User build(Cursor c) {
		int columnId = c.getColumnIndex("user_id");
		int columnIdstr = c.getColumnIndex("user_idstr");
		
		User user = new User();
//		user.setUid( c.getString(columnId));
//		user.setAddress(c.getString(columnIdstr));
		return null;
	}

	@Override
	public ContentValues deconstruct(User user) {
		ContentValues values = new ContentValues();
//		values.put("radio_id", user.getUid());
		return values;
	}

}
