package com.example.base.widget;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.TextView;

import com.example.base.R;

public class LoadingDialog extends ProgressDialog {

	private TextView content;
	private String message;

	public LoadingDialog(Context context) {
		super(context);
	}

	public LoadingDialog(Context context, String message) {
		super(context);
		this.message = message;
	}

	public LoadingDialog(Context context, int theme, String message) {
		super(context, theme);
		this.message = message;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.view_tips_loading);
		setCanceledOnTouchOutside(false);
		content = (TextView) findViewById(R.id.tips_msg);
		content.setText(this.message);
	}

	public void setText(String message) {
		content.setText(message);
	}

	public void setText(int resId) {
		setText(getContext().getResources().getString(resId));
	}
}