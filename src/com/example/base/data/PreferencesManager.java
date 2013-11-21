package com.example.base.data;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * 存储管理类
 */
public class PreferencesManager {
	
	private Context context = null;
	private static SharedPreferences mPreferences;
	private static Editor mEditor;

	public PreferencesManager(Context context) {
		this.context = context;
		init();
	}

	/**
	 * 数据存储初始化
	 */
	public void init() {
		mPreferences = this.context.getSharedPreferences(PreferencesConstants.PREFERENCES_FILE, Context.MODE_PRIVATE);
		mEditor = mPreferences.edit();
	}

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            待保存数据名
	 * @param value
	 *            待保存数据值
	 */
	public void putBoolean(String key, boolean value) {
		mEditor.putBoolean(key, value);
		mEditor.commit();
	}

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            待保存数据名
	 * @param value
	 *            待保存数据值
	 */
	public void putFloat(String key, float value) {
		mEditor.putFloat(key, value);
		mEditor.commit();
	}

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            待保存数据名
	 * @param value
	 *            待保存数据值
	 */
	public void putInt(String key, int value) {
		mEditor.putInt(key, value);
		mEditor.commit();
	}

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            待保存数据名
	 * @param value
	 *            待保存数据值
	 */
	public void putLong(String key, long value) {
		mEditor.putLong(key, value);
		mEditor.commit();
	}

	/**
	 * 保存数据
	 * 
	 * @param key
	 *            待保存数据名
	 * @param value
	 *            待保存数据值
	 */
	public void putString(String key, String value) {
		mEditor.putString(key, value);
		mEditor.commit();
	}

	/**
	 * 删除数据名为key的数据
	 * 
	 * @param key
	 *            数据名
	 */
	public void remove(String key) {
		mEditor.remove(key);
		mEditor.commit();
	}

	/**
	 * 是否保存了数据名为key的数据
	 * 
	 * @param key
	 *            数据名
	 * @return true:有相应数据，否则false
	 */
	public boolean contains(String key) {
		return mPreferences.contains(key);
	}

	/**
	 * 获取数据名为key的数值
	 * 
	 * @param key
	 *            数据名
	 * @param defValue
	 *            如果数据key不存在返回的值
	 * @return 如果数据名key存在则返回key所对应值，如果不存在则返回defValue
	 */
	public boolean getBoolean(String key, boolean defValue) {
		return mPreferences.getBoolean(key, defValue);
	}

	/**
	 * 获取数据名为key的数值
	 * 
	 * @param key
	 *            数据名
	 * @param defValue
	 *            如果数据key不存在返回的值
	 * @return 如果数据名key存在则返回key所对应值，如果不存在则返回defValue
	 */
	public float getFloat(String key, float defValue) {
		return mPreferences.getFloat(key, defValue);
	}

	/**
	 * 获取数据名为key的数值
	 * 
	 * @param key
	 *            数据名
	 * @param defValue
	 *            如果数据key不存在返回的值
	 * @return 如果数据名key存在则返回key所对应值，如果不存在则返回defValue
	 */
	public int getInt(String key, int defValue) {
		return mPreferences.getInt(key, defValue);
	}

	/**
	 * 获取数据名为key的数值
	 * 
	 * @param key
	 *            数据名
	 * @param defValue
	 *            如果数据key不存在返回的值
	 * @return 如果数据名key存在则返回key所对应值，如果不存在则返回defValue
	 */
	public long getLong(String key, long defValue) {
		return mPreferences.getLong(key, defValue);
	}

	/**
	 * 获取数据名为key的数值
	 * 
	 * @param key
	 *            数据名
	 * @param defValue
	 *            如果数据key不存在返回的值
	 * @return 如果数据名key存在则返回key所对应值，如果不存在则返回defValue
	 */
	public String getString(String key, String defValue) {
		return mPreferences.getString(key, defValue);
	}
	
	/**
	 * 删除
	 */
	public void clearData() {
		mEditor.clear();
		mEditor.commit();
	}
}
