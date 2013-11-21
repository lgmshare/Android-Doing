package com.example.base.utils;

import java.io.File;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager.NameNotFoundException;
import android.net.Uri;
import android.os.Environment;
import android.widget.Toast;

public class Utils {

	/**
	 * 安装apk文件
	 * 
	 * @param file
	 * @param context
	 */
	public static void installApk(Context context, String apkPath) {
		File file = new File(apkPath);
		if(!file.exists()){
			Toast.makeText(context, "未找不到安装文件", Toast.LENGTH_LONG);
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.fromFile(file);
		intent.setDataAndType(data, "application/vnd.android.package-archive");
		context.startActivity(intent);
	}

	/**
	 * 卸载apk文件
	 * 
	 * @param packageName
	 * @param context
	 */
	public static void uninstallApk(Context context, String packageName) {
		Intent intent = new Intent(Intent.ACTION_VIEW);
		Uri data = Uri.parse("package:" + packageName);
		intent.setData(data);
		context.startActivity(intent);
	}

	/**
	 * 获取本地ip地址
	 * 
	 * @return
	 */
	public String getLocalIpAddress() {
		try {
			Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
			while (en.hasMoreElements()) {
				NetworkInterface intf = en.nextElement();
				Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses();
				while (enumIpAddr.hasMoreElements()) {
					InetAddress inetAddress = enumIpAddr.nextElement();
					if (!inetAddress.isLoopbackAddress()) {
						return inetAddress.getHostAddress().toString();
					}
				}
			}
		} catch (SocketException ex) {
			ex.printStackTrace();
		}
		return null;
	}

	/**
	 * 是否挂载了SD卡
	 * 
	 * @return
	 */
	public static boolean isHaveExternalStorage() {
		if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
			return true;
		} else {
			return false;
		}
	}
	
	/**
	 * 获取当前版本标示号
	 * @param mContext
	 * @return
	 */
	public static int getCurrentVersionCode(Context mContext){
		try {
			return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionCode;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return 0;
		}
	}
	/**
	 * 获取当前版本号
	 * @param mContext
	 * @return
	 */
	public static String getCurrentVersionName(Context mContext){
		try {
			return mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	/**
	 * 判断是不是一个合法的电子邮件地址
	 * @param email
	 * @return
	 */
	public static boolean isEmail(String email){
		Pattern emailer = Pattern.compile("\\w+([-+.]\\w+)*@\\w+([-.]\\w+)*\\.\\w+([-.]\\w+)*");
		if(email == null || email.trim().length()==0) 
			return false;
	    return emailer.matcher(email).matches();
	}

	/**
	 * 拨打电话
	 * 
	 * @param activity
	 * @param phone
	 */
	public static void callPhone(Activity activity, String phone) {
		Uri uri = Uri.parse("tel:" + phone);
		Intent intent = new Intent(Intent.ACTION_DIAL, uri);
		activity.startActivity(intent);
	}

	/**
	 * 发送短信
	 * 
	 * @param activity
	 * @param phone
	 * @param msg
	 */
	public static void sendMessage(Activity activity, String phone, String msg) {
		Uri uri = Uri.parse("smsto:" + phone);
		Intent intent = new Intent(Intent.ACTION_SENDTO, uri);
		intent.putExtra("sms_body", msg);
		activity.startActivity(intent);
	}
}
