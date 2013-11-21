package com.example.base;

import java.lang.Thread.UncaughtExceptionHandler;
import java.util.LinkedList;
import java.util.List;
import java.util.UUID;

import android.app.Activity;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.telephony.TelephonyManager;

import com.example.base.data.PreferencesConstants;
import com.example.base.data.PreferencesManager;
import com.example.base.data.RuntimeDataManager;
import com.example.base.db.cache.CacheManager;
import com.example.base.utils.ActivityUtil;
import com.example.base.utils.StringEncodeUtil;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

public class BaseApplication extends Application{

	private static BaseApplication instance;
	
	private List<Activity> activityList = new LinkedList<Activity>();
	
	// 数据库缓存管理
	private CacheManager mCacheManager;
	// preference数据存储管理
	private PreferencesManager mPreferenceManager;
	//
	private RuntimeDataManager mRuntimeDataManager;
	//app的上一次安装版本号
	private static int mAppLastVer;
	//app的当前安装版本号
	private static int mAppCurVer;
	//app的codeName
	private static String mAppCurVerName;
	
	public static BaseApplication getInstance(){
		return instance;
	}
	
	@Override
	public void onCreate(){
		super.onCreate();
		instance = this;
		mPreferenceManager = new PreferencesManager(this);
//		mRuntimeDataManager = new RuntimeDataManager();
//		mCacheManager = new CacheManager(this);
		initExceptionHandler();
		initPreferenceVsersionData();
		initImageLoader();
	}
	
	/**
	 * 覆盖系统异常处理
	 */
	private void initExceptionHandler(){
		Thread.setDefaultUncaughtExceptionHandler(new UncaughtExceptionHandler() {
			@Override
			public void uncaughtException(Thread thread, Throwable ex) {
				 ex.printStackTrace();
			     ActivityUtil.getInstance().exit();
			}
		});
	}
	
	/**
	 * 初始话图片加载模块
	 * @param context
	 */
	private void initImageLoader() {
		// This configuration tuning is custom. You can tune every option, you
		// may tune some of them,
		// or you can create default configuration by
		// ImageLoaderConfiguration.createDefault(this);
		// method.
		DisplayImageOptions options = new DisplayImageOptions.Builder()
										.showStubImage(R.drawable.global_defaultmain)
										.showImageForEmptyUri(R.drawable.global_defaultmain)
										.cacheInMemory().cacheOnDisc().build();
		ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(
				this).threadPriority(Thread.NORM_PRIORITY - 2)
				.denyCacheImageMultipleSizesInMemory()
				.discCacheFileNameGenerator(new Md5FileNameGenerator())
				.defaultDisplayImageOptions(options)
				.tasksProcessingOrder(QueueProcessingType.LIFO).enableLogging() // Not
																				// necessary
																				// in
																				// common
				.build();
		// Initialize ImageLoader with configuration.
		ImageLoader.getInstance().init(config);
	}
	
	private void initPreferenceVsersionData(){
		mAppLastVer = mPreferenceManager.getInt(PreferencesConstants.APP_LAST_VER, 0);
		try { 
			PackageInfo pkg = getPackageManager().getPackageInfo(getPackageName(), 0);
			mAppCurVer = pkg.versionCode;
			mAppCurVerName = pkg.versionName;
		} catch (NameNotFoundException e) {
			mAppCurVer = mAppLastVer;
			mAppCurVerName = "";
		}
	}
	
	/**
	 * 获取App唯一标识
	 * @return
	 */
	public String getAppUniqueID() {
		String uniqueID = mPreferenceManager.getString(PreferencesConstants.APP_UNIQUEID,"");
		if(StringEncodeUtil.isEmpty(uniqueID)){
			//获取设备ID
		    TelephonyManager tm = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
		    uniqueID = tm.getDeviceId();
             if (StringEncodeUtil.isEmpty(uniqueID)) {
            	 uniqueID = UUID.randomUUID().toString();
			}
             mPreferenceManager.putString(PreferencesConstants.APP_UNIQUEID, uniqueID);
		}
		return uniqueID;
	}
	
	/**
	 * 获取缓存对象
	 * @return
	 */
	public CacheManager getCacheManager() {
		return mCacheManager;
	}
	
	/**
	 * 获取运行时数据缓存
	 * @return
	 */
	public RuntimeDataManager getRuntimeDataManager() {
		return mRuntimeDataManager;
	}
	
	public PreferencesManager getPreferencesManager(){
		return mPreferenceManager;
	}

	/**
	 * 是否是新安装应用
	 * @return
	 */
	public boolean isNewInstall() {
		return mAppLastVer != mAppCurVer;
	}
	
	/**
	 * Retrieves application's version number from the manifest
	 * 
	 * @return
	 */
	public String getVersion(){
		return mAppCurVerName;
	}

	/**
	 * 更新安装版本信息
	 */
	public void updateNewVersion() {
		mPreferenceManager.putInt(PreferencesConstants.APP_LAST_VER, mAppCurVer);
	}
	
	public Activity getActivity(){
		return activityList.get(activityList.size() - 1);
	}
	
	/**
	 * add activity in list
	 */
	public void addActivity(Activity activity){
		activityList.add(activity);
	}

	/**
	 * remove activity in list
	 */
	public void removeActivity(Activity activity){
		activityList.remove(activity);
	}
	
	/**
	 * exit app
	 */
	public void exit(){
		for (Activity activity : activityList) {
			activity.finish();
		}
		System.exit(0);
	}
}
