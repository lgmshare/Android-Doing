package com.example.base.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.base.exception.ParseException;
import com.example.base.type.IType;

/**
 * 解析器基础类
 * 
 */
public abstract class BaseParser implements IParser<IType> {

	public abstract IType parseItype(String jsonString) throws ParseException;

	@Override
	public IType parse(String jsonString) throws ParseException {
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			boolean flag = jsonObject.optBoolean("flag");
			String msg = jsonObject.optString("msg");
			if (flag) {
				return parseItype(jsonString);
			} else {
				throw new ParseException(602,msg);
			}
		} catch (JSONException e) {
			throw new ParseException(601,"数据解析异常");
		}
	}

	public String parserNullString(JSONObject obj, String key) {
		String value = null;
		try {
			if (obj.get(key) == JSONObject.NULL || obj.isNull(key)) {
				value = "";
			} else {
				value = obj.getString(key);
			}
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return value;
	}

}
