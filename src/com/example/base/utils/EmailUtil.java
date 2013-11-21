package com.example.base.utils;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.text.Html;

public class EmailUtil {

	/**
	 * 发送邮件
	 * @param activity
	 * @param requestCode
	 * @param receiver
	 * @param title
	 * @param content
	 */
	public static void sendMail(Activity activity, int requestCode, String[] receiver, String title, String content){
		LogUtil.i("Sending emails to:" + receiver);
	    String addresses = "mailto: ";
	    for (int i = 0; i < receiver.length; i++) {
	    	addresses = addresses + receiver[i];
		}
	    Intent in = new Intent("android.intent.action.SENDTO", Uri.parse(addresses));
	    in.putExtra("android.intent.extra.SUBJECT", title);
	    in.putExtra("android.intent.extra.TEXT", Html.fromHtml(content));
	    activity.startActivityForResult(in, requestCode);
	}
}
