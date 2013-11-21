package com.example.base.type;

import java.io.Serializable;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * 登录用户
 * 
 * @author akerisshi
 * 
 */
public class User implements IType, Serializable, Parcelable {

	private static final long serialVersionUID = 1L;

	// 0无效用户、1正常用户、2未审核、3该账户或则密码不存在
	public static final String STATUS_INVALIDATE = "0";
	public static final String STATUS_NORMAL = "1";
	public static final String STATUS_VERIFY = "2";
	public static final String STATUS_INEXISTENCE = "3";
	// 用户类型（1普通用户，2俱乐部用户）
	public static final String TYPE_COMMON = "1";
	public static final String TYPE_CLUB = "2";
	public static final String TYPE_VIP = "3";
	
	private String id;
	private String username;
	private String nick;
	private String age;
	private String sex;
	private String signature;// 签名
	private String avatarUrl;// 头像
	private String userType;// 用户类型
	private String userStatus;// 用户状态
	private String belongComm;// 所属公司
	private String belongIndustry;// 所属行业
	private String belongTearcher;// 所属教练
	private String mobile;// 移动电话
	private String regTimer;// 注册时间
	private String commName;// 公司名称
	private String commTel;// 公司电话
	private String commDesc;// 公司简介
	private String industryDesc;//行业描述
	private String commPic;// 公司宣传图片

	public static final Parcelable.Creator<User> CREATOR = new Parcelable.Creator<User>() {
		@Override
		public User createFromParcel(Parcel source) {
			// 序列化user对象
			User user = new User();
			user.setId(source.readString());
			user.setUsername(source.readString());
			user.setNick(source.readString());
			user.setAge(source.readString());
			user.setSex(source.readString());
			user.setSignature(source.readString());
			user.setAvatarUrl(source.readString());
			user.setUserType(source.readString());
			user.setUserStatus(source.readString());
			user.setBelongComm(source.readString());
			user.setCommName(source.readString());
			user.setCommTel(source.readString());
			user.setBelongIndustry(source.readString());
			user.setIndustryDesc(source.readString());
			user.setCommDesc(source.readString());
			user.setCommPic(source.readString());
			user.setBelongTearcher(source.readString());
			user.setRegTimer(source.readString());
			return user;
		}

		@Override
		public User[] newArray(int size) {
			return new User[size];
		}
	};

	@Override
	public int describeContents() {
		return 0;
	}

	@Override
	public void writeToParcel(Parcel dest, int flags) {
		dest.writeString(id);
		dest.writeString(username);
		dest.writeString(nick);
		dest.writeString(age);
		dest.writeString(sex);
		dest.writeString(signature);
		dest.writeString(avatarUrl);
		dest.writeString(userType);
		dest.writeString(userStatus);
		dest.writeString(belongComm);
		dest.writeString(commName);
		dest.writeString(commTel);
		dest.writeString(belongIndustry);
		dest.writeString(industryDesc);
		dest.writeString(commDesc);
		dest.writeString(commPic);
		dest.writeString(belongTearcher);
		dest.writeString(regTimer);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getUsername() {
		return username;
	}

	public void setUsername(String username) {
		this.username = username;
	}

	public String getNick() {
		return nick;
	}

	public void setNick(String nick) {
		this.nick = nick;
	}

	public String getAge() {
		return age;
	}

	public void setAge(String age) {
		this.age = age;
	}

	public String getSex() {
		return sex;
	}

	public void setSex(String sex) {
		this.sex = sex;
	}

	public String getSignature() {
		return signature;
	}

	public void setSignature(String signature) {
		this.signature = signature;
	}

	public String getAvatarUrl() {
		return avatarUrl;
	}

	public void setAvatarUrl(String avatarUrl) {
		this.avatarUrl = avatarUrl;
	}

	public String getUserType() {
		return userType;
	}

	public void setUserType(String userType) {
		this.userType = userType;
	}

	public String getUserStatus() {
		return userStatus;
	}

	public void setUserStatus(String userStatus) {
		this.userStatus = userStatus;
	}

	public String getBelongComm() {
		return belongComm;
	}

	public void setBelongComm(String belongComm) {
		this.belongComm = belongComm;
	}

	public String getMobile() {
		return mobile;
	}

	public void setMobile(String mobile) {
		this.mobile = mobile;
	}

	public String getCommName() {
		return commName;
	}

	public void setCommName(String commName) {
		this.commName = commName;
	}

	public String getCommTel() {
		return commTel;
	}

	public void setCommTel(String commTel) {
		this.commTel = commTel;
	}

	public String getBelongIndustry() {
		return belongIndustry;
	}

	public void setBelongIndustry(String belongIndustry) {
		this.belongIndustry = belongIndustry;
	}

	public String getIndustryDesc() {
		return industryDesc;
	}

	public void setIndustryDesc(String industryDesc) {
		this.industryDesc = industryDesc;
	}

	public String getCommDesc() {
		return commDesc;
	}

	public void setCommDesc(String commDesc) {
		this.commDesc = commDesc;
	}

	public String getCommPic() {
		return commPic;
	}

	public void setCommPic(String commPic) {
		this.commPic = commPic;
	}

	public String getBelongTearcher() {
		return belongTearcher;
	}

	public void setBelongTearcher(String belongTearcher) {
		this.belongTearcher = belongTearcher;
	}

	public String getRegTimer() {
		return regTimer;
	}

	public void setRegTimer(String regTimer) {
		this.regTimer = regTimer;
	}

}
