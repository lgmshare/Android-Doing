package com.example.base.ui;

import java.security.NoSuchAlgorithmException;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.base.R;
import com.example.base.homewatcher.HomeWatcher;
import com.example.base.http.AppHttpClient;
import com.example.base.http.AppHttpRequest;
import com.example.base.http.HttpClientApi;
import com.example.base.http.RequestCallBack;
import com.example.base.http.RequestParams;
import com.example.base.http.ResponseInfo;
import com.example.base.parser.UserParser;
import com.example.base.security.SecurityManager;
import com.example.base.type.User;
import com.example.base.update.UpdateManager;
import com.example.base.utils.DateUtil;
import com.example.base.utils.EmailUtil;
import com.example.base.widget.LoadingDialog;

public class MainActivity extends BaseActivity implements OnClickListener,
		HomeWatcher.OnHomePressedListener {

	private HomeWatcher homeWatcher;
	private ProgressBar mProgressBar;

	private static final int MESSAGETYPE_01 = 0x0001;
	private TextView mProgressText;
	private ProgressDialog progressDialog;

	private Handler myHandler = null;
	Dialog dialog;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		showTips(R.drawable.tips_warning, "dddd");
		findViewById(R.id.button1).setOnClickListener(this);
		findViewById(R.id.button2).setOnClickListener(this);
		findViewById(R.id.button3).setOnClickListener(this);
		EditText et_search = (EditText) findViewById(R.id.et_search);
		et_search.setOnKeyListener(new OnKeyListener() {// 输入完后按键盘上的搜索键【回车键改为了搜索键】

					public boolean onKey(View v, int keyCode, KeyEvent event) {
						((InputMethodManager) getSystemService(INPUT_METHOD_SERVICE))
								.hideSoftInputFromWindow(MainActivity.this
										.getCurrentFocus().getWindowToken(),
										InputMethodManager.HIDE_NOT_ALWAYS);
						return false;
					}
				});
		setHeader("holle world", this);
		homeWatcher = new HomeWatcher(this);
		homeWatcher.setOnHomePressedListener(this);
		homeWatcher.startWatch();

		mProgressText = (TextView) findViewById(R.id.progressText);
		mProgressBar = (ProgressBar) findViewById(R.id.progressBar);
		mProgressBar.setMax(100);
		mProgressBar.setProgress(0);
		HandlerThread handlerThread = new HandlerThread("myHandlerThread");
		handlerThread.start();
		myHandler = new Handler(handlerThread.getLooper());
		// myHandler.post(new MyRunnable());
		myHandler.post(ra);
	}

	@Override
	protected void onDestroy() {
		homeWatcher.stopWatch();
		super.onDestroy();
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_back:
			MainActivity.this.finish();
			break;
		case R.id.button1:
			showTips(R.drawable.tips_warning,
					DateUtil.friendlyTime("2013-10-31 14:55:12"));
			break;
		case R.id.button2:
//			dialog = new LoadingDialog(this, "加载中，请稍后...");
//			dialog.show();
			
			AppHttpClient http = new AppHttpClient();
			RequestParams pa = new RequestParams();
			pa.put("username", "liming");
			try {
				pa.put("password", SecurityManager.encryMD5("123456"));
				pa.put("keycode", SecurityManager.encryMD5("yj123456"));
			} catch (NoSuchAlgorithmException e) {
				e.printStackTrace();
			}
			
//			 http.send(AppHttpRequest.HttpMethod.POST,  "http://211.149.152.140:9080/yuanjuweb/api/member/memberLoginApi",pa,new UserParser(),new RequestCallBack<User>(){
//					@Override
//					public void onStart() {
//						Log.i("22", "onStart");
//					}
//			        @Override
//			        public void onLoading(long total, long current, boolean isUploading) {
//			        	super.onLoading(total, current, isUploading);
//			        	Log.i("22", "onLoading");
//			        	Log.i("22", "total" + total);
//			        	Log.i("22", "current" + current);
//			        	Log.i("22", "isUploading" + isUploading);
//			        }
//
//			        @Override
//			        public void onSuccess(ResponseInfo<User> responseInfo) {
//			        	Log.i("22", "onSuccess" + responseInfo.result.toString());
//			        	User user = responseInfo.result;
//			        	Log.i("", user.getUsername());
//			        }
//
//			        @Override
//			        public void onFailure(Throwable error, String code,String msg) {
//			        	Log.i("22", "onFailure:" + error + "---code:" + code +"---msg:" + msg);
//			        	
//			        }
//
//			        @Override
//					public void onFinish() {
//						super.onFinish();
//						Log.i("22", "onFinish");
//					}
//			        
//			});
//			
			
			HttpClientApi.post(HttpClientApi.REQ_USER_LOGIN, pa, new UserParser(), new RequestCallBack<User>() {

				public void onStart() {
					Log.i("22", "onStart");
				}
		        @Override
		        public void onLoading(long total, long current, boolean isUploading) {
		        	super.onLoading(total, current, isUploading);
		        	Log.i("22", "onLoading");
		        	Log.i("22", "total" + total);
		        	Log.i("22", "current" + current);
		        	Log.i("22", "isUploading" + isUploading);
		        }

		        @Override
		        public void onSuccess(ResponseInfo<User> responseInfo) {
		        	Log.i("22", "onSuccess" + responseInfo.result.toString());
		        	User user = responseInfo.result;
		        	Log.i("", user.getUsername());
		        }

		        @Override
		        public void onFailure(Throwable error, String code,String msg) {
		        	Log.i("22", "onFailure:" + error + "---code:" + code +"---msg:" + msg);
		        	
		        }

		        @Override
				public void onFinish() {
					super.onFinish();
					Log.i("22", "onFinish");
				}
			});
			
			break;
		case R.id.button3:
			EmailUtil.sendMail(MainActivity.this, 200,
					new String[] { "ffffff@11.com" }, "标题要长", "dfffffffff");
			break;
		case R.id.btnStart:
			mProgressBar.setProgress(0);

			handler.post(ra);
			break;
		case R.id.btnStop:
			handler.removeCallbacks(ra);
			break;
		default:
			break;
		}
	}

	private Runnable ra = new Runnable() {
		@Override
		public void run() {
			if (null != progressDialog) {
				progressDialog.dismiss();
			}
			int pro = mProgressBar.getProgress() + 1;
			mProgressBar.setProgress(pro);
			mProgressText.setText("下载进度：" + String.valueOf(pro) + "%");
			if (pro < 100) {
				// 如果进度小于100,，则延迟500毫秒后重复执行
				handler.postDelayed(ra, 100);
			}
		}
	};

	private class MyRunnable implements Runnable {

		@Override
		public void run() {
			System.out.println("Runnable---The Thread is running");
			System.out.println("Runnable---The Thread id is ："
					+ Thread.currentThread().getId());
			try {
				Thread.sleep(6000);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
	}

	private Handler handler = new Handler() {
		public void handleMessage(Message message) {
			switch (message.what) {
			case MESSAGETYPE_01:
				// 比较耗时的工作
				progressDialog.dismiss();
				break;
			}
			super.handleMessage(message);
		}
	};

	@Override
	public void onPause() {
		super.onPause();
		handler.removeCallbacks(ra);
	}

	@Override
	public void onHomePressed() {

	}

	@Override
	public void onHomeLongPressed() {

	}

	/**
	 * 创建menu TODO 停用原生菜单
	 */
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.activity_main, menu);
		return true;
	}

	/**
	 * 菜单被显示之前的事件
	 */
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		int item_id = item.getItemId();
		switch (item_id) {
		case R.id.main_menu_about:
			/* 升级程序[主动] */
			UpdateManager downManger = UpdateManager.getInstance();
			downManger.checkUpdate(MainActivity.this, true);
			break;
		case R.id.main_menu_exit:

			new AlertDialog.Builder(this)
					/* 弹出窗口的最上头文字 */
					.setTitle("消息提示")
					.setIcon(R.drawable.icon)
					.setMessage("确定要退出吗？")
					.setPositiveButton("确定",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									// ActivityManagerUtil.getInstance().exit();
								}
							})
					.setNegativeButton("取消",
							new DialogInterface.OnClickListener() {
								public void onClick(
										DialogInterface dialoginterface, int i) {
									dialoginterface.dismiss();
								}
							}).create().show();

			break;
		}
		return true;
	}
}
