package com.example.base.utils;

import android.app.Activity;
import android.content.Context;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Display;

import com.example.base.BaseApplication;

public class UiUtils {

	/**
	 * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
	 * @param context
	 * @param dpValue
	 * @return
	 */
	public static int dp2px(Context context, float dpValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (dpValue * scale + 0.5f);
	}

	/** 
	 * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
	 * @param context
	 * @param pxValue
	 * @return
	 */
	public static int px2dp(Context context, float pxValue) {
		final float scale = context.getResources().getDisplayMetrics().density;
		return (int) (pxValue / scale + 0.5f);
	}
	
	/** 
	 * 根据手机的分辨率从 sp 的单位 转成为 px
	 * @param context
	 * @param spValue
	 * @return
	 */
	public static float sp2px(Context context, int spValue) {
        return TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_SP, spValue, context.getResources().getDisplayMetrics());
    }

	/**
	 * 获取屏幕宽度
	 * @return
	 */
	public static int getScreenWidth() {
        Activity activity = BaseApplication.getInstance().getActivity();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            return metrics.widthPixels;
        }
        return 0;
    }

	/**
	 * 获取屏幕高度
	 * @return
	 */
    public static int getScreenHeight() {
        Activity activity = BaseApplication.getInstance().getActivity();
        if (activity != null) {
            Display display = activity.getWindowManager().getDefaultDisplay();
            DisplayMetrics metrics = new DisplayMetrics();
            display.getMetrics(metrics);
            return metrics.heightPixels;
        }
        return 0;
    }
	
}
