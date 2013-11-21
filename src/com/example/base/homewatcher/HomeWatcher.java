package com.example.base.homewatcher;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.example.base.recevier.HomeWatcherRecevier;

/**
 * Home键监听封装
 * 
 */
public class HomeWatcher {

	private Context mContext;
	private IntentFilter mFilter;
	private OnHomePressedListener mListener;
	private HomeWatcherRecevier mRecevier;

	// 回调接口
	public interface OnHomePressedListener {
		public void onHomePressed();

		public void onHomeLongPressed();
	}

	public HomeWatcher(Context context) {
		mContext = context;
		mFilter = new IntentFilter(Intent.ACTION_CLOSE_SYSTEM_DIALOGS);
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 */
	public void setOnHomePressedListener(OnHomePressedListener listener) {
		mListener = listener;
		mRecevier = new HomeWatcherRecevier();
	}

	/**
	 * 开始监听，注册广播
	 */
	public void startWatch() {
		if (mRecevier != null) {
			mContext.registerReceiver(mRecevier, mFilter);
		}
	}

	/**
	 * 停止监听，注销广播
	 */
	public void stopWatch() {
		if (mRecevier != null) {
			mContext.unregisterReceiver(mRecevier);
		}
	}
	
	/**
	 * home键按下
	 */
	public void homePressed(){
		mListener.onHomePressed();
	}

	/**
	 * home键长按
	 */
	public void homeLongPressed(){
		mListener.onHomeLongPressed();
	}
}
