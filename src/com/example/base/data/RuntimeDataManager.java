package com.example.base.data;

import java.util.Hashtable;

/**
 * 存储上下文数据类（单例类）数据保存在内存中所以保存数据不宜过大
 * 
 */
public class RuntimeDataManager {

	public static final String EXAMPLE_FALG = "falg";

	private Hashtable<String, Object> mTable;

	public RuntimeDataManager() {
		mTable = new Hashtable<String, Object>();
	}

	/**
	 * 保存将键为key，键值为value数据，如果键值key已经存在则将替换原来值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param value
	 *            键值 不能为null
	 */
	public void put(String key, Object value) {
		mTable.put(key, value);
	}

	/**
	 * 获取键key所对应的键值
	 * 
	 * @param key
	 *            键，不能为null
	 * @return 键值，如果键key不存在则返回null
	 */
	public Object get(String key) {
		return mTable.get(key);
	}

	/**
	 * 保存将键为key，键值为value数据，如果键值key已经存在则将替换原来值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param value
	 *            键值 不能为null
	 */
	public void put(String key, String value) {
		mTable.put(key, value);
	}

	/**
	 * 获取键key所对应的键值
	 * 
	 * @param key
	 *            键，不能为null
	 * @return 键值，如果键key不存在则返回null
	 */
	public String getString(String key) {
		return (String) mTable.get(key);
	}

	/**
	 * 保存将键为key，键值为value数据，如果键值key已经存在则将替换原来值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param value
	 *            键值 不能为null
	 */
	public void put(String key, boolean value) {
		mTable.put(key, value);
	}

	/**
	 * 获取键key所对应的键值
	 * 
	 * @param key
	 *            键，不能为null
	 * @return 键值，如果键key不存在则返回null
	 */
	public boolean getBoolean(String key, boolean defaultValue) {
		Boolean ret = (Boolean) mTable.get(key);
		if (null == ret) {
			return defaultValue;
		} else {
			return ret.booleanValue();
		}
	}

	/**
	 * 保存将键为key，键值为value数据，如果键值key已经存在则将替换原来值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param value
	 *            键值 不能为null
	 */
	public void put(String key, double value) {
		mTable.put(key, value);
	}

	/**
	 * 获取键key所对应的键值
	 * 
	 * @param key
	 *            键，不能为null
	 * @return 键值，如果键key不存在则返回null
	 */
	public double getDouble(String key, double defaultValue) {
		Double ret = (Double) mTable.get(key);
		if (null == ret) {
			return defaultValue;
		} else {
			return ret.doubleValue();
		}
	}

	/**
	 * 保存将键为key，键值为value数据，如果键值key已经存在则将替换原来值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param value
	 *            键值 不能为null
	 */
	public void put(String key, int value) {
		mTable.put(key, value);
	}

	/**
	 * 获取键key所对应的键值
	 * 
	 * @param key
	 *            键，不能为null
	 * @return 键值，如果键key不存在则返回null
	 */
	public int getInt(String key, int defaultValue) {
		Integer ret = (Integer) mTable.get(key);
		if (null == ret) {
			return defaultValue;
		} else {
			return ret.intValue();
		}
	}

	/**
	 * 保存将键为key，键值为value数据，如果键值key已经存在则将替换原来值
	 * 
	 * @param key
	 *            键 不能为null
	 * @param value
	 *            键值 不能为null
	 */
	public void put(String key, long value) {
		mTable.put(key, value);
	}

	/**
	 * 获取键key所对应的键值
	 * 
	 * @param key
	 *            键，不能为null
	 * @return 键值，如果键key不存在则返回null
	 */
	public long getLong(String key, long defaultValue) {
		Long ret = (Long) mTable.get(key);
		if (null == ret) {
			return defaultValue;
		} else {
			return ret.longValue();
		}
	}

	public void clearDataByKey(String key){
		mTable.remove(key);
	}
	
	public void clearData() {
		mTable.clear();
	}
}
