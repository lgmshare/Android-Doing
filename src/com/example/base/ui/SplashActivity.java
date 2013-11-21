package com.example.base.ui;

import android.os.Bundle;
import android.os.Handler;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ViewAnimator;

import com.example.base.BaseApplication;
import com.example.base.R;
import com.example.base.utils.ActivityUtil;
import com.example.base.widget.UserGuideView;

public class SplashActivity extends BaseActivity implements OnClickListener{

	private final int SPLASH_DISPLAY_LENGHT = 1 * 1000; // 延迟三秒
	
	private ViewAnimator mViewMan = null;
	//用户操作指引
	private View mUserGuideView;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.splash_activity);
		initViewManager();
		
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				if (BaseApplication.getInstance().isNewInstall()) {
					showUserGuide();
				}else{
					goHome();
				}
			}
		}, SPLASH_DISPLAY_LENGHT);
	}

	/**
	 * 初始化ViewAnimator
	 */
	private void initViewManager() {
		mViewMan = (ViewAnimator) findViewById(R.id.vm_startup);
		mViewMan.setInAnimation(SplashActivity.this, R.anim.fade_in);
		mViewMan.setOutAnimation(SplashActivity.this, R.anim.fade_out);
	}
	
	/**
	 * 初始化用户向导界面
	 */
	private void showUserGuide() {
		UserGuideView view = UserGuideView.createGuideView(SplashActivity.this);
		view.setOnClickListener(SplashActivity.this);
		mUserGuideView = view;
		mViewMan.addView(mUserGuideView);
		mViewMan.showNext();
		mViewMan.setInAnimation(SplashActivity.this, R.anim.in_from_right);
		mViewMan.setOutAnimation(SplashActivity.this, R.anim.out_to_left);
		BaseApplication.getInstance().updateNewVersion();
	}

	@Override
	public void onClick(View v) {
		goHome();
	}
	
	private void goHome(){
		ActivityUtil.next(SplashActivity.this, MainActivity.class,R.anim.fade_in, R.anim.fade_out, true);
	}
	
	@Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK && event.getRepeatCount() == 0) {
           return true;
        }
        return super.onKeyDown(keyCode, event);
    }
}
