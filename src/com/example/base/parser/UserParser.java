package com.example.base.parser;

import org.json.JSONException;
import org.json.JSONObject;

import com.example.base.type.IType;
import com.example.base.type.User;

/**
 * 用户信息解析器
 *
 */
public class UserParser extends BaseParser{

    public User parseUser(JSONObject jsonObject) throws JSONException {
    	User user = new User();
        if(jsonObject.has("data")){
        	JSONObject dataJson = jsonObject.getJSONObject("data");
        	user.setId(parserNullString(dataJson,"id"));
        	user.setUsername(parserNullString(dataJson,"username"));
        	user.setNick(parserNullString(dataJson,"nick"));
        	user.setAge(parserNullString(dataJson,"age"));
        	user.setSex(parserNullString(dataJson,"sex"));
        	user.setSignature(parserNullString(dataJson,"signature"));
        	user.setAvatarUrl(parserNullString(dataJson,"avatarUrl"));
        	user.setUserType(parserNullString(dataJson,"userType"));
        	user.setUserStatus(parserNullString(dataJson,"userStatus"));
        	user.setBelongComm(parserNullString(dataJson,"belongComm"));
        	user.setCommName(parserNullString(dataJson,"commName"));
        	user.setCommTel(parserNullString(dataJson,"commTel"));
        	user.setBelongIndustry(parserNullString(dataJson,"belongIndustry"));
        	user.setIndustryDesc(parserNullString(dataJson,"industryDesc"));
        	user.setCommDesc(parserNullString(dataJson,"commDesc"));
        	user.setCommPic(parserNullString(dataJson,"commPic"));
        	user.setBelongTearcher(parserNullString(dataJson,"belongTearcher"));
        	user.setRegTimer(parserNullString(dataJson,"regTimer"));
        	user.setMobile(parserNullString(dataJson,"mobile"));
        }
        
        return user;
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
    
	public IType parseItype(String jsonString) {
		User user = null;
		try {
				JSONObject jsonObject = new JSONObject(jsonString);
				user = parseUser(jsonObject);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return user;
	}

}
