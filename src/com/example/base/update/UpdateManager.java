package com.example.base.update;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import org.json.JSONObject;

import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.net.ParseException;
import android.os.Handler;
import android.view.WindowManager;
import android.widget.Toast;

import com.example.base.R;
import com.example.base.config.BaseConfig;
import com.example.base.type.ApkInfo;
import com.example.base.utils.NetworkUtil;
import com.example.base.utils.Utils;

/**
 * app更新下载管理
 * 
 * @date： 2012-12-18
 */
public class UpdateManager {

	private Context mContext;
	private static UpdateManager instance;
	private ApkInfo apkInfo;
	private AlertDialog noticeDialog; // 提示弹出框
	private ProgressDialog progressDialog; //手动检查等待框

	private boolean isDownloading = false;
	private boolean mIsAccord = false;; // 是否主动检查软件升级（主动检查会给出提示）
	private Handler checkHandler;
	
	private UpdateManager() {
	}
	
	public static UpdateManager getInstance(){
		if (null == instance) {
			instance = new UpdateManager();
		}
		return instance;
	}

	public void setDownloadState(boolean bl){
		isDownloading = bl;
	}
	
	public boolean isDownloadState(){
		return isDownloading;
	}
	
	/**
	 * 检查下载更新
	 * 
	 */
	public void checkUpdate(Context context, boolean isAccord) {
		if (isDownloading) {
			Toast.makeText(mContext, "正在下载更新。", Toast.LENGTH_LONG).show();
		}
		this.mContext = context;
		this.mIsAccord = isAccord;
		//自动检查更新
		if (isAccord)
			progressDialog = ProgressDialog.show(mContext, "", "请稍后，正在检查更新...");
		checkHandler = new CheckHandler();
		new Thread() {
			@Override
			public void run() {
				if (!NetworkUtil.isConnect(mContext)) { // 检查网络连接是否正常
					checkHandler.sendEmptyMessage(CheckHandler.CHECK_NETFAIL);
				} else if (checkTodayUpdate() || mIsAccord) {// 判断今天是否已自动检查过更新 ；如果手动检查更新，直接进入
//					Result result;
					try {
//						result = (Result) BaseHttpRequest.get(mContext, BaseClientApi.REQ_USER_LOGIN, null, null);
//						apkinfo = (ApkInfo) result.mType;
						String result = "{'apkVersion':'1.2','apkVerCode':'11','apkSize':'7.8MB','apkName':'androidupdatecode.apk','apkDownloadUrl':'http://www.oschina.net/uploads/oschina-1.7.6.3.apk','apklog':'更in提升机'}";
						JSONObject obj = new JSONObject(result);
						String apkVersion = obj.getString("apkVersion");
						int apkCode = obj.getInt("apkVerCode");
						String apkSize = obj.getString("apkSize");
						String apkName = obj.getString("apkName");
						String downloadUrl = obj.getString("apkDownloadUrl");
						String apkLog = obj.getString("apklog");
						apkInfo = new ApkInfo(downloadUrl, apkVersion, apkSize, apkCode, apkName, apkLog,false);
						
						if (apkInfo != null && checkApkVercode()) { // 检查版本号
							alreayCheckTodayUpdate(); // 设置今天已经检查过更新
							checkHandler.sendEmptyMessage(CheckHandler.CHECK_SUCCESS);
						} else {
							checkHandler.sendEmptyMessage(CheckHandler.CHECK_NOUPGRADE);
						}
					} catch (ParseException e1) {
						e1.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
						checkHandler.sendEmptyMessage(CheckHandler.CHECK_FAIL);
					}
				}
			}
		}.start();
	}

	/** 
	 * 弹出软件更新提示对话框
	 */
	private void showNoticeDialog() {
		StringBuffer sb = new StringBuffer();
		sb.append("版本号：" + apkInfo.getApkVersion() + "\n");
		sb.append("文件大小：" + apkInfo.getApkSize() + "\n");
		sb.append("更新日志：\n" + apkInfo.getApkLog());
		Builder builder = new AlertDialog.Builder(mContext);
		builder.setIcon(R.drawable.ic_launcher).setTitle("版本更新").setMessage(sb.toString());
		builder.setPositiveButton("立即更新",
			new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					String apkPath = BaseConfig.apkSavepath + apkInfo.getApkName();
					UpdateCallback downCallback = new UpdateInstall(mContext, apkPath, apkInfo.getApkVersion(), apkInfo.getApkCode(), apkInfo.isForceUpdate());
					UpdateAsyncTask request = new UpdateAsyncTask(downCallback);
					request.execute(apkInfo.getDownloadUrl(), apkPath);
					dialog.dismiss();
				}
			});
		if (!apkInfo.isForceUpdate()) {
			builder.setNegativeButton("以后再说",
					new DialogInterface.OnClickListener() {
				@Override
				public void onClick(DialogInterface dialog, int which) {
					dialog.dismiss();
				}
			});
		}
		noticeDialog = builder.create();
		//是否是强制更新
		if (apkInfo.isForceUpdate()) 
			noticeDialog.setCancelable(false);
		noticeDialog.getWindow().setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT); // 设置最顶层Alertdialog
		noticeDialog.show();
	}

	/**
	 * 根据日期检查是否需要进行软件升级
	 * 
	 * @throws Exception
	 */
	private boolean checkTodayUpdate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		SharedPreferences sharedPreference = mContext.getSharedPreferences(UpdateShared.SETTING_UPDATE_APK_INFO, 0);
		String checkDate = sharedPreference.getString(UpdateShared.CHECK_DATE, "");
		String updateDate = sharedPreference.getString(UpdateShared.UPDATE_DATE, "");
		if ("".equals(checkDate) && "".equals(updateDate)) { // 刚安装的新版本，设置详细信息
			int verCode = Utils.getCurrentVersionCode(mContext);
			String versionName = Utils.getCurrentVersionName(mContext);
			String dateStr = sdf.format(new Date());
			sharedPreference.edit().putString(UpdateShared.CHECK_DATE, dateStr)
					.putString(UpdateShared.UPDATE_DATE, dateStr)
					.putString(UpdateShared.APK_VERSION, versionName)
					.putInt(UpdateShared.APK_VERCODE, verCode).commit();
			return true;
		}
		try {
			// 判断defaultMinUpdateDay天内不检查升级
			if ((new Date().getTime() - sdf.parse(updateDate).getTime()) / 1000 / 3600 / 24 < BaseConfig.defaultMinUpdateDay) {
				return false;
			} else if (checkDate.equalsIgnoreCase(sdf.format(new Date()))) {// 判断今天是否检查过升级
				return false;
			}
		} catch (Exception e) {
			e.printStackTrace();
			return false;
		}
		return true;
	}

	/**
	 * 设置今天已经检查过升级
	 * 
	 * @return
	 */
	private void alreayCheckTodayUpdate() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
		String date = sdf.format(new Date());
		SharedPreferences sharedPreference = mContext.getSharedPreferences(UpdateShared.SETTING_UPDATE_APK_INFO, 0);
		sharedPreference.edit().putString(UpdateShared.CHECK_DATE, date).commit();
	}

	/**
	 * 检查版本是否需要更新
	 * 
	 * @return
	 */
	private boolean checkApkVercode() {
		if (apkInfo.getApkCode() > Utils.getCurrentVersionCode(mContext)) {
			return true;
		} else {
			return false;
		}
	}

	/**
	 * 更新检查提示
	 */
	private class CheckHandler extends Handler {
		
		final static int CHECK_FAIL = 0;
		final static int CHECK_SUCCESS = 1;
		final static int CHECK_NOUPGRADE = 2;
		final static int CHECK_NETFAIL = 3;
		
		public void handleMessage(android.os.Message msg) {
			if (progressDialog != null) {
				progressDialog.dismiss();
			}
			switch (msg.what) {
				case CHECK_SUCCESS: {
					showNoticeDialog();
					break;
				}
				case CHECK_NOUPGRADE: { // 不需要更新
					if (mIsAccord)
						Toast.makeText(mContext, "当前版本是最新版。", Toast.LENGTH_LONG).show();
					break;
				}
				case CHECK_NETFAIL: {
					if (mIsAccord)
						Toast.makeText(mContext, "网络连接不正常。", Toast.LENGTH_LONG).show();
					break;
				}
				case CHECK_FAIL: {
					if (mIsAccord)
						Toast.makeText(mContext, "从服务器获取更新数据失败。",Toast.LENGTH_LONG).show();
					break;
				}
			}
		};
	};
	
	static interface UpdateShared {
		String SETTING_UPDATE_APK_INFO = "cbt_upgrade_setting";
		String UPDATE_DATE = "updatedate";
		String APK_VERSION = "apkversion";
		String APK_VERCODE = "apkvercode";
		String CHECK_DATE = "checkdate";
	}
}
