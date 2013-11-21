package com.example.base.db.cache;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;

public class CacheContentProvider extends ContentProvider {

	public static final int ALL_CACHE = 1;
	public static final int SINGLE_CACHE = 2;
	
	public static final Uri CONTENT_URI = Uri.parse("content://com.polyvi.cupmp.cache");
	public static final Uri CONTENT_URI_ITEM = Uri.parse("content://com.polyvi.cupmp.cache/#");

	public static final String TEMPLATES_TYPE = "vnd.android.cursor.dir/cache";
	public static final String TEMPLATES_ITEM_TYPE = "vnd.android.cursor.item/cache";
	
	public static final String AUTHORITY = "com.polyvi.cupmp.cache";
	
	private CacheSqliteHelper mDbHelper;

	static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
	static {
		matcher.addURI(AUTHORITY, null, ALL_CACHE);
		matcher.addURI(AUTHORITY, "/#", SINGLE_CACHE);
	}

	@Override
	public String getType(Uri uri) {
		int match = matcher.match(uri);
		switch (match) {
		case ALL_CACHE:
			return TEMPLATES_TYPE;
		case SINGLE_CACHE:
			return TEMPLATES_ITEM_TYPE;
		}
		return null;
	}

	@Override
	public Uri insert(Uri uri, ContentValues values) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		long rowId = 0;
		switch (matcher.match(uri)) {
		case ALL_CACHE:
			rowId = db.insert(CacheSqliteHelper.TABLE_CACHE, "", values);
		}
		if (rowId > 0) {
			Uri returnUri = Uri.parse("content://" + AUTHORITY + "/" + rowId);
			return returnUri;
		} else {
			return null;
		}
	}

	@Override
	public boolean onCreate() {
		mDbHelper = new CacheSqliteHelper(this.getContext());
		if (mDbHelper == null){
			return false;
		} else {
			return true;
		}
	}

	@Override
	public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
		SQLiteQueryBuilder builder = new SQLiteQueryBuilder();
		builder.setTables(CacheSqliteHelper.TABLE_CACHE);
		Cursor result = null;
		int match = matcher.match(uri);

		switch (match) {
		case ALL_CACHE:
			result = builder.query(mDbHelper.getReadableDatabase(), projection, selection, selectionArgs, null, null, sortOrder);
			break;
		}
		if (result != null){
			result.setNotificationUri(getContext().getContentResolver(), uri);
		}
		return result;
	}

	@Override
	public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowId = 0;
		rowId = db.update(CacheSqliteHelper.TABLE_CACHE, values, selection, selectionArgs);
		return rowId;
	}

	@Override
	public int delete(Uri uri, String selection, String[] selectionArgs) {
		SQLiteDatabase db = mDbHelper.getWritableDatabase();
		int rowId = 0;
		rowId = db.delete(CacheSqliteHelper.TABLE_CACHE, selection, selectionArgs);
		return rowId;
	}

}
