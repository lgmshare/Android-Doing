package com.example.base.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.base.type.ApkInfo;
import com.example.base.type.IType;

public class ApkInfoParser extends BaseParser{

	public ApkInfo parseUser(JSONObject jsonObject) throws JSONException {
		ApkInfo apk = new ApkInfo();
        if(jsonObject.has("data")){
        	JSONObject  dataJson = jsonObject.getJSONObject("data");
        	apk.setApkName(dataJson.optString("id"));
        	String apkVersion = jsonObject.getString("apkVersion");
			int apkCode = jsonObject.getInt("apkVerCode");
			String apkSize = jsonObject.getString("apkSize");
			String apkName = jsonObject.getString("apkName");
			String downloadUrl = jsonObject.getString("apkDownloadUrl");
			String apkLog = jsonObject.getString("apklog");
			apk = new ApkInfo(downloadUrl, apkVersion, apkSize, apkCode, apkName, apkLog,true);
        }
        
        return apk;
    }

	@Override
	public IType parseItype(String jsonString) {
		ApkInfo apk = null;
		try {
			JSONObject jsonObject = new JSONObject(jsonString);
			apk = parseUser(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return apk;
	}
}
