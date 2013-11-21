package com.example.base.ui;

import android.app.Activity;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.base.BaseApplication;
import com.example.base.R;
import com.example.base.widget.TipsToast;

public class BaseActivity extends Activity{

	private Toast msgToast;
	private TipsToast tipsToast;
	
	@Override
	protected void onCreate(Bundle savedInstanceState){
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
	}
	
	@Override
	protected void onResume(){
		BaseApplication.getInstance().addActivity(this);
		super.onResume();
	}
	
	@Override
	protected void onPause(){
		super.onPause();
	}

	@Override
	protected void onDestroy() {
		BaseApplication.getInstance().removeActivity(this);
		super.onDestroy();
	}
	
	@Override
	public void onUserInteraction() {
		super.onUserInteraction();
	}
	
	public void setHeader(String title) {
		TextView header = (TextView) findViewById(R.id.tv_title);
		header.setText(title);
	}

	public void setHeader(String title, View.OnClickListener listener) {
		TextView header = (TextView) findViewById(R.id.tv_title);
		header.setText(title);
		ImageView back = (ImageView) findViewById(R.id.btn_back);
		back.setVisibility(View.VISIBLE);
		back.setOnClickListener(listener);
	}
	
	/**
	 * 系统toast提示
	 * @param msg
	 */
	public void showToastMsg(String msg) {
		if (msg != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				msgToast.cancel();
			}
		}else{
			msgToast = Toast.makeText(this, "", Toast.LENGTH_SHORT);
		}
		if (TextUtils.isEmpty(msg)) {
			msg = "";
		}
		msgToast.setText(msg);
		msgToast.show();
	}
	
	/**
	 * 自定义提示toast
	 * @param icon
	 * @param msg
	 */
	public void showTips(int icon, String msg) {
		if (tipsToast != null) {
			if (Build.VERSION.SDK_INT < Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
				tipsToast.cancel();
			}
		} else {
			tipsToast = TipsToast.makeText(getApplication().getBaseContext(), icon, TipsToast.LENGTH_SHORT);
		}
		if (TextUtils.isEmpty(msg)) {
			msg = "";
		}
		tipsToast.setIcon(icon);
		tipsToast.setText(msg);
		tipsToast.show();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		return super.onKeyDown(keyCode, event);
	}
}
