package com.example.base.db.cache;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;

import com.example.base.exception.ParseException;
import com.example.base.parser.IParser;
import com.example.base.type.IType;

public class CacheManager {

	private Context mContext;

	public CacheManager(Context context) {
		this.mContext = context;
	}

	/**
	 * insert Cache
	 * 
	 * @param
	 */
	public void addCache(String key, String value) {
		ContentValues contentValues = null;
		contentValues = new ContentValues();
		contentValues.put(CacheSqliteHelper.TABLE_CACHE_FIELD_KEY, key);
		contentValues.put(CacheSqliteHelper.TABLE_CACHE_FIELD_VALUE, value);
		mContext.getContentResolver().insert(CacheContentProvider.CONTENT_URI,
				contentValues);
	}

	/**
	 * update Cache
	 * 
	 * @param
	 */
	public void updateCache(String key, String values) {
		ContentValues contentValues = null;
		contentValues = new ContentValues();
		contentValues.put(CacheSqliteHelper.TABLE_CACHE_FIELD_KEY, key);
		contentValues.put(CacheSqliteHelper.TABLE_CACHE_FIELD_VALUE, values);
		mContext.getContentResolver().update(CacheContentProvider.CONTENT_URI,
				contentValues,
				" " + CacheSqliteHelper.TABLE_CACHE_FIELD_KEY + " = ? ",
				new String[] { key });
	}

	/**
	 * del all Cache
	 * 
	 * @param
	 */
	public void delCardsAllList() {
		mContext.getContentResolver().delete(CacheContentProvider.CONTENT_URI,
				" _id > ? ", new String[] { "0" });
	}

	/**
	 * 通过键删除对应的值
	 * 
	 * @param key
	 */
	public void delValuesByKey(String key) {
		mContext.getContentResolver().delete(CacheContentProvider.CONTENT_URI,
				" " + CacheSqliteHelper.TABLE_CACHE_FIELD_KEY + " = ? ",
				new String[] { key });
	}

	/**
	 * query Cache by key
	 * 
	 * @return IType
	 */
	public IType getCacheByKey(String key, IParser<? extends IType> parser) {
		IType iType = null;
		Cursor cursor = mContext.getContentResolver().query(
				CacheContentProvider.CONTENT_URI, null,
				" " + CacheSqliteHelper.TABLE_CACHE_FIELD_KEY + " = ? ",
				new String[] { key }, null);
		String response = null;
		while (cursor != null && cursor.moveToNext()) {
			response = cursor.getString(cursor
					.getColumnIndex(CacheSqliteHelper.TABLE_CACHE_FIELD_VALUE));
		}
		if (cursor != null) {
			cursor.close();
		}
		try {
			if (response != null) {
				iType = parser.parse(response);
			}
		} catch (ParseException e) {
			iType = null;
			e.printStackTrace();
		}
		return iType;
	}

	public String getCacheByKey(String key) {
		Cursor cursor = mContext.getContentResolver().query(
				CacheContentProvider.CONTENT_URI, null,
				" " + CacheSqliteHelper.TABLE_CACHE_FIELD_KEY + " = ? ",
				new String[] { key }, null);
		String response = null;
		while (cursor != null && cursor.moveToNext()) {
			response = cursor.getString(cursor
					.getColumnIndex(CacheSqliteHelper.TABLE_CACHE_FIELD_VALUE));
		}
		if (cursor != null) {
			cursor.close();
		}
		return response;
	}

	/**
	 * 此键存在的个数,可用于判断此键是否已经存在等;
	 * 
	 * @param key
	 * @return
	 */
	public int existCount(String key) {
		int count = 0;
		Cursor cursor = mContext.getContentResolver().query(
				CacheContentProvider.CONTENT_URI, null,
				" " + CacheSqliteHelper.TABLE_CACHE_FIELD_KEY + " = ? ",
				new String[] { key }, null);
		while (cursor != null && cursor.moveToNext()) {
			count++;
		}
		if (cursor != null) {
			cursor.close();
		}
		return count;
	}

}
