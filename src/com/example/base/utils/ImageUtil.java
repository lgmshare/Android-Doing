package com.example.base.utils;

import java.io.InputStream;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.StateListDrawable;

public class ImageUtil {

	/**
	 * 以最省内存的方式读取本地资源的图片
	 * 
	 * @param context
	 * @param resId
	 * @return
	 */
	public static Bitmap readBitMap(Context context, int resId) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		InputStream is = context.getResources().openRawResource(resId);
		return BitmapFactory.decodeStream(is, null, opt);
	}

	/**
	 * 以最省内存的方式读取本地资源的图片 或者SDCard中的图片
	 * 
	 * @param imagePath
	 *             图片在SDCard中的路径
	 * @return
	 */
	public static Bitmap getSDCardImg(String imagePath) {
		BitmapFactory.Options opt = new BitmapFactory.Options();
		opt.inPreferredConfig = Bitmap.Config.RGB_565;
		opt.inPurgeable = true;
		opt.inInputShareable = true;
		// 获取资源图片
		return BitmapFactory.decodeFile(imagePath, opt);
	}
	
	/**
	 * 编辑图片大小，保持图片不变形。
	 * 
	 * @param sourceBitmap
	 * @param resetWidth
	 * @param resetHeight
	 * @return
	 */
	public static Bitmap resetImage(Bitmap sourceBitmap, int resetWidth,
			int resetHeight) {
		int width = sourceBitmap.getWidth();
		int height = sourceBitmap.getHeight();
		int tmpWidth;
		int tmpHeight;
		float scaleWidth = (float) resetWidth / (float) width;
		float scaleHeight = (float) resetHeight / (float) height;
		float maxTmpScale = scaleWidth >= scaleHeight ? scaleWidth : scaleHeight;
		// 保持不变形
		tmpWidth = (int) (maxTmpScale * width);
		tmpHeight = (int) (maxTmpScale * height);
		Matrix m = new Matrix();
		m.setScale(maxTmpScale, maxTmpScale, tmpWidth, tmpHeight);
		sourceBitmap = Bitmap.createBitmap(sourceBitmap, 0, 0, width, height, m, false);
		// 切图
		int x = (tmpWidth - resetWidth) / 2;
		int y = (tmpHeight - resetHeight) / 2;
		return Bitmap.createBitmap(sourceBitmap, x, y, resetWidth, resetHeight);
	}
	
	/**
	 * 获取本地图片并指定高度和宽度
	 * 
	 * @param imagePath
	 * @return
	 */
	public static Bitmap getNativeImage(String imagePath) {
		BitmapFactory.Options options = new BitmapFactory.Options();
		options.inJustDecodeBounds = true;
		// 获取这个图片的宽和高
		Bitmap myBitmap = BitmapFactory.decodeFile(imagePath, options); // 此时返回myBitmap为空
		// 计算缩放比
		int be = (int) (options.outHeight / (float) 200);
		int ys = options.outHeight % 200;// 求余数
		float fe = ys / (float) 200;
		if (fe >= 0.5)
			be = be + 1;
		if (be <= 0)
			be = 1;
		options.inSampleSize = be;
		// 重新读入图片，注意这次要把options.inJustDecodeBounds 设为 false
		options.inJustDecodeBounds = false;
		myBitmap = BitmapFactory.decodeFile(imagePath, options);
		return myBitmap;
	}
	
	/**
	 * 代码创建一个selector 代码生成会清除padding
	 */
	public static Drawable CreateStateDrawable(Context context, int bulr,
			int focus) {
		Drawable bulrDrawable = context.getResources().getDrawable(bulr);
		Drawable focusDrawable = context.getResources().getDrawable(focus);
		StateListDrawable drawable = new StateListDrawable();
		drawable.addState(new int[] { android.R.attr.state_pressed },
				focusDrawable);
		drawable.addState(new int[] {}, bulrDrawable);
		return drawable;
	}
	
	/**
	 * 图片资源回收
	 * 
	 * @param bitmap
	 */
	public void distoryBitmap(Bitmap bitmap) {
		if (null != bitmap && bitmap.isRecycled()) {
			bitmap.recycle();
		}
	}
}
