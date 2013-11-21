package com.example.base.recevier;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.example.base.homewatcher.HomeWatcher;
import com.example.base.utils.LogUtil;

/**
 * home键监听广播
 */
public class HomeWatcherRecevier extends BroadcastReceiver{
	
	private final String SYSTEM_DIALOG_REASON_KEY = "reason";
	private final String SYSTEM_DIALOG_REASON_GLOBAL_ACTIONS = "globalactions";
	private final String SYSTEM_DIALOG_REASON_RECENT_APPS = "recentapps";
	private final String SYSTEM_DIALOG_REASON_HOME_KEY = "homekey";

	private HomeWatcher watcherManager;
	
	@Override
	public void onReceive(Context context, Intent intent) {
		String action = intent.getAction();
		if (action.equals(Intent.ACTION_CLOSE_SYSTEM_DIALOGS)) {
			String reason = intent.getStringExtra(SYSTEM_DIALOG_REASON_KEY);
			if (reason != null) {
				LogUtil.i("home键监听：action:" + action + ",reason:" + reason);
				if (watcherManager != null) {
					if (reason.equals(SYSTEM_DIALOG_REASON_HOME_KEY)) {
						// 短按home键
						watcherManager.homePressed();
					} else if (reason.equals(SYSTEM_DIALOG_REASON_RECENT_APPS)) {
						// 长按home键
						watcherManager.homeLongPressed();
					}
				}
			}
		}
	}
}
