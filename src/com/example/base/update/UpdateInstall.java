/**
 * Copyright(c)2012 Beijing PeaceMap Co. Ltd.
 * All right reserved. 
 */
package com.example.base.update;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.RemoteViews;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base.BaseApplication;
import com.example.base.R;
import com.example.base.config.BaseConfig;
import com.example.base.update.UpdateManager.UpdateShared;
import com.example.base.utils.Utils;

/**
 * 
 * @author lim
 *
 */
public class UpdateInstall implements UpdateCallback {

	private final int NOTIFICATION_ID = 8888;
	
	private Context mContext;
	private String apkPath;
	private String apkVersion;
	private int apkCode;
	
	private TextView textView;
	private ProgressBar progressView;
	private AlertDialog downloadDialog;    //下载弹出框
	private boolean isForceUpdate = false;  //是否强制更新
	private boolean interceptFlag = false;  //是否取消下载
	
	private NotificationManager notificationManager;
	private Notification notification;
	private boolean showNotification = false;
	
	public UpdateInstall(Context mContext,String apkPath,String apkVersion,int apkCode, boolean isForceUpdate) {
		this.mContext = mContext;
		this.apkCode = apkCode;
		this.apkPath = apkPath;
		this.apkVersion = apkVersion;
		this.isForceUpdate = isForceUpdate;
	}
	
	@Override
	public void onPreare() {
		UpdateManager.getInstance().setDownloadState(true);
		if(Utils.isHaveExternalStorage()){
			File file = new File(BaseConfig.apkSavepath);
			if(!file.exists()){
				file.mkdir();
			}
			Builder builder = new AlertDialog.Builder(mContext);
			builder.setIcon(R.drawable.ic_launcher).setTitle("正在下载新版本");
			//设置在对话框中显示进度条
			View view = LayoutInflater.from(mContext).inflate(R.layout.update_apk, null);
			textView = (TextView)view.findViewById(R.id.tv_progress);
			textView.setText("进度：0");
			progressView = (ProgressBar)view.findViewById(R.id.progressBar);
			builder.setView(view);
			
			if (!isForceUpdate) {
				builder.setNegativeButton("取消更新", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						interceptFlag = true; 
					}
				});
				builder.setPositiveButton("后台运行", new DialogInterface.OnClickListener(){
					@Override
					public void onClick(DialogInterface dialog, int which) {
						dialog.dismiss();
						showNotification = true;
					}
				});
			}
			downloadDialog = builder.create();
			if (isForceUpdate) {
				downloadDialog.setCancelable(false);
			}
			downloadDialog.show();
		}else{
			interceptFlag = true;
			Toast.makeText(mContext, "检测到手机没有存储卡,请安装了内存卡后再升级。", Toast.LENGTH_LONG).show();
		}
	}
	
	@Override
	public void onProgress(int progress) {
		//设置下载状态
		if (showNotification) {
			if (notification == null) {
				notificationManager = (NotificationManager) mContext.getSystemService(android.content.Context.NOTIFICATION_SERVICE);
				Intent notificationIntent = new Intent(mContext, this.getClass());
				notificationIntent.addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP);
				// add flag设置跳转类型
				PendingIntent contentIntent = PendingIntent.getActivity(mContext, 0, notificationIntent, 0);
				// 创建Notifcation对象，设置图标，提示文字
				long number = 100;
				notification = new Notification(R.drawable.ic_launcher,"DownLoadManager", number);
				notification.flags |= Notification.FLAG_ONGOING_EVENT;// 出现在 “正在运行的”栏目下面
				
				RemoteViews rv = new RemoteViews(BaseApplication.getInstance().getPackageName(), R.layout.update_notification);
				rv.setTextViewText(R.id.n_title, "准备下载");
				rv.setTextViewText(R.id.n_text, "进度：" + 0 + "% ");
				rv.setProgressBar(R.id.n_progress, 100, 0, false);
				notification.contentView = rv;
				notification.contentIntent = contentIntent;
			}else{
				if (progress >= 99) {
					notificationManager.cancel(NOTIFICATION_ID);
					return;
				}
				RemoteViews rv = notification.contentView;
				rv.setTextViewText(R.id.n_title, "TST");
				rv.setTextViewText(R.id.n_text, "下载进度：" + progress + "% ");
				rv.setProgressBar(R.id.n_progress, 100, progress, false);
				notification.contentView = rv;
				// 提交一个通知在状态栏中显示。如果拥有相同标签和相同id的通知已经被提交而且没有被移除，该方法会用更新的信息来替换之前的通知。
				notificationManager.notify(NOTIFICATION_ID, notification);
			}
		}else{
			progressView.setProgress(progress);   //设置下载进度
			textView.setText("进度："+progress+"%");
		}
	}

	@Override
	public boolean onCancel() {
		return interceptFlag;
	}
	
	@Override
	public void onCompleted(boolean success, String errorMsg) {
		UpdateManager.getInstance().setDownloadState(false);
		if(downloadDialog != null){
			downloadDialog.dismiss();
		}
		
		if (showNotification) {
			notificationManager.cancel(NOTIFICATION_ID);
		}
		
		if(success){  
			//更新成功
			alearyUpdateSuccess();
			installApk();
		}else{
			Toast.makeText(mContext, errorMsg, Toast.LENGTH_SHORT).show();
		}
	}
	
	/**
	 * 升级成功，更新升级日期和版本号，和版本code
	 */
	private void alearyUpdateSuccess(){
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd",Locale.getDefault());
		SharedPreferences sharedPreference = mContext.getSharedPreferences(UpdateShared.SETTING_UPDATE_APK_INFO, 0);
		sharedPreference.edit().putString(UpdateShared.UPDATE_DATE, sdf.format(new Date()))
		.putString(UpdateShared.APK_VERSION, apkVersion).putInt(UpdateShared.APK_VERCODE, apkCode).commit();
	}
	
	/**
	 * 安装apk
	 */
	private void installApk(){
		File file = new File(apkPath);
		if(!file.exists()){
			return;
		}
		Intent intent = new Intent(Intent.ACTION_VIEW);
		intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
		mContext.startActivity(intent);
	}
}
